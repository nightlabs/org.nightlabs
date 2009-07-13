/**
 * 
 */
package org.nightlabs.script;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.regex.Matcher;


/**
 * This parser takes the contents of a String as input
 * and interprets it as jshtml (inside-out javascript with html output).
 * <p>
 * This parser is a intended to be used only once (you pass the string 
 * to parse in the constructor).
 * </p>
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class JSHTMLParser {

	public static final char lt = '<';
	public static final char gt ='>';
	public static final char eq =  '=';
	public static final char qm = '?';
	private static final char qt = '"';
	private static final char lb = '\n';

	/** Greater then <  */
	private static final int EXPECT_GT = -2;
	/** Question mark ?  */
	private static final int EXPECT_QM = -3;
	/** Equals =  */
	private static final int EXPECT_EQ = -4;
	/** Normal  */
	private static final int NORMAL = 0;

	private String textPartContent;
	private StringBuilder buffer;
	private StringBuilder nextJS;
	private StringBuilder nextTxt;

	private boolean nextJSSingleReturn;

	private final String printObject;
	private final String printMethod;

	/**
	 * Create a new {@link JSHTMLParser}.
	 * 
	 * @param script The script to parse
	 * @param printObjectName 
	 * 		The name of the object the resulting script uses as output 
	 * 		(e.g. 'Packages.java.lang.System.out' or some other object you introduce in the script).
	 * @param printMethodName 
	 * 		The name of the method the resulting script should invoke on printObjectName.
	 * 		For example if you used 'Packages.java.lang.System.out' as printObjectName you might
	 * 		use 'print' as printMethodName.
	 */
	public JSHTMLParser(String script, String printObjectName, String printMethodName) {
		this.textPartContent = script;
		this.printObject = printObjectName;
		this.printMethod = printMethodName;
	}

	public JSHTMLParser(String script, String printMethodName) {
		this(script,"",printMethodName);
	}


	/**
	 * Use this method to access the script resulting from {@link #parse()}.
	 * 
	 * @return The result script.
	 */
	public String getParsedText() {
		return buffer.toString();
	}


	public String getTextPartContent() {
		return textPartContent;
	}

	public void setTextPartContent(String textPartContent) {
		this.textPartContent = textPartContent;
	}

	/**
	 * Parse the String given in the constructor.
	 * The result script can be accessed after
	 * this method returned by {@link #getParsedText()}.
	 */
	public synchronized void parse() {
		StringReader reader = new StringReader(textPartContent);
		StreamTokenizer stk = new StreamTokenizer(reader);
		try {
			stk.resetSyntax();
			stk.wordChars(0, Integer.MAX_VALUE);
			stk.ordinaryChar(lt);
			stk.ordinaryChar(gt);
			stk.ordinaryChar(eq);
			stk.ordinaryChar(qm);
			stk.ordinaryChar(qt);
			stk.ordinaryChar(lb);
			boolean inString = false;
			boolean inJS = false;
			buffer = new StringBuilder();
			nextJS = new StringBuilder();
			nextTxt = new StringBuilder();

			int expectedToken = NORMAL;
			char previousOrdinary = 0;

			while (stk.nextToken() != StreamTokenizer.TT_EOF) {
				if (stk.ttype == StreamTokenizer.TT_EOL) {
					if (inJS) {						
						writeJS();
					}
					else {
						writeTxt();
					}					
				} else if (stk.ttype == StreamTokenizer.TT_WORD) {
					if (expectedToken < 0 && expectedToken != EXPECT_EQ) {
						// if we expect something but get a word
						// we add the last ordinary char
						// eq is excluded here as because that's only expected
						// when js-intializatinon finished, therefore if it does 
						// not follow right away we don't want to append the initialization
						if (inJS) {
							nextJS.append(previousOrdinary);
							nextJS.append(stk.sval);
						}
						else {
							nextTxt.append(previousOrdinary);
							nextTxt.append(stk.sval);
						}

					} else {
						// For a word where we expect normal we add
						// the token to the current type and reset
						// the buffers.
						if (inJS) {
							nextJS.append(stk.sval);
						} else {
							nextTxt.append(stk.sval);
						}
					}
					expectedToken = NORMAL;
				} else {
					// ordinary chars, meaning the separators for us <?>="
					if (inJS) {
						if (stk.ttype == qm && !inString) {
							expectedToken = EXPECT_GT;							
						} else if (stk.ttype == gt && expectedToken == EXPECT_GT) {
							writeJS();
							inJS = false;
							expectedToken = NORMAL;
						} else if (stk.ttype == qt) {
							nextJS.append(qt);
							inString = !inString;
						} else if (stk.ttype == eq && expectedToken == EXPECT_EQ) {
							expectedToken = NORMAL;
							nextJSSingleReturn = true; 
						} else {
							nextJS.append((char)stk.ttype);
							expectedToken = NORMAL;
						}
					}
					else {
						if (expectedToken == NORMAL && stk.ttype == lt) {
							expectedToken = EXPECT_QM;
						} else if (stk.ttype == EXPECT_QM && inJS) {
							expectedToken = EXPECT_GT;
						} else if (expectedToken == EXPECT_QM) {
							if (stk.ttype == qm) {
								inJS = true;
								writeTxt();
								expectedToken = EXPECT_EQ;
							} else {
								nextTxt.append(lt);
								nextTxt.append(stk.ttype);
								expectedToken = NORMAL;
							}
						} else {
							nextTxt.append((char)stk.ttype);
							expectedToken = NORMAL;
						}
					}
					previousOrdinary = (char) stk.ttype; 
				}
			}
			if (nextTxt.length() != 0)
				writeTxt();
			else if (nextJS.length() != 0)
				writeJS();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			reader.close();
		}
	}

	private void writeJS() {
		if (nextJSSingleReturn) {
			// if the object name is omitted then dont include it
			if(printObject.isEmpty())
				buffer.append(printMethod + "(" + nextJS.toString() + ");\n");
			else
				buffer.append(printObject + "." + printMethod + "(" + nextJS.toString() + ");\n");
			nextJSSingleReturn = false;
		} else {
			buffer.append(nextJS.toString() + "\n");
		}
		nextJS.setLength(0);
	}

	private void writeTxt() {
		// if the object name is omitted then dont include it
		if(printObject.isEmpty())
			buffer.append(printMethod + "(\"" + nextTxt.toString() + "\");\n");
		else
			buffer.append(printObject + "." + printMethod + "(\"" + nextTxt.toString() + "\");\n");
		nextTxt.setLength(0);
	}

	protected static String escapeEvalString(String strToEval, int level, boolean convertStringLineBreaks) {
		String escaped = strToEval;
		for (int i = 0; i < level; i++) {
			escaped = escaped.replaceAll("\\\\", Matcher.quoteReplacement("\\\\"));
			escaped = escaped.replaceAll("\"", Matcher.quoteReplacement("\\\""));
		}
		if (convertStringLineBreaks) {
			escaped = escaped.replaceAll("\\n", Matcher.quoteReplacement("\" + \n\""));
		}
		return escaped;
	}
}

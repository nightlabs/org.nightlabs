package org.nightlabs.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.apache.log4j.Logger;

/**
 * an open implemtation of Java scripting Library, This parser takes the contents of a Text as input
 * and interprets the java script and returns the result.
 * 
 * @author Fitas Amine <!-- fitas [AT] nightlabs [DOT] de -->
 */

public class ScriptParser {
	
	private static final long serialVersionUID = 1L;

	private static final String OPENINGTAG = "<SCRIPT";

	private static final String CLOSINGTAG = "</SCRIPT";	

	private static final char  CLOSECHAR='>';	

	private String textPartContent;
	private ScriptEngine scriptEngine;
	private StringWriter scriptOut;
	private ScriptContext newContext;

	public ScriptParser(String textPartContent) {
		this(textPartContent,"JavaScript");
	}

	public ScriptParser(String textPartContent,String scriptLanguage) {
		super();
		// create a script engine manager
		ScriptEngineManager factory = new ScriptEngineManager();
		// create a JavaScript engine
		this.scriptEngine = factory.getEngineByName(scriptLanguage);

		this.scriptOut = new StringWriter();
		this.newContext = new SimpleScriptContext();
		newContext.setWriter(new PrintWriter(scriptOut));

		this.textPartContent = textPartContent;
	}

	/**
	 * checks the content text if it contains a valid scripting tags
	 * 
	 * @return The number of scripting blocks found inside the content text.
	 */
	public int containsValidScript()
	{
		//"(.*?)\\<SCRIPT\\s*+>(.*?)</SCRIPT\\s*+>(.*?)";  
		String regexStr = "(.*?)\\" + OPENINGTAG + "\\s*+" + CLOSECHAR + "(.*?)"  + CLOSINGTAG + "\\s*+" + CLOSECHAR + "(.*?)";	
		int groupCounts = 0;
		try
		{	
			Pattern p = Pattern.compile(regexStr);
			Matcher  matcher = p.matcher(textPartContent);
			while (matcher.find()) {
				groupCounts++;
			}
		}
		catch(PatternSyntaxException e)
		{
			logger.info("Pattern Syntax:"+ e.getDescription());
			groupCounts = -1;
		}
		return groupCounts;
	}

	/**
	 * exposes the Object reference to the scripting code
	 * 
	 * @param name The name of the object to the script.
	 * @param object The object to expose. 
	 * @return The number of scripting blocks found inside the content text.
	 */
	public void Expose(String name, Object object)
	{
		scriptEngine.put(name,object); 
	}
	
	/**
	 * executes the block of text and script and returns the whole text with the result of the execution
	 * 
	 * @return The result Text of the execution.
	 */
	public StringBuilder execute() throws ScriptException
	{
		String regexStr = "\\" + OPENINGTAG + "\\s*+" + CLOSECHAR + "(.*?)"  + CLOSINGTAG + "\\s*+" + CLOSECHAR;	
		Pattern p = Pattern.compile(regexStr);
		Matcher matcher = p.matcher(textPartContent);
		StringBuilder nextJS = new StringBuilder();
		String str;
		int strInd=0;
		int endInd = 0;

		if(!(containsValidScript()>0))
			return null;

		while (matcher.find()) {

			endInd = matcher.start();    	
			str = textPartContent.substring(strInd, endInd); 
			nextJS.append(str);

			str = eval(matcher.group());	
			if(str!= null)
				nextJS.append(str);
			else
				return null; // script Error

			strInd = matcher.start() + matcher.group().length();

		}
		// if they are any left text beyond the script tag
		if(textPartContent.length() >strInd)
		{
			str = textPartContent.substring(strInd, textPartContent.length()); 
			nextJS.append(str);
		}	
		return nextJS;
	}
	/**
	 * executes a single statment within an open and close tag
	 * 
	 * @return The result Text of the execution.
	 */
	private String eval(String eval) throws ScriptException
	{
		// strips the eval script from all the tags
		String regexStrOpenTag = "\\" + OPENINGTAG + "\\s*+" + CLOSECHAR ;	
		String regexStrCloseTag = "\\" +  CLOSINGTAG + "\\s*+" + CLOSECHAR;	

		eval = eval.replaceAll(regexStrOpenTag, "");
		eval = eval.replaceAll(regexStrCloseTag, "");
		scriptEngine.eval(eval,newContext);
		return scriptOut.toString();	
	}

	/**
	 * sets the Text content of the scripting block.
	 */
	public void setText(String textPartContent)
	{
		this.textPartContent = textPartContent;	
	}


	private static final Logger logger = Logger.getLogger(ScriptParser.class);
}



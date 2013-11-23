package org.nightlabs.script;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.apache.log4j.Logger;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;

/**
 * an open implemtation of Java scripting Library, This parser takes the contents of a Text as input
 * and interprets the java script and returns the result.
 * 
 * @author Fitas Amine <!-- fitas [AT] nightlabs [DOT] de -->
 */

public class JSHTMLExecuter {

	private static final long serialVersionUID = 1L;
	private static final char OPENINGTAG = JSHTMLParser.lt;
	private static final char CLOSINGTAG = JSHTMLParser.gt;	
	private I18nTextBuffer textScriptContent;
	private ScriptEngine scriptEngine;
	private StringWriter scriptOut;
	private ScriptContext newContext;
	private JSHTMLParser parser;

	public JSHTMLExecuter() {
		this(new I18nTextBuffer(),"JavaScript");
	}

	public JSHTMLExecuter(I18nText textPartContent) {
		this(textPartContent,"JavaScript");
	}

	public JSHTMLExecuter(I18nText textPartContent,String scriptLanguage) {
		super();
		this.textScriptContent = (I18nTextBuffer) textPartContent;
		// create a script engine manager
		ScriptEngineManager factory = new ScriptEngineManager();
		// create a JavaScript engine
		this.scriptEngine = factory.getEngineByName(scriptLanguage);
		this.parser = new JSHTMLParser("", "print");	
		this.scriptOut = new StringWriter();
		this.newContext = new SimpleScriptContext();
		newContext.setWriter(new PrintWriter(scriptOut));
	}

	/**
	 * checks the content text if it contains a valid scripting tags
	 * 
	 * @return The number of scripting blocks found inside the content text.
	 */
	public int containsValidScript()
	{
		int doesContains = 0;
		for (Entry<String, String> entry : textScriptContent.getTexts())
			doesContains += containsValidScript(entry.getValue());

		return doesContains;
	}

	/**
	 * very usefull utility method to validate the contents of the scripts
	 * 
	 * @return null if the contained script does not contains any error or the error message.
	 */
	public String validateContent()
	{		
		try {
			execute();	
		} catch (ScriptException e) {				
			return e.getMessage();
		}
		return null;
	}	

	/**
	 * checks the content text if it contains a valid scripting tags
	 * this method doesnt validate the syntax it rather insures that a valid open 
	 * code bracket is included.
	 * 
	 * @return The number of scripting blocks tag found inside the content text.
	 */
	public int containsValidScript(String string)
	{
		// the special tag word used after the opening bracket 
		String tagWord = "["  + JSHTMLParser.qm + "]";
		String regexSingleStateStr = "(.*?)\\" + OPENINGTAG + "[" + JSHTMLParser.eq + "]" + "\\s*+"  + "(.*?)" + "[^" +JSHTMLParser.qm  +"]" + CLOSINGTAG + "\\s*+"  + "(.*?)";	
		String regexMultiStateStr ="(.*?)\\" + OPENINGTAG + tagWord  + "\\s*+"  + "(.*?)" + tagWord  + CLOSINGTAG + "\\s*+"  + "(.*?)";	
		int groupCounts = 0;			
		groupCounts =findStatements(string,regexSingleStateStr);
		groupCounts +=findStatements(string,regexMultiStateStr);
		return groupCounts;
	}
	
	private int findStatements(String string, String regularExpr)
	{	
		int groupCounts = 0;	
		if (string == null)
			return 0;
		try
		{	
			Pattern p = Pattern.compile(regularExpr,Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
			Matcher  matcher = p.matcher(string);
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

	private String execute(String script) throws ScriptException
	{
		scriptOut.getBuffer().setLength(0);// empty the previous buffer from any content
		parser.setTextPartContent(script);
		parser.parse();
		String result = parser.getParsedText();
		scriptEngine.eval(result,newContext);
		return scriptOut.toString();	
	}


	public I18nTextBuffer execute() throws ScriptException
	{
		I18nTextBuffer buffer = new I18nTextBuffer();
		for (Entry<String, String> entry : textScriptContent.getTexts())
			buffer.setText(entry.getKey(), execute(entry.getValue()));
		return buffer;
	}


	/**
	 * sets the Text content of the scripting block.
	 */
	public I18nText getTextScriptContent()
	{
		return this.textScriptContent;	
	}


	private static final Logger logger = Logger.getLogger(JSHTMLExecuter.class);
}



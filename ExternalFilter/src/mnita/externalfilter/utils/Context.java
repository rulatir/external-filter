/**
 * 
 */
package mnita.externalfilter.utils;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern; 

/**
 * @author Szczepan Hołyszewski
 *
 */
public final class Context {
	
	private AbstractDecoratedTextEditor editor = null;
	String name;
	
	public Context(IWorkbenchWindow window) {
	
		IEditorPart part = window.getActivePage().getActiveEditor();
		if (part instanceof AbstractDecoratedTextEditor) {
			
			editor = (AbstractDecoratedTextEditor) part;
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IPathEditorInput) {
				
				name = ((IPathEditorInput) input).getPath().toString();
			}
		}
	}
	
	public String shellQuote(String input) {
		
		return "\"" + input
				.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("!", "\\!")
				.replace("$", "\\$") + "\"";
	}
	
	
	public String replacementQuote(String input) {
		
		return input
				.replace("\\", "\\\\")
				.replace("$", "\\$");
	}

	public String preprocessCommand(String command) {
		
		Matcher matcher = Pattern.compile("\\$\\{([_A-Za-z][_0-9A-Za-z]*)\\}").matcher(command);
		
		StringBuffer buffer = new StringBuffer();
		while(matcher.find()) {
		
			MatchResult result = matcher.toMatchResult();
			String replacement;
			String key = result.group(1);
			switch(key) {
			
				case "__FILE__": 	replacement = name; break;
				default: 			replacement = result.group(0); break;
			}
			matcher.appendReplacement(buffer,shellQuote(replacementQuote(replacement)));
		}
		matcher.appendTail(buffer);
		String ret = buffer.toString();
		return ret;
	}
}
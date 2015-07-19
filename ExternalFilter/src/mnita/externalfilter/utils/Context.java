/**
 * 
 */
package mnita.externalfilter.utils;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import java.util.HashMap;
import java.util.Map;
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

	public String preprocessCommand(String command) {
		
		Matcher matcher = Pattern.compile("\\$\\(([a-z][_a-z]*)\\)").matcher(command);
		
		StringBuffer buffer = new StringBuffer();
		
		final Map<String, String> map = new HashMap<String, String>();
		map.put("selected_resource_loc", name);
		
		while(matcher.find()) {
		
			MatchResult result = matcher.toMatchResult();
			matcher.appendReplacement(
					
				buffer,
				map.containsKey(result.group(1)) ? map.get(result.group(1)) : result.group(0)
			);
		}
		matcher.appendTail(buffer);
		String ret = buffer.toString();
		return ret;
	}
}
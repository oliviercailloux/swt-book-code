/*
 * Copyright (c) 2004 Steve Northover and Mike Wilson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 */
package part2;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import java.io.*;

public class RememberFont {

public static void main(String[] args) {
    final Display display = new Display();
	final Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    Button b = new Button(shell, SWT.PUSH);
    b.setText("Choose Font");
    b.addSelectionListener(
    	new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FontData fd = chooseFont(shell);
				if (fd != null)
					System.out.println("FontData: " + fd);
			}
    	});
    shell.setText("Remember Font");
    shell.setSize(200, 100);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    display.dispose();
}

// IMPORTANT: 
//    This method uses the *old* style setter for 
//    the starting FontData for the dialog. See
//    class part2.ShowFont for a better implementation.
public static FontData chooseFont(Shell parent) {
	FontData fd;
	String fdString = null;
	FontDialog dialog = new FontDialog(parent);
	final String fileName = "the-font";
	
	// Get FontData string from file if it exists
	try {
		FileReader fr = new FileReader(fileName);
		StringBuffer buf = new StringBuffer();
		for (int ch = fr.read(); ch >= 0; ch = fr.read())
			buf.append((char) ch);
		fr.close();
		fdString = buf.toString();
	} catch (IOException e) { }
	if (fdString != null)
		dialog.setFontData(new FontData(fdString));
		
	fd = dialog.open();
	
	// If a font was chosen, save the FontData string.
	if (fd != null) {
		FileWriter ofw;
		try {
			ofw = new FileWriter(fileName);
			ofw.write(fd.toString());
			ofw.close();
		} catch (IOException e1) { }
	}
	
	return fd;
}}

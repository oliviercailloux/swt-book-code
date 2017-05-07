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
import java.util.*;

public class ShowFont {

	static FontData[] fd;
	static Display display;
	static Shell shell;
	static Button fontButton;
	static Canvas drawingArea;
	
public static void main(String[] args) {
    display = new Display();
	shell = new Shell(display);
	GridLayout layout = new GridLayout();
	layout.numColumns = 1;
    shell.setLayout(layout);
    fontButton = new Button(shell, SWT.PUSH);
	fontButton.setText("Choose Font");
	fontButton.addSelectionListener(
    	new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fd = chooseFont(shell);
				drawingArea.redraw();
			}
    	});
    fontButton.setLayoutData(new GridData());
    drawingArea = new Canvas(shell, SWT.BORDER);
	drawingArea.addListener(SWT.Paint, new Listener() {
		public void handleEvent(Event event) {
			if (fd != null) {
				GC gc = event.gc;
				Font font = new Font(display, fd);
				gc.setFont(font);
				gc.drawString("This is font " + fd[0].getName(), 0, 0);
				font.dispose();
			}
		}});
	drawingArea.setLayoutData(
		new GridData(GridData.FILL_BOTH));
    shell.setText("Show Font");
    shell.setSize(200, 100);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    display.dispose();
}

public static FontData[] chooseFont(Shell parent) {
	FontData fd[];
	FontDialog dialog = new FontDialog(parent);
	final String fileName = "the-font";
	
	// Get FontData strings from file if it exists
	ArrayList inputFDs = new ArrayList(10);
	try {
		BufferedReader reader = 
			new BufferedReader(
				new FileReader(fileName));
		String line = reader.readLine(); 
		while (line != null) { 
			inputFDs.add(new FontData(line));
			line = reader.readLine();
		}
		reader.close();
	} catch (IOException e) { }
	if (inputFDs.size() > 0) {
		fd = new FontData[inputFDs.size()];
		inputFDs.toArray(fd);
		dialog.setFontList(fd);
	}
		
	dialog.open();
	fd = dialog.getFontList();
	
	// If a font was chosen, save the FontData strings.
	if (fd != null) {
		try {
			BufferedWriter writer = 
				new BufferedWriter(
					new FileWriter(fileName));
			for (int i=0; i < fd.length; ++i) {
				writer.write(fd[i].toString()); 
				writer.newLine();
			}
			writer.close();
		} catch (IOException e1) { }
	}
	
	return fd;
}}

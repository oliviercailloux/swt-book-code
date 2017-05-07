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
package part1.ch15;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class ForceWrap3 {
	
public static void main(String[] args) {
	Display display = new Display();
	final Shell shell = new Shell(display);
	int barStyle = SWT.FLAT | SWT.WRAP;
	final ToolBar toolBar = new ToolBar(shell, barStyle);
	for (int i=0; i<12; i++) {
		ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setText("Item " + i);
	}
	int style = SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL;
	Text text = new Text(shell, style);
	FormLayout layout = new FormLayout();
	shell.setLayout(layout);
	final FormData toolData = new FormData();
	toolData.top = new FormAttachment(0);
	toolData.left = new FormAttachment(0);
	toolData.right = new FormAttachment(100);
	toolBar.setLayoutData(toolData);
	FormData textData = new FormData();
	textData.top = new FormAttachment(toolBar);
	textData.bottom = new FormAttachment(100);
	textData.left = new FormAttachment(0);
	textData.right = new FormAttachment(100);
	text.setLayoutData(textData);
	shell.addListener(SWT.Resize, new Listener() {
		public void handleEvent(Event e) {
			Rectangle rect = shell.getClientArea();
			toolData.width = rect.width;
		}
	});
	text.setFocus();
	shell.pack();
	shell.open();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) display.sleep();
	}
	display.dispose();
}
}
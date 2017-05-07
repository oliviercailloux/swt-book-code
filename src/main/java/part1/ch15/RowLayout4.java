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
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class RowLayout4 {
	
public static void main(String[] args) {
	Display display = new Display();
	Shell shell = new Shell(display, SWT.DIALOG_TRIM);
	RowLayout layout1 = new RowLayout();
	layout1.type = SWT.VERTICAL;
	layout1.fill = true;
	layout1.spacing = 10;
	layout1.marginWidth = layout1.marginHeight = 10;
	shell.setLayout(layout1);
	Label label = new Label (shell, SWT.NONE);
	label.setText ("This is a very simple MessageBox.");
	Composite composite = new Composite (shell, SWT.NONE);
	RowLayout layout2 = new RowLayout();
	layout2.pack = false;
	layout2.justify = true;
	layout2.marginWidth = layout2.marginHeight = 0;
	composite.setLayout(layout2);
	Button okButton = new Button(composite, SWT.PUSH);
	okButton.setText("OK");
	Button cancelButton = new Button(composite, SWT.PUSH);
	cancelButton.setText("Cancel");
	composite.pack();
	shell.pack();
	shell.open();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) display.sleep();
	}
	display.dispose();
}

}

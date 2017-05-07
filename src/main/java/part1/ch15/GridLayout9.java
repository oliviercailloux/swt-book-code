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

public class GridLayout9 {
	
public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    shell.setLayout(layout);
    Label label = new Label(shell, SWT.NONE);
    label.setText("This is a very simple MessageBox.");
    GridData labelData = new GridData();
    labelData.horizontalSpan = 2;
    label.setLayoutData(labelData);
    Button okButton = new Button(shell, SWT.PUSH);
    okButton.setText("OK");
    GridData okData =
        new GridData(SWT.CENTER, SWT.CENTER, true, true);
    okButton.setLayoutData(okData);
    Button cancelButton = new Button(shell, SWT.PUSH);
    cancelButton.setText("Cancel");
    GridData cancelData =
        new GridData(SWT.CENTER, SWT.CENTER, true, true);
    cancelButton.setLayoutData(cancelData);
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}

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

public class FindDialog1 {
	
public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setText("Find (GridLayout)");
    Label label = new Label(shell, SWT.NONE);
    label.setText("Find what:");
    Text text = new Text(shell, SWT.BORDER);
    Button findButton = new Button(shell, SWT.PUSH);
    findButton.setText("Find Next");
    Group group = new Group(shell, SWT.NONE);
    group.setLayout(new RowLayout());
    Button upButton = new Button(group, SWT.RADIO);
    upButton.setText("Up");
    Button downButton = new Button(group, SWT.RADIO);
    downButton.setText("Down");
    downButton.setSelection(true);
    group.setText("Direction");
    Button cancelButton = new Button(shell, SWT.PUSH);
    cancelButton.setText("Cancel");
    
    /* Use a GridLayout to position the controls */
    Monitor monitor = shell.getMonitor();
    int width = monitor.getClientArea().width / 7;
    GridLayout layout = new GridLayout(4, false);
    layout.marginWidth = layout.marginHeight = 9;
    shell.setLayout(layout);
    GridData labelData =
        new GridData(SWT.FILL, SWT.CENTER, false, false);
    label.setLayoutData(labelData);
    GridData textData =
        new GridData(SWT.FILL,SWT.CENTER,true,false,2,1);
    textData.widthHint = width;
    text.setLayoutData(textData);
    GridData findData =
        new GridData(SWT.FILL, SWT.CENTER, false, false);
    findButton.setLayoutData(findData);
    GridData groupData =
        new GridData(SWT.RIGHT,SWT.TOP,false,false,3,1);
    group.setLayoutData(groupData);
    GridData cancelData =
        new GridData(SWT.FILL, SWT.TOP, false, false);
    cancelButton.setLayoutData(cancelData);
    
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}
}

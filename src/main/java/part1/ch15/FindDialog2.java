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

public class FindDialog2 {
	
public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setText("Find (FormLayout)");
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
    
    /* Use a FormLayout to position the controls */
    Monitor monitor = shell.getMonitor();
    int width = monitor.getClientArea().width / 7;
    FormLayout layout = new FormLayout();
    layout.spacing = 5;
    layout.marginWidth = layout.marginHeight = 9;
    shell.setLayout(layout);
    FormData labelData = new FormData();
    labelData.left = new FormAttachment(0);
    labelData.top = new FormAttachment(text, 0,SWT.CENTER);
    label.setLayoutData(labelData);
    FormData textData = new FormData(width, SWT.DEFAULT);
    textData.top = new FormAttachment(0);
    textData.left = new FormAttachment(label);
    textData.right = new FormAttachment(findButton);
    text.setLayoutData(textData);
    FormData findData = new FormData();
    findData.right = new FormAttachment(100);
    findData.top = new FormAttachment(text, 0, SWT.CENTER);
    findButton.setLayoutData(findData);
    FormData groupData = new FormData();
    groupData.right = new FormAttachment(findButton);
    groupData.top = new FormAttachment(text);
    group.setLayoutData(groupData);
    FormData cancelData = new FormData();
    cancelData.left = new FormAttachment(group);
    cancelData.right = new FormAttachment(100);
    cancelData.top = new FormAttachment(findButton);
    cancelButton.setLayoutData(cancelData);
    
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}
}

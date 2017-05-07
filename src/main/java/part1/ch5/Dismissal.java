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
package part1.ch5;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class Dismissal {
    
public static void main(String[] args) {
    Display display = new Display ();
    final Shell dialog = new Shell (SWT.DIALOG_TRIM);
    Label label = new Label (dialog, SWT.NONE);
    label.setText ("Exit the application?");
    Button b1 = new Button(dialog, SWT.PUSH);
    Button b2 = new Button(dialog, SWT.PUSH);
    final Button okButton, cancelButton;
    if (display.getDismissalAlignment() == SWT.LEFT) {
        okButton = b1;
        cancelButton = b2;
    } else {
        cancelButton = b1;
        okButton = b2;
    }
    okButton.setText("Ok");
    cancelButton.setText("Cancel");
    
    final boolean [] result = new  boolean [1];
    Listener listener = new Listener () {
        public void handleEvent (Event event) {
            result [0] = event.widget == okButton;
            dialog.dispose ();
        }
    };
    okButton.addListener (SWT.Selection, listener);
    cancelButton.addListener (SWT.Selection, listener);
    
    FormLayout form = new FormLayout ();
    form.marginWidth = form.marginHeight = 8;
    dialog.setLayout (form);
    FormData okData = new FormData ();
    okData.top = new FormAttachment (label, 8);
    okButton.setLayoutData (okData);
    FormData cancelData = new FormData ();
    cancelData.left = new FormAttachment (okButton, 8);
    cancelData.top = new FormAttachment (okButton, 0, SWT.TOP);
    cancelButton.setLayoutData (cancelData);
    
    cancelButton.pack ();
    cancelData.width = cancelButton.getSize().x;
    okData.width = cancelData.width;
    dialog.setDefaultButton (okButton);
    dialog.pack ();
//    dialog.setSize(200, 200);
    dialog.open ();
    
    while (!dialog.isDisposed ()) {
        if (!display.readAndDispatch ()) display.sleep ();
    }
    System.out.println ("Dialog result: " + result [0]);
    display.dispose ();
}

public static void main2(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    RowLayout layout = new RowLayout ();
    layout.justify = true;
    layout.pack = false;
    shell.setLayout(layout);
    Button b1 = new Button(shell, SWT.PUSH);
    Button b2 = new Button(shell, SWT.PUSH);
    Button okButton = null, cancelButton = null;
    if (display.getDismissalAlignment() == SWT.LEFT) {
        okButton = b1;
        cancelButton = b2;
    } else {
        cancelButton = b1;
        okButton = b2;
    }
    okButton.setText("Ok");
    cancelButton.setText("Cancel");
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    display.dispose();
}

}
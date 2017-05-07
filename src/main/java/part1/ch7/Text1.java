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
package part1.ch7;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class Text1 {

public static void main(String[] args) {
    Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setText("ModifyExample");
    shell.setLayout(new FillLayout());
	int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER;
    Text text = new Text(shell, style);
    final boolean [] modified = new boolean [1];
    text.addListener(SWT.Modify, new Listener() {
        public void handleEvent(Event e) {
            modified[0] = true;
        }
    });
    shell.addListener(SWT.Close, new Listener() {
        public void handleEvent(Event e) {
            if (!modified[0]) return;
            int style = SWT.PRIMARY_MODAL | 
            	SWT.YES | SWT.NO | SWT.CANCEL;
            MessageBox box = new MessageBox(shell, style);
            box.setText(shell.getText());
            box.setMessage("Save changes?");
            switch (box.open()) {
                case SWT.YES:
                	System.out.println ("Saving ...");
                    // if (!saveText()) break;
                    //FALL THROUGH 
                case SWT.NO :
                    break;
                case SWT.CANCEL :
                    e.doit = false;
                    break;
            }
        }
    });
    text.setText("Initial text.");
    modified[0] = false;
    shell.setSize(200, 200);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}

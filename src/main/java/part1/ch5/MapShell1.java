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
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class MapShell1 {

public static void main(String[] args) {
    final Display display = new Display();
    int style = SWT.SHELL_TRIM | SWT.RIGHT_TO_LEFT;
    final Shell shell = new Shell(display, style);
    final Button button = new Button(shell, SWT.PUSH);
    button.setText("Button");
    button.pack();
    button.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event event) {
            Shell dialog = new Shell(shell, SWT.ON_TOP);
            Rectangle rect = button.getBounds();
            // WRONG - Transforms the top corner of the
            // button (which is in the upper right) to
            // the coordinate system of the Display
            // which gives a point in the upper right
            // but in Display coordinates
            Point pt = shell.toDisplay (rect.x, rect.y);
            pt.y += rect.height;
            dialog.setBounds(pt.x, pt.y, rect.width, 200);
            display.beep();
            dialog.setVisible(true);
        }
    });
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}

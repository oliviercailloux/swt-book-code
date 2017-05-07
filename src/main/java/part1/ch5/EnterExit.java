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

import org.eclipse.swt.widgets.*;

public class EnterExit {

public static void main(String[] args) {
    //NOTE - Use SWT.MouseEnter and SWT.MouseExit instead
    final Display display = new Display();
    final Control[] last = new Control[1];
    display.timerExec(100, new Runnable() {
        public void run() {
            Control control = display.getCursorControl();
            if (control != last[0]) {
                if (last[0] != null
                    && !last[0].isDisposed()) {
                    System.out.println("Exit:" + last[0]);
                }
                if (control != null
                    && !control.isDisposed()) {
                    System.out.println("Enter:" + control);
                }
            }
            last[0] = control;
            display.timerExec(100, this);
        }
    });
    Shell shell = new Shell(display);
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}
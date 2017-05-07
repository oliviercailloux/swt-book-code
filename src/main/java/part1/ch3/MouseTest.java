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
package part1.ch3;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class MouseTest {

public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    Listener mouseListener = new Listener() {
        public void handleEvent(Event e) {
            String string = "UNKNOWN";
            switch (e.type) {
                case SWT.MouseDown: string = "DOWN"; break;
                case SWT.MouseUp: string = "UP"; break;
                case SWT.MouseMove: string = "MOVE"; break;
                case SWT.MouseDoubleClick:
                    string = "DOUBLE";
                    break;
                case SWT.MouseEnter: string="ENTER"; break;
                case SWT.MouseExit: string = "EXIT"; break;
                case SWT.MouseHover: string="HOVER"; break;
            }
            string += ": stateMask=0x"
                + Integer.toHexString(e.stateMask);
            if ((e.stateMask & SWT.CTRL) != 0)
                string += " CTRL";
            if ((e.stateMask & SWT.ALT) != 0)
                string += " ALT";
            if ((e.stateMask & SWT.SHIFT) != 0)
                string += " SHIFT";
            if ((e.stateMask & SWT.COMMAND) != 0)
                string += " COMMAND";
            if ((e.stateMask & SWT.BUTTON1) != 0)
                string += " BUTTON1";
            if ((e.stateMask & SWT.BUTTON2) != 0)
                string += " BUTTON2";
            if ((e.stateMask & SWT.BUTTON3) != 0)
                string += " BUTTON3";
            string += ", button=0x"
                + Integer.toHexString(e.button);
            string += ", x=" + e.x + ", y=" + e.y;
            System.out.println(string);
        }
    };
    shell.addListener(SWT.MouseDown, mouseListener);
    shell.addListener(SWT.MouseUp, mouseListener);
    shell.addListener(SWT.MouseMove, mouseListener);
    shell.addListener(SWT.MouseDoubleClick, mouseListener);
    shell.addListener(SWT.MouseEnter, mouseListener);
    shell.addListener(SWT.MouseExit, mouseListener);
    shell.addListener(SWT.MouseHover, mouseListener);
    shell.setSize(200, 200);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}
}

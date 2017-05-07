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
package part1.ch2;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class KeyTest {

public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    Listener listener = new Listener() {
        public void handleEvent(Event event) {
            String string =
                event.type == SWT.KeyDown ? "DOWN": "UP  ";
            string += ": stateMask=0x"
                + Integer.toHexString(event.stateMask);
            if ((event.stateMask & SWT.CTRL) != 0)
                string += " CTRL";
            if ((event.stateMask & SWT.ALT) != 0)
                string += " ALT";
            if ((event.stateMask & SWT.SHIFT) != 0)
                string += " SHIFT";
            if ((event.stateMask & SWT.COMMAND) != 0)
                string += " COMMAND";
            string += ", keyCode=0x"
                + Integer.toHexString(event.keyCode);
            string += ", character=0x"
                + Integer.toHexString(event.character);
            switch (event.character) {
                case 0: string += " '\\0'"; break;
                case SWT.BS: string += " '\\b'"; break;
                case SWT.CR: string += " '\\r'"; break;
                case SWT.DEL: string += " DEL"; break;
                case SWT.ESC: string += " ESC"; break;
                case SWT.LF: string += " '\\n'"; break;
                case SWT.TAB: string += " '\\t'";
                    break;
                default:
                    string += " '" + event.character + "'";
                    break;
            }
            System.out.println(string);
        }
    };
    shell.addListener(SWT.KeyDown, listener);
    shell.addListener(SWT.KeyUp, listener);
    shell.setSize(200, 200);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}
}

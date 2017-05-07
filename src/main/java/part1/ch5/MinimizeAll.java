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

public class MinimizeAll {

public static void main(String[] args) {
    Display display = new Display();
    for (int i=0; i<4; i++) {
        Shell shell = new Shell(display);
        shell.setText("Shell");
        shell.setSize(200, 100);
        shell.open();
        Shell dialog = new Shell(shell);
        dialog.setText("Dialog");
        dialog.setSize(200, 100);
        dialog.open ();
    }
    Shell shell = display.getActiveShell ();
    if (shell != null) {
        while (shell.getParent () != null) {
            shell = shell.getParent ().getShell ();
        }
    }
    if (shell != null) {
        Shell [] shells = display.getShells ();
        for (int i=0; i<shells.length; i++) {
            if (shells [i].getParent () == null) {
                if (shells [i] != shell) {
                    shells [i].setMinimized (true);
                } 
            }
        }
    }
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    display.dispose();
}

}
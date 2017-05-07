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

public class Wake3 {

public static void main(String[] args) {
    final Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setSize(500, 64);
    shell.open();
    final boolean[] done = new boolean[1];
    final boolean[] reporting=new boolean[1];
    final StringBuffer title=new StringBuffer("Running ");
    new Thread() {
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(500);
                } catch (Throwable th) {
                }
                title.append(".");
                if (reporting[0]) continue;
            	reporting[0] = true;
                display.asyncExec(new Runnable() {
                    public void run() {
                        if (shell.isDisposed()) return;
                        shell.setText(title.toString());
                        reporting[0] = false;
                    }
                });
            }
            done[0] = true;
            // wake the user-interface thread from sleep
            display.wake();
        }
    }
    .start();
    shell.setText(title.toString());
    while (!done[0]) {
        if (!display.readAndDispatch()) display.sleep();
    }
    if (!shell.isDisposed()) {
    	title.append(" done.");
        shell.setText(title.toString());
        try {
            Thread.sleep(500);
        } catch (Throwable th) {
        }
    }
    display.dispose();
}

}
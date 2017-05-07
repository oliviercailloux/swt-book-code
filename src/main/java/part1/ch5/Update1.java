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

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class Update1 {
   
Display display;
Shell shell1, shell2;
public void f1() {
    shell2.dispose();
    while (display.readAndDispatch());
    Rectangle rect = shell1.getBounds();
}
     
public static void main(String[] args) {
    Display display = new Display();
    Shell shell1 = new Shell(display);
    shell1.setText("shell1");
    shell1.setBounds(50, 50, 200, 100);
    shell1.open();
    Shell shell2 = new Shell(display);
    shell2.setText("shell2");
    shell2.setBounds(60, 60, 200, 100);
    shell2.open();
    shell2.dispose();
    System.out.println("Waiting ... shell1 is not drawn.");
    try {Thread.sleep(5000);} catch (Throwable th) {};
    display.update();
    System.out.println("Waiting ... shell1 is drawn.");
    try {Thread.sleep(5000);} catch (Throwable th) {};
    System.out.println("Running the event loop.");
    while (!shell1.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}
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
package part1.ch11;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class Composite1 {

static void traverse(Control control, int level) {
    for (int i = 0; i < level; i++) {
    	System.out.print("\t");
    }
    System.out.println(control);
    if (!(control instanceof Composite)) return;
    Composite composite = (Composite) control;
    Control[] children = composite.getChildren();
    for (int i = 0; i < children.length; i++) {
        traverse(children[i], level + 1);
    }
}

public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    for (int i = 0; i < 2; i++) {
        Composite c1 = new Composite(shell, SWT.NULL);
        for (int j = 0; j < 3; j++) {
        	Composite c2 = new Composite(c1, SWT.NULL);
            for (int k = 0; k < 4; k++) {
        		Composite c3 = new Composite(c2, SWT.NULL);
            }
        }
    }
    shell.open();
    traverse(shell, 0);
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}
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
package part2;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;

/**
 * Displays each of the system cursors
 * 
 * @author mcq
 */

public class SystemCursors {

Display display;
Shell shell;

// Cursors
String[] cursorNames = new String[] {
  "CURSOR_ARROW", "CURSOR_WAIT", "CURSOR_CROSS", "CURSOR_APPSTARTING", "CURSOR_HELP",
  "CURSOR_SIZEALL", "CURSOR_SIZENESW", "CURSOR_SIZENS", "CURSOR_SIZENWSE", "CURSOR_SIZEWE",
  "CURSOR_SIZEN", "CURSOR_SIZES", "CURSOR_SIZEE", "CURSOR_SIZEW", "CURSOR_SIZENE", "CURSOR_SIZESE",
  "CURSOR_SIZESW", "CURSOR_SIZENW", "CURSOR_UPARROW", "CURSOR_IBEAM", "CURSOR_NO", "CURSOR_HAND"
};
Cursor[] cursors = new Cursor[cursorNames.length];

public static void main(String[] args) {
    new SystemCursors().run();
}

/**
 * Opens a shell and runs the event loop.
 * 
 * @author mcq
 */
void run() {
    display = new Display();
    getCursors();
    
    shell = new Shell(display, SWT.SHELL_TRIM);
    shell.setLayout(new FillLayout(SWT.VERTICAL));
    
    Label label;
    for (int i=0; i < cursorNames.length; ++i) {
        label = new Label(shell, SWT.BORDER);
        label.setText(cursorNames[i]);
        label.setCursor(cursors[i]);
   }    
    
    shell.pack();
    shell.setText("System Cursors");
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    disposeCursors();
    display.dispose();
}

/**
 * Set the cursors array based on the system cursor names.
 * Uses reflection for brevity if not for simplicity.
 *
 * @author mcq
 */
void getCursors() {
    Class swt = null;
    try {
        swt = Class.forName("org.eclipse.swt.SWT");
    } catch (ClassNotFoundException e) {
        System.err.println("Could not find class SWT. That's not possible");
        e.printStackTrace();
    }
    for (int i=0; i<cursorNames.length; ++i) {
        String name = cursorNames[i];
        int cursorNumber = 0;
        try {
            cursorNumber = swt.getDeclaredField(name).getInt(swt);
        } catch (Exception e1) {
            System.err.println("Could not get field " + name + ". That's not possible");
            e1.printStackTrace();
        }
        cursors[i] = new Cursor(display, cursorNumber);
    }
}

/**
 * Dispose of all the cursors we got. Should not be called until
 * after the shell has been disposed of.
 *
 * @author mcq
 */
void disposeCursors() {
    for (int i=0; i < cursorNames.length; ++i) {
        if (cursors[i] != null) {
            Cursor cursor = cursors[i];
            cursors[i] = null;
            cursor.dispose();
        }
    }
}

}

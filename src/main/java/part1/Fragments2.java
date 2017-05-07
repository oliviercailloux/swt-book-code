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
package part1;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class Fragments2 {

Display display;
Shell shell, shell1, shell2;
boolean done;
Font textFont;
Runnable timer;
GC gc;
Control control;

public void f1() {
display.addListener(SWT.Dispose, new Listener() {
    public void handleEvent(Event event) {
        // the display is getting disposed
    }
});
}

public void f1a() {
display.addFilter(SWT.KeyDown, new Listener() {
    public void handleEvent(Event event) {
        event.type = SWT.None;
        event.doit = false;
    }
});
}

public void f2() {
display.disposeExec(new Runnable() {
    public void run() {
        // dispose the shared font
        textFont.dispose();
    }
});
}

public void f3() {
while (!shell.isDisposed()) {
    if (!display.readAndDispatch()) display.sleep();
}
}

public void f3a() {
while (!done) display.readAndDispatch();
}

public void f3b() {
while (display.readAndDispatch());
}

public void f4() {
// this code is running in the event loop thread
while (!done) {
    if (!display.readAndDispatch()) display.sleep();
}
}

public void f5() {
// this code is not running in the event loop thread
done = true;
display.wake ();
}

public void f6() {
if (display.getThread() == Thread.currentThread()) {
    // current thread is the UI thread
}
}

public void f6a() {
display.timerExec(2000, new Runnable() {
    public void run() {
        System.out.println("Once, after 2 seconds.");
    }
});
}

public void f6b() {
display.timerExec(2000, new Runnable() {
    public void run() {
        System.out.println("Every 2 seconds.");
        display.timerExec(2000, this);
    }
});
}

public void f6c() {
display.timerExec(2000, timer);
display.timerExec(5000, timer);
}

public void f6d() {
display.timerExec (-1, timer);
}

public void f7() {
Monitor[] list = display.getMonitors();
System.out.println(list.length + " monitors.");
for (int i = 0; i < list.length; i++) {
    String string = "\t" + i + " - " + list[i].getBounds();
    System.out.println(string);
}
System.out.println("Total bounds: " + display.getBounds());
}

public void f7a() {
Shell shell = display.getActiveShell();
if (shell != null) shell.dispose();
}

public void f8() {
Shell shell = display.getActiveShell();
if (shell != null) {
    while (shell.getParent() != null) {
        shell = shell.getParent().getShell();
    }
}
if (shell != null) {
    Shell[] shells = display.getShells();
    for (int i = 0; i < shells.length; i++) {
        if (shells[i].getParent() == null) {
            if (shells[i] != shell) {
                shells[i].setMinimized(true);
            }
        }
    }
}
}

public void f9 () {
Control control = display.getFocusControl();
System.out.println("Focus control is " + control);
}

public void f9a () {
Point location = display.getCursorLocation();
}

public void f9b () {
Point dpi = display.getDPI ();
gc.drawRectangle (10, 10, dpi.x, dpi.y);
}

public void f10 () {
Color red = display.getSystemColor (SWT.COLOR_RED);
}

public void f11 () {
Font font = display.getSystemFont();
control.setFont(font);
}

public void f12 () {
Button b1 = new Button(shell, SWT.PUSH);
Button b2 = new Button(shell, SWT.PUSH);
Button okButton = null, cancelButton = null;
if (display.getDismissalAlignment() == SWT.LEFT) {
    okButton = b1;
    cancelButton = b2;
} else {
    cancelButton = b1;
    okButton = b2;
}
okButton.setText("Ok");
cancelButton.setText("Cancel");
}

public void f13 () {
shell2.dispose();
while (display.readAndDispatch());
Rectangle rect = shell1.getBounds();
}

}
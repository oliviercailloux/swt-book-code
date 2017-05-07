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
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;

public class Fragments1 {
	
Composite parent;
Text text;
Shell shell;
Widget widget;
Event event;
MenuItem item;
int i;

public void f1() {
Text text = new Text(parent,
    SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
}

public void f2() {
if ((text.getStyle () & SWT.SINGLE) != 0) {
    System.out.println ("Single Line Text");
}
}

public void f3() {
Label label = new Label(shell, SWT.NONE) {
    protected void checkSubclass() {
    }
    public void setText(String string) {
        System.out.println("Setting the string");
        super.setText(string);
    }
};
}

public void f4() {
widget.addListener(SWT.Dispose, new Listener() {
    public void handleEvent(Event event) {
        // widget was disposed
    }
});
}

public void f5() {
widget.addDisposeListener(new DisposeListener() {
    public void widgetDisposed(DisposeEvent event) {
        // widget was disposed
    }
});
}

public void f6() {
Listener listener = new Listener() {
    public void handleEvent(Event event) {
        switch (event.type) {
            case SWT.Dispose: break;
            case SWT.MouseDown: break;
            case SWT.MouseUp: break;
            case SWT.MouseMove: break;
        }
        System.out.println("Something happened.");
    }
};
shell.addListener(SWT.Dispose, listener);
shell.addListener(SWT.MouseDown, listener);
shell.addListener(SWT.MouseUp, listener);
shell.addListener(SWT.MouseMove, listener);
}

public void f7 () {
Listener listener = new Listener() {
	public void handleEvent(Event e) {
		switch (e.detail) {
			case SWT.TRAVERSE_ESCAPE:
				e.doit = false;
				/* Code to cancel edit goes here */
				break;
		}
	}
};
text.addListener(SWT.Traverse, listener);
}

public void f8 () {
Listener listener = new Listener() {
	public void handleEvent(Event e) {
		switch (e.detail) {
			case SWT.TRAVERSE_ESCAPE:
				e.detail = SWT.TRAVERSE_NONE;
				e.doit = true;
				/* Code to cancel edit goes here */
				break;
			case SWT.TRAVERSE_RETURN:
				e.detail = SWT.TRAVERSE_NONE;
				e.doit = true;
				/* Code to accept edit goes here */
				break;
		}
	}
};
text.addListener(SWT.Traverse, listener);
}

int getStyle() {
	return 0;
}

public void f9 () {
Listener listener = new Listener() {
    public void handleEvent(Event e) {
        switch (e.detail) {
            case SWT.TRAVERSE_ESCAPE:
            case SWT.TRAVERSE_PAGE_NEXT:
            case SWT.TRAVERSE_PAGE_PREVIOUS:
                e.doit = true;
                break;
            case SWT.TRAVERSE_RETURN:
            case SWT.TRAVERSE_TAB_NEXT:
            case SWT.TRAVERSE_TAB_PREVIOUS:
                if ((getStyle() & SWT.SINGLE) != 0) {
                    e.doit = true;
                } else {
                    if ((e.stateMask & SWT.MODIFIER_MASK)
                        != 0) {
                        e.doit = true;
                    }
                }
                break;
        }
    }
};
text.addListener(SWT.Traverse, listener);
}

public void f10 () {
//WRONG – broken when new modifier masks are added
int bits = SWT.CONTROL | SWT.ALT | SWT.SHIFT | SWT.COMMAND;
if ((event.stateMask & bits) == 0) {
    System.out.println("No modifiers are down");
}

//CORRECT – works when new modifier masks are added
if ((event.stateMask & SWT.MODIFIER_MASK) == 0) {
    System.out.println("No modifiers are down");
}
}

public void f11 () {
item.setText("Select &All\tCtrl+A");
item.setAccelerator(SWT.MOD1 + 'A');
item.addListener(SWT.Selection, new Listener() {
    public void handleEvent(Event e) {
        System.out.println("The item was selected.");
    }
});
}

public void f12 () {
    //WRONG – only draws last percentage
    text.setText("0 %");
    for (int i=1; i<=100; i++) {
    	try {
    		Thread.sleep(100);
    	} catch (Throwable th) {}
        text.setText(i + " %");
    }
}

public void f13 () {
    //CORRECT – draws every percentage
    text.setText("0 %");
    text.update();
    for (int i=1; i<=100; i++) {
    	try {
    		Thread.sleep(100);
    	} catch (Throwable th) {}
        text.setText(i + " %");
        text.update();
    }
}
}
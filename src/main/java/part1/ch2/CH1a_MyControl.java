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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class CH1a_MyControl {

    static class MyControl extends Canvas implements Listener {
	String string = "";

	public MyControl(final Composite parent, final int style) {
	    super(parent, style);
	    addListener(SWT.Paint, this);
	    addListener(SWT.FocusIn, this);
	    addListener(SWT.FocusOut, this);
	    addListener(SWT.KeyDown, this);
	}

	public void handleEvent(final Event event) {
	    switch (event.type) {
	    case SWT.Paint:
		final GC gc = event.gc;
		final Rectangle rect = getClientArea();
		final Point extent = gc.textExtent(string);
		int x = (rect.width - extent.x) / 2;
		int y = (rect.height - extent.y) / 2;
		gc.drawText(string, x, y);
		if (isFocusControl()) {
		    x -= 2;
		    y -= 2;
		    extent.x += 3;
		    extent.y += 3;
		    gc.drawFocus(x, y, extent.x, extent.y);
		}
		break;
	    case SWT.FocusIn:
	    case SWT.FocusOut:
		redraw();
		break;
	    case SWT.KeyDown:
		if (event.character == ' ') {
		    notifyListeners(SWT.Selection, null);
		}
		break;
	    }
	}

	public void setText(final String string) {
	    checkWidget();
	    this.string = string == null ? "" : string;
	}
    }

    public static void main(final String[] args) {
	final Display display = new Display();
	final Shell shell = new Shell(display);
	shell.setLayout(new RowLayout(SWT.VERTICAL));
	final MyControl button1 = new MyControl(shell, SWT.PUSH);
	button1.setText("MyControl");
	shell.setSize(200, 200);
	shell.open();
	while (!shell.isDisposed()) {
	    if (!display.readAndDispatch()) {
		display.sleep();
	    }
	}
	display.dispose();
    }

}
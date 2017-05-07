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
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class TabTraversal {
	
public static void main(String[] args) {
	Display display = new Display();
	final Color red =
		display.getSystemColor(SWT.COLOR_RED);
	final Color blue =
		display.getSystemColor(SWT.COLOR_BLUE);
	Shell shell = new Shell(display);
	shell.setLayout(new RowLayout(SWT.VERTICAL));
	Button button = new Button(shell, SWT.PUSH);
	button.setBounds(10, 10, 100, 32);
	button.setText("Button");
	final Canvas canvas = new Canvas(shell, SWT.BORDER);
	canvas.setBackground(blue);
	canvas.addListener(SWT.Traverse, new Listener() {
		public void handleEvent(Event e) {
			switch (e.detail) {
				/* Do tab group traversal */
				case SWT.TRAVERSE_ESCAPE:
				case SWT.TRAVERSE_RETURN:
				case SWT.TRAVERSE_TAB_NEXT:
				case SWT.TRAVERSE_TAB_PREVIOUS:
				case SWT.TRAVERSE_PAGE_NEXT:
				case SWT.TRAVERSE_PAGE_PREVIOUS:
					e.doit = true;
					break;
			}
		}
	});
	canvas.addListener(SWT.FocusIn, new Listener() {
		public void handleEvent(Event e) {
			canvas.setBackground(red);
		}
	});
	canvas.addListener(SWT.FocusOut, new Listener() {
		public void handleEvent(Event e) {
			canvas.setBackground(blue);
		}
	});
	canvas.addListener(SWT.KeyDown, new Listener() {
		public void handleEvent(Event e) {
			System.out.println("Got a Key");
		}
	});
	shell.setDefaultButton(button);
	shell.setSize(200, 200);
	shell.open();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) display.sleep();
	}
	display.dispose();
}

}
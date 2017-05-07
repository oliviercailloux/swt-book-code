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
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class Shell2 {
	
public static void main(String[] args) {
    final Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout ());
    int style = SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL;
	final Text text = new Text(shell, style);
	final Shell tip = new Shell (shell, SWT.ON_TOP);
	final List list = new List(tip, style);
	for (int i=0; i<128; i++) {
		list.add ("Item" + i);
	}
	list.setSize(100,100);
	tip.pack();
	Listener hideListener = new Listener () {
		public void handleEvent (Event event) {
			tip.setVisible(false);
		}
	};
	Widget [] widgets = new Widget [] {
		shell,
		text,
		text.getHorizontalBar(), 
		text.getVerticalBar(),
	};
	for (int i=0; i<widgets.length; i++) {
		Widget widget = widgets [i];
		if (widget != null) {
			widget.addListener (SWT.Move, hideListener);
			widget.addListener (SWT.Resize, hideListener);
			widget.addListener (SWT.MouseDown, hideListener);
			widget.addListener (SWT.Selection, hideListener);
		}
	}
	text.addListener(SWT.KeyDown, new Listener () {
		public void handleEvent (Event event) {
			switch (event.character) {
				case 0: break;
				case ' ':
					if ((event.stateMask & SWT.CTRL) != 0) {
						if (!tip.isVisible()) {
							Point pt = text.getCaretLocation();
							pt.y += text.getLineHeight();
							pt = display.map(text, null, pt);
							tip.setLocation(pt);
							list.setSelection(0);
							tip.setVisible (true);
						}
						event.doit = false;
						return;
					}
					//FALL THROUGH
				case '\r':
					if (tip.isVisible()) {
						String [] strings = list.getSelection();
						if (strings.length > 0) {
							text.insert(strings [0]);
							event.doit = false;
						}
					}
					//FALL THROUGH
				default:
					tip.setVisible (false);
					return;
			}
			if (!tip.isVisible()) return;
			switch (event.keyCode) {
				case SWT.HOME: {
					list.setSelection(0);	
					event.doit = false;			
					break;
				}	
				case SWT.END: {
					list.setSelection(list.getItemCount() - 1);	
					event.doit = false;			
					break;
				}
				case SWT.ARROW_UP: {
					int index = list.getSelectionIndex() - 1;
					list.setSelection(index);	
					event.doit = false;			
					break;
				}
				case SWT.ARROW_DOWN: {
					int index = list.getSelectionIndex() + 1;
					list.setSelection(index);	
					event.doit = false;			
					break;
				}
				case SWT.PAGE_UP: {
					int index = list.getTopIndex();
					if (index == list.getSelectionIndex()) {
						Rectangle rect = list.getClientArea();
						int page = rect.height / list.getItemHeight();
						page = Math.max (1, page - 1);
						index = Math.max (0, index - page);
						list.setTopIndex(index);
					}
					list.setSelection (index);
					event.doit = false;			
					break;
				}
				case SWT.PAGE_DOWN: {
					int index = list.getTopIndex();
					Rectangle rect = list.getClientArea();
					int page = rect.height / list.getItemHeight();
					page = Math.max (1, page - 1);
					int last = list.getItemCount() - 1;
					index = Math.min (last, index + page);
					if (index == list.getSelectionIndex()) {
						list.setTopIndex(index);
						index = list.getTopIndex();
						index += page;
					}
					list.setSelection(index);
					event.doit = false;			
					break;
				}
			}
		}
	});
    shell.setSize(200, 200);
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}

}

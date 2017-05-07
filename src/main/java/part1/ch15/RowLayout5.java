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
package part1.ch15;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class RowLayout5 {

    public static void main(final String[] args) {
	final Display display = new Display();
	final Monitor primary = display.getPrimaryMonitor();
	final Rectangle rect = primary.getClientArea();
	final Class clazz = RowLayout5.class;
	final Image image = new Image(display, clazz.getResourceAsStream("pyramid.jpg"));
	final Shell shell = new Shell(display, SWT.NONE);
	final RowLayout layout = new RowLayout();
	layout.type = SWT.VERTICAL;
	// layout.fill = true;
	shell.setLayout(layout);
	final Label label = new Label(shell, SWT.NONE);
	label.setImage(image);
	final ProgressBar progress = new ProgressBar(shell, SWT.NONE);
	shell.pack();
	final Rectangle bounds = shell.getBounds();
	final int x = rect.x + Math.max(0, (rect.width - bounds.width) / 2);
	final int y = rect.y + Math.max(0, (rect.height - bounds.height) / 2);
	shell.setBounds(x, y, bounds.width, bounds.height);
	shell.open();
	display.timerExec(1000, new Runnable() {
	    public void run() {
		final int value = progress.getSelection();
		if (value < 100) {
		    progress.setSelection(value + 10);
		    display.timerExec(1000, this);
		} else {
		    shell.dispose();
		}
	    }
	});
	while (!shell.isDisposed()) {
	    if (!display.readAndDispatch()) {
		display.sleep();
	    }
	}
	image.dispose();
	display.dispose();
    }

}

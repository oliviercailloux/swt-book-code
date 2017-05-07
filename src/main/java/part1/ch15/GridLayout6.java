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

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class GridLayout6 {
	
public static void main(String[] args) {
    Display display = new Display();
    final Shell shell = new Shell(display);
    final GridLayout layout = new GridLayout();
    layout.numColumns = 5;
    shell.setLayout(layout);
    int[] alignment = new int[] {
	    SWT.BEGINNING,
	    SWT.CENTER,
	    SWT.END,
	    SWT.FILL,
    };
    String[] name = new String[] {"B", "C", "E", "F"};
    for (int i = 0; i < alignment.length; i++) {
	    for (int j= 0; j < alignment.length; j++) {
	        Button button = new Button(shell, SWT.PUSH);
	        button.setText(name[i] + "," + name [j]);
	        GridData data = new GridData();
	        data.horizontalAlignment = alignment[i];
	        data.verticalAlignment = alignment[j];
	        button.setLayoutData(data);
	    }
	    Label label = new Label(shell, SWT.BORDER);
		GridData data = new GridData(128, 64);
	    label.setLayoutData(data);
    }
    for (int i = 0; i < alignment.length; i++) {
	    Label label = new Label(shell, SWT.BORDER);
		GridData data = new GridData(128, 64);
	    label.setLayoutData(data);
    }
    //BUGS - I GIVE UP!!
    shell.addListener(SWT.Paint, new Listener() {
    	public void handleEvent(Event event) {
    		GC gc = event.gc;
    		Rectangle rect = shell.getClientArea();
		    for (int i = 0; i < 4; i++) {
		    	int y = layout.marginHeight;
		    	y += (64 + layout.verticalSpacing) * i;
		    	gc.drawLine(0, y, rect.width, y);
		    }
			for (int i= 0; i < 4; i++) {
		    	int x = layout.marginWidth;
		    	x += (128 + layout.horizontalSpacing) * i;
		    	gc.drawLine(x, 0, x, rect.height);
		    }
    	}
    });
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
}
}

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

/**
 * Failed attempt to build a custom cursor transparency: transparentPixel
 * 
 * @author mcq
 */

public class CustomCursorInColor1 {

static final int points = 5;
static final int cursorWidth = 100;
static final int cursorHeight = 100;

public static void main(String[] args) {
    
    Display display = new Display();
    
    Image starImage = 
        new Image(display, cursorWidth, cursorHeight);
    drawStar(display, starImage, points);
    ImageData starImageData = starImage.getImageData();
    starImage.dispose();
    starImageData.transparentPixel = 
        starImageData.getPixel(0, 0);
    Cursor cursor = new Cursor (
        display, 
        starImageData, 
        cursorWidth / 2, 
        cursorHeight / 2);

    final Shell shell = new Shell(display);
    final Image img = new Image(display, starImageData);
    shell.setText("Custom Cursor 2");
    shell.setSize(400, 400);
    shell.setCursor(cursor);
    shell.addListener(SWT.Paint, new Listener() {
    	public void handleEvent(Event event) {
    		Rectangle r = shell.getClientArea();
    		int x = (r.width - cursorWidth) / 2 + r. x;
    		int y = (r.height - cursorHeight) / 2 + r.y;
    		event.gc.drawImage(img, x, y);
    	}});
    shell.open();
    
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    
    // Clean up
    img.dispose();
    cursor.dispose();
    display.dispose();
}

static void drawStar(Display display, Image image, int points) {
    // Make the shape
    Color white = display.getSystemColor(SWT.COLOR_WHITE);
    Color yellow = display.getSystemColor(SWT.COLOR_YELLOW);
    Color blue = display.getSystemColor(SWT.COLOR_BLUE);
    int[] radial = new int[points * 2];
    Rectangle bounds = image.getBounds();
    Point center = new Point(bounds.width / 2, bounds.height / 2);
    int pos = 0;
    for (int i = 0; i < points; ++i) {
        double r = Math.PI * 2 * pos / points;
        radial[i * 2] = (int) ((1 + Math.cos(r)) * center.x);
        radial[i * 2 + 1] = (int) ((1 + Math.sin(r)) * center.y);
        pos = (pos + points / 2) % points;
    }
    
    // Draw it
    GC gc = new GC(image);
    gc.setBackground(white);
    gc.fillRectangle(bounds);
    gc.setBackground(yellow);
    gc.setForeground(blue);
    gc.fillPolygon(radial);
    gc.drawPolygon(radial);
    
    // Clean up
    gc.dispose();
}

}

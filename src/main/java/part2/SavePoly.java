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
 * Draws a 5-pointed star, then saves it in a a jpeg file.
 * 
 * @author mcq
 */

public class SavePoly {

static final int points = 5;
static final String filename = "Star.jpeg";

public static void main(String[] args) {
    
    // Make the shape
    int[] radial = new int[points * 2];
    Rectangle bounds = new Rectangle(0, 0, 200, 200);
    Point center = new Point(100, 100);
    int pos = 0;
    for (int i = 0; i < points; ++i) {
        double r = Math.PI * 2 * pos / points;
        radial[i * 2] = (int) ((1 + Math.cos(r)) * center.x);
        radial[i * 2 + 1] = (int) ((1 + Math.sin(r)) * center.y);
        pos = (pos + points / 2) % points;
    }
    
    // Draw it
    Display display = new Display();
    Color yellow = display.getSystemColor(SWT.COLOR_YELLOW);
    Color blue = display.getSystemColor(SWT.COLOR_BLUE);
    Image image = new Image(display, bounds);
    GC gc = new GC(image);
    gc.setForeground(blue);
    gc.setBackground(yellow);
    gc.fillPolygon(radial);
    gc.drawPolygon(radial);
    gc.dispose();
    
    // Write it out
    ImageLoader loader = new ImageLoader();
    loader.data = new ImageData[] {image.getImageData()};
    loader.save(filename, SWT.IMAGE_JPEG);
    
    // Clean up
    image.dispose();
    blue.dispose();
    yellow.dispose();
    display.dispose();
}
}

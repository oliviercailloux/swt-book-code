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

public class MakeAnimationImage {

static final String fileName = "Animation.jpg";

public static void main(String[] args) {
    Display display = new Display();
    Image image = new Image(display, 500, 50);
    final Color white = 
        display.getSystemColor(SWT.COLOR_WHITE);
    GC gc = new GC(image);
    gc.setBackground(white);
    gc.fillRectangle(0, 0, 500, 50);
    int[] points = new int[6];
    for (int i=0; i<10; ++i) {
    	points[0] = (50*i) + 5 + (i * 4);
    	points[1] = new int [] {10, 15, 25, 35, 40, 45, 40, 35, 25, 15} [i];
    	points[2] = (50*i) + 20 + (i * 2);
    	points[3] = new int[] {35, 40, 45, 40, 35, 25, 15, 10, 15, 25} [i];
    	points[4] = (50*i) + i * 6;
    	points[5] = new int[] {35, 25, 15, 10, 15, 25, 35, 40, 45, 40} [i];
    	gc.drawPolygon(points);
    }
    ImageLoader loader = new ImageLoader();
    loader.data = new ImageData[] {image.getImageData()};
    loader.save(fileName, SWT.IMAGE_JPEG);
    display.dispose();
}

}

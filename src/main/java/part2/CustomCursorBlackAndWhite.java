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
 * Create a black and white custom cursor from a color image. This version of
 * the code creates an ImageData that has three colors: black, white, and a
 * color to represent transparency, then uses the 
 *    <code>Cursor(Device device, ImageData source, int hotspotX, int hotspotY)</code>
 * version of the constructor to to create the cursor.
 * 
 * @author mcq
 */

public class CustomCursorBlackAndWhite {

static final int cursorWidth = 100;
static final int cursorHeight = 100;
static final RGB black = new RGB(0, 0, 0);
static final RGB white = new RGB(255, 255, 255);

public static void main(String[] args) {
    
    Display display = new Display();
    
    Image starImage = 
        new Image(display, cursorWidth, cursorHeight);
    drawStar(display, starImage);
    ImageData starImageData = starImage.getImageData();
    starImage.dispose();
    starImageData = makePixelTransparent(
            starImageData, 
            starImageData.getPixel(0, 0));
    Cursor cursor = new Cursor (
            display, 
            starImageData, 
            cursorWidth / 2, cursorHeight / 2);

    final Shell shell = new Shell(display);
    final Image img = new Image(display, starImageData);
    shell.setText("Custom Cursor - Black and White");
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

static void drawStar(Display display, Image image) {
    // Make the shape
    int points = 5;
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
    Color white = display.getSystemColor(SWT.COLOR_WHITE);
    Color yellow = display.getSystemColor(SWT.COLOR_YELLOW);
    Color blue = display.getSystemColor(SWT.COLOR_BLUE);
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

static ImageData 
    makePixelTransparent(ImageData id, int pixel) {

    // Downsample to 3 color (black, white, transparent).
    int w = id.width, h = id.height;
    int pixelRow[] = new int[w];
    RGB[] colors = new RGB[3];
    RGB transparent = new RGB(128, 128, 128);
    colors[0] = black;
    colors[1] = white;
    colors[2] = transparent;
    int colorsIndex = 0;
    PaletteData p = id.palette;
    PaletteData p2 = new PaletteData(colors);
    int pixelRow2[] = new int[w];
    ImageData id2 = new ImageData(id.width,id.height,2,p2);
    id2.transparentPixel = 2;
    for (int j=0; j < h; ++j) {
        id.getPixels(0, j, w, pixelRow, 0);
        for (int i=0; i < w; ++i) {
            if (pixelRow[i] == pixel)
                pixelRow2[i] = 2;
            else
                pixelRow2[i] = mapPixel(p,pixelRow[i],p2);
        }
        id2.setPixels(0, j, w,pixelRow2, 0);
    }
    return id2;
}

/**
 * Converts the sourcePixel from the source to the destination palette.
 * 
 * @author mcq
 * @param source source PaletteData
 * @param sourcePixel pixel value in the source PaletteData
 * @param dest destination PaletteData
 * @return the corresponding pixel value in the destination
 */
static int mapPixel(
        PaletteData source, 
        int sourcePixel, 
        PaletteData dest) 
{
    RGB color = source.getRGB(sourcePixel);
    if (color.red+color.green+color.blue > (128*3))
        return dest.getPixel(white);
    else
        return dest.getPixel(black);
}

}

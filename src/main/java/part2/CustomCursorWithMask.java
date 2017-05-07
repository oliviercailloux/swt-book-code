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
 * the code creates source and mask images, then uses the 
 *    <code>Cursor(Device device, ImageData source, ImageData mask, int hotspotX, int hotspotY)</code>
 * version of the constructor to to create the cursor.
 * 
 * Note: The appearance of the cursor created by this code varies
 * signigicantly from platform to platform.
 * 
 * @author mcq
 */

public class CustomCursorWithMask {

    static final RGB black = new RGB(0, 0, 0);
    static final RGB white = new RGB(255, 255, 255);
    
public static void main(String[] args) {
    
    Display display = new Display();
    
    // Build an image
	final Point cursorSize = display.getCursorSizes()[0];
    Image image = new Image(display, cursorSize.x, cursorSize.y);
    drawStar(display, image);
    ImageData id = image.getImageData();
    image.dispose();
    ImageData source = makeSource(id);
    ImageData mask = makeMask(id, id.getPixel(0, 0));
    
    Cursor cursor = new Cursor (display, source, mask, cursorSize.x / 2, cursorSize.y / 2);

    final Shell shell = new Shell(display);
    final Image sImage = new Image(display, source);
    final Image mImage = new Image(display, mask);
    shell.setText("Custom Cursor - With Mask");
    shell.setSize(400, 400);
    shell.setCursor(cursor);
    shell.addListener(SWT.Paint, new Listener() {
    	public void handleEvent(Event event) {
    		Rectangle r = shell.getClientArea();
    		int x = r.width / 2 + r. x;
    		int y = (r.height - cursorSize.y) / 2 + r.y;
            event.gc.drawImage(sImage, x - cursorSize.x, y);
            event.gc.drawImage(mImage, x, y);
           }});
    shell.open();
    
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch())
            display.sleep();
    }
    
    // Clean up
    mImage.dispose();
    sImage.dispose();
    cursor.dispose();
    display.dispose();
}

/**
 * Draw a simple 5-pointed start to use as our cursor. The star has a blue
 * border and is filled with yellow. The background is drawn in white, which we
 * will make transparent later.
 * 
 * @author mcq
 */
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

/**
 * Converts the given ImageData into a transparency mask. The
 * pixel parameter is the pixel value that will be treated as
 * transparent in the result
 * 
 * @author mcq
 * @param id the image to convert
 * @param pixel the transparent pixel
 * @return the transparency mask
 * @see ImageData#getTransparencyMask
 */
static ImageData makeMask(ImageData id, int pixel) {
    int w = id.width, h = id.height;
    int pixelRow[] = new int[w];
    RGB[] colors = new RGB[2];
    colors[0] = black;
    colors[1] = white;
    int colorsIndex = 0;
    PaletteData p = id.palette;
    PaletteData p2 = new PaletteData(colors);
    int pixelRow2[] = new int[w];
    ImageData id2 = new ImageData(id.width, id.height, 1, p2);
    for (int j=0; j < h; ++j) {
        id.getPixels(0, j, w, pixelRow, 0);
        for (int i=0; i < w; ++i)
            if (pixelRow[i] == pixel)
                pixelRow2[i] = 0;
            else
                pixelRow2[i] = 1;
        id2.setPixels(0, j, w,pixelRow2, 0);
    }
    return id2;
}

/**
 * Returns a new ImageData that is the result of converting the parameter into
 * a monochrome bitmap.
 * 
 * @author mcq
 * @param id the ImageData to convert to monochrome
 * @return a monochrome equivalent
 */
static ImageData makeSource(ImageData id) {
    int w = id.width, h = id.height;
    RGB[] colors = new RGB[2];
    colors[0] = black;
    colors[1] = white;
    int colorsIndex = 0;
    PaletteData p = id.palette;
    PaletteData p2 = new PaletteData(colors);
    int pixelRow[] = new int[w];
    int pixelRow2[] = new int[w];
    ImageData id2 = 
        new ImageData(id.width, id.height, 1, p2);
    for (int j=0; j < h; ++j) {
        id.getPixels(0, j, w, pixelRow, 0);
        for (int i=0; i < w; ++i) {
            pixelRow2[i] = mapPixel(p, pixelRow[i], p2);
        }
        id2.setPixels(0, j, w,pixelRow2, 0);
    }
    return id2;
}

/**
 * Maps a color specified by the sourcePixel in the source palette, to a
 * a color from the dest palette, and returns the resulting pixel value.
 * This version assumes that the destination palette is black & white, and
 * performs a very simple color -> monochrome mapping.
 * 
 * @param source the palette to get the source color from
 * @param sourcePixel the pixel value to convert
 * @param dest the palette to use for conversion
 * @return the pixel value for the color in the destination palette
 */
static int mapPixel(PaletteData source, int sourcePixel, PaletteData dest) {
    RGB color = source.getRGB(sourcePixel);
    if (color.red+color.green+color.blue > (128*3))
        return dest.getPixel(white);
    else
        return dest.getPixel(black);
}

}

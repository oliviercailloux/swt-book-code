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

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;

/**
 * Failed attempt to build a custom cursor transparency: downsample + transparentPixel
 * 
 * @author mcq
 */

public class CustomCursorInColor2 {

static final int points = 5;
static final int cursorWidth = 100;
static final int cursorHeight = 100;

public static void main(String[] args) {
    
    Display display = new Display();
    
    // Build an image
    Image starImage = 
        new Image(display, cursorWidth, cursorHeight);
    drawStar(display, starImage, points);
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

/**
 * 
 * @param id
 * @param pixel
 * @return
 */
static ImageData 
    makePixelTransparent(ImageData id, int pixel) 
{
    ImageData id2 = makeIndexed(id);
    id2.transparentPixel = 
        mapPixel(id.palette, pixel, id2.palette);
    return id2;
}

/**
 * If the given image does not already use an indexed 
 * palette, convert it to one that does.
 * 
 * @author mcq
 * @param id the image data convert
 * @return a version that uses an indexed palette
 */
static ImageData makeIndexed(ImageData id) {
    if (!id.palette.isDirect) return id;
    int w = id.width, h = id.height;
    PaletteData p = id.palette;
    PaletteData p2 = getIndexedPalette(id, 256);
    int pixelRow[] = new int[w];
    int pixelRow2[] = new int[w];
    ImageData id2 = 
        new ImageData(id.width, id.height, 8, p2);
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
 * Create an indexed palette for the given image. This (very simplistic)
 * version just returns the first "maxColors" different colors from the image
 * A real version would have to look at all the colors in the image, and
 * return a palette that provided the best coverage.
 * 
 * @author mcq
 * @param id the ImageData for the image to scan
 * @param maxColors the number of colors to use
 * @return an index Palettedata
 */
private static PaletteData 
    getIndexedPalette(ImageData id, int maxColors)
{
    int w = id.width, h = id.height;
    RGB[] colors = new RGB[maxColors];
    Arrays.fill(colors, new RGB(0, 0, 0));
    HashSet colorSet = new HashSet(maxColors);
    int color = 0;
    PaletteData p = id.palette;
    int pixelRow[] = new int[w];
    for (int j=0; j<h && color<colors.length; ++j) {
        id.getPixels(0, j, w, pixelRow, 0);
        for (int i=0; i<w && color<colors.length; ++i) {
            RGB rgb = p.getRGB(pixelRow[i]);
            if (!colorSet.contains(rgb)) {
                colorSet.add(rgb);
                colors[color++] = rgb;
            }
        }
    }
    return new PaletteData(colors);
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
static int mapPixel (
        PaletteData source, 
        int sourcePixel, 
        PaletteData dest) 
{
    RGB[] RGBs = dest.getRGBs();
    RGB matchColor = source.getRGB(sourcePixel);
    int closestMatch = -1;
    int closestDiff = 256*3;
    for (int i=0; i < RGBs.length; ++i) {
        RGB c = RGBs[i];
        int diff = 
            Math.abs(c.red - matchColor.red) +
            Math.abs(c.green - matchColor.green) +
            Math.abs(c.blue - matchColor.blue);
        if (diff == 0) return i;
        if (diff < closestDiff) {
            closestDiff = diff;
            closestMatch = i;
        }
    }
    return closestMatch;
}

}

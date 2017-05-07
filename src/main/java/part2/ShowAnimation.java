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
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;

/**
 * Loads an animated GIF from a file and displays it. The animation
 * will repeat as many times as is specified by the GIF file.
 * 
 * Use -DSLOW_ANIMATION to slow the animation down.
 * 
 * @author mcq
 * @see #getDelayTime()
 */

public class ShowAnimation {

// Location to draw the animation within the canvas
static final int drawX = 10;
static final int drawY = 10;

// True if we animation speed should be bounded.
static boolean SLOW_ANIMATION = 
    System.getProperty("SLOW_ANIMATION") != null;

// Controls in the application
Display display;
Color canvasBackground;
Shell shell;
Canvas canvas;

// Image loading and animation
ImageLoader loader;
ImageData[] imageData;
Image[] images;
int currentImage = 0;
int animationLoopCount;
Rectangle drawBounds;

/**
 * Launches the application.
 * 
 * @author mcq
 * @param args ignored.
 */
public static void main(String[] args) {
    new ShowAnimation().run();
}

/**
 * Opens a shell, gets a GIF, displays it, and runs the event loop.
 * 
 * @author mcq
 */
void run() {
    display = new Display();
    shell = new Shell(display, SWT.SHELL_TRIM);
    shell.setText("Show Animation");
    shell.setLayout(new FillLayout());
    canvas = new Canvas(shell, 
        SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE);
    canvasBackground = 
        canvas.getBackground();
        // display.getSystemColor(SWT.COLOR_WHITE);
    canvas.addPaintListener(new PaintListener() {
        public void paintControl(PaintEvent e) {
            Image img = getCurrentImage();
            if (img == null) {
                // Erase the whole canvas
                e.gc.setBackground(canvasBackground);
                e.gc.fillRectangle(canvas.getClientArea());
            } else {
                // Display the image, then erase the rest
                e.gc.drawImage(img, drawX, drawY);
                Region theRest = new Region(display);
                e.gc.getClipping(theRest);
                theRest.subtract(drawBounds);
                e.gc.setClipping(theRest);
                e.gc.setBackground(canvasBackground);
                e.gc.fillRectangle(canvas.getClientArea());
                theRest.dispose();
            }
        }});
    shell.setSize(400, 300);
    shell.open();
    if (loadImages(getFile())) {
        drawCurrentImage();
        startAnimationTimer();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }
    disposeImages();
    display.dispose();
}

/**
 * Answers the current image to display if it exists, or null.
 * 
 * @author mcq
 * @return the current Image, or null
 */
protected Image getCurrentImage() {
    if (images == null) return null;
    return images[currentImage];
}

/**
 * Answers a file to load a GIF from, or null if a file was not selected.
 * 
 * @author mcq
 * @return the name of the file to display, or null
 */
String getFile() {
    FileDialog dialog = new FileDialog(shell, SWT.OPEN);
    dialog.setText("Select an animated GIF");
    dialog.setFilterPath(System.getProperty("user.home"));
    dialog.setFilterExtensions(new String[] { "*.gif" });
    dialog.setFilterNames(new String[] { "GIF (*.gif)" });
    return dialog.open();
}

/**
 * Reads one or more images from the specified file into
 * an array of ImageData instances, then converts the 
 * ImageDatas into a matching array of Images.
 * 
 * @author mcq
 * @param fileName the name of the file to read
 * @return true if images could be loaded.
 */
boolean loadImages(String fileName) {
    if (fileName == null) return false;
    try {
        loader = new ImageLoader();
        imageData = loader.load(fileName);
        if (imageData.length == 0) return false;
        images = convertImageDataToImages(
            imageData,
            canvasBackground);
        animationLoopCount = loader.repeatCount;
        drawBounds = images[0].getBounds();
        drawBounds.x += drawX;
        drawBounds.y += drawY;
    } catch (SWTException t) {
        System.err.println("Got an exception:" + t);
        t.printStackTrace(System.err);
        return false;
    }
    return true;
}

/**
 * Answers an array, which has one Image for each ImageData in the argument
 * array, by applying the transitions specified by the disposalMethod of each
 * ImageData. The defaultBackground is the background color that will be used
 * by the DM_FILL_BACKGROUND disposalMethod.
 * 
 * @author mcq
 * @return the argument converted to Images
 * @param ids the ImageDatas to convert
 * @param defaultBackground default background color
 */
Image[] convertImageDataToImages (
        ImageData[] ids, 
        Color defaultBackground) 
{
    if (ids == null) return null;
    Image[] answer = new Image[imageData.length];

    // Step 1: Determine the size of the resulting images.
    int width = 0, height = 0;
    for (int i = 0; i < imageData.length; ++i) {
        ImageData id = imageData[i];
        width = Math.max(width, id.x + id.width);
        height = Math.max(height, id.y + id.height);
    }

    // Step 2: Construct each image.
    int transition = SWT.DM_FILL_BACKGROUND;
    for (int i = 0; i < imageData.length; ++i) {
        ImageData id = imageData[i];
        answer[i] = new Image(display, width, height);
        GC gc = new GC(answer[i]);
        
        // Do the transition from the previous image.
        switch (transition) {
            case SWT.DM_FILL_NONE:
            case SWT.DM_UNSPECIFIED:
                // Start from last image.
                gc.drawImage(answer[i-1], 0, 0);
                break;
            case SWT.DM_FILL_PREVIOUS:
                // Start from second last image.
                gc.drawImage(answer[i-2], 0, 0);
                break;
            default:
                // DM_FILL_BACKGROUND or anything else,
                // just fill with default background.
            	gc.setBackground(defaultBackground);
            	gc.fillRectangle(0, 0, width, height);
            	break;
        }

        // Draw the current image and clean up.
        Image img = new Image(display, id);
        gc.drawImage(
            img,
            0, 0, id.width, id.height,
            id.x, id.y, id.width, id.height);
        img.dispose();
        gc.dispose();

        // Compute the next transition.
        // Special case: Can't do DM_FILL_PREVIOUS on the
        // second image since there is no "second last"
        // image to use.
        transition = id.disposalMethod;
        if (i == 0 && transition == SWT.DM_FILL_PREVIOUS)
            transition = SWT.DM_FILL_NONE;
    }
    return answer;
}

/**
 * Answers the time to delay before displaying the next image.
 * 
 * Because early browsers had significant overhead when displaying animation,
 * and some of the early GIF specifications incorrectly stated that the
 * delayTime was in seconds, some GIF images have exceptionally small delayTime
 * values. Because of this, Most modern applications that display animated GIFs
 * insert artificial delays to prevent them from running too fast. Use
 * -DSLOW_ANIMATION on the command line to enable that behavior for this app.
 * 
 * @author mcq
 * @return int the time to delay before showing the next image
 */
int getDelayTime() {
    if (SLOW_ANIMATION) 
        return imageData[currentImage].delayTime * 10 + 30;
    else
        return imageData[currentImage].delayTime * 10;
}

/**
 * Draws the current image.
 *
 * @author mcq
 */
void drawCurrentImage() {
    canvas.redraw(
        drawBounds.x, drawBounds.y,
        drawBounds.width, drawBounds.height,
        false);
    canvas.update();
}

/**
 * Starts a timeExec that causes the images to animate.
 * 
 * Note: This code is implemented using the algorithm
 * that is used by most browsers: display the image, then
 * delay for the full delay amount. This means that the 
 * total time between frames is "time to draw + delay".
 * 
 * @author mcq
 */
void startAnimationTimer() {
    // If there is only one image, don't start a timer.
    if (images.length < 2) return;
    display.timerExec(
        getDelayTime(),
        new Runnable() {
            public void run() {
                if (canvas.isDisposed()) return;
                currentImage = 
                    (currentImage+1) % images.length;
                drawCurrentImage();
                // If this is the last image in the
                // animation, check if we are looping
                // forever, or still have more loops to
                // do. If not, don't restart the timer.
                if (currentImage+1 == images.length &&
                        animationLoopCount != 0 &&
                            --animationLoopCount <= 0)
                        return;
                display.timerExec(getDelayTime(), this);
            }});
}

/**
 * Dispose of any images that were created.
 * 
 * @author mcq
 */
void disposeImages() {
    if (images == null)
        return;
    for (int i = 0; i < images.length; ++i) {
        if (images[i] != null) {
            try {
                images[i].dispose();
            } catch (SWTException ex) {
                System.err.println(
                    "Exception while disposing of images"
                    + ex);
                ex.printStackTrace(System.err);
            }
        }
    }
}

}

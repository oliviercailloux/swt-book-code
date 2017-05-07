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
package part3.minesweeper;

import org.eclipse.swt.widgets.*;

/**
 * @author Steve Northover
 *
 * A Timer is a counter that automatically increments its value
 * about once a second.
 * 
 * Thursday June 17, 2004.
 */
public class Timer extends Counter {
	
	long base;
	int offset;
	boolean running;
	Runnable runnable;
	
	/**
	 * The period of the timer in milliseconds.
	 */
	static final int PERIOD = 500;
	
public Timer (Composite parent, int style) {
	super (parent, style);
	final Display display = getDisplay ();
	runnable = new Runnable () {
		public void run () {
			if (isDisposed ()) return;
			long time = System.currentTimeMillis ();
			setValue (offset + (int) (time - base) / 1000);
			display.timerExec (PERIOD, runnable);
		}
	};
}

/**
 * Determines if the timer is running.
 * 
 * @return true if the timer is running; false otherwise.
 */
public boolean isRunning () {
	checkWidget ();
	return running;
}

/**
 * Resets the counter.  The next time the counter draws,
 * it will start again from zero.
 */
public void reset () {
	checkWidget ();
	base = System.currentTimeMillis ();
	offset = 0;
}

/**
 * Starts the counter.  If the counter was stopped,
 * it will start drawing again begining at the same
 * point where it stopped.
 */
public void start () {
	checkWidget ();
	if (running) return;
	base = System.currentTimeMillis ();
	running = true;
	Display display = getDisplay ();
	display.timerExec (0, runnable);
}

/**
 * Stops the counter.  The counter will stop drawing.
 */
public void stop () {
	checkWidget ();
	if (!running) return;
	offset = getValue ();
	running = false;
	Display display = getDisplay ();
	display.timerExec (-1, runnable);
}

}
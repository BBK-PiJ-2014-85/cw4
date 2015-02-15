package interfaces;

import java.util.Calendar;

/**
 * A class to provide and set the current time, used by implementations of ContactManager. This can be either 
 * the system time, or set to another fixed time (which is useful for testing etc.). 
 * 
 * @author Paul Day
 */

public interface Clock {

	/**
	 * Returns the current time, as determined by the system clock or a previously set fixed time.
	 * 
	 * @return the Calendar containing the current time.
	 */
	Calendar getCurrent();
	
	/**
	 * Sets the current time to that of the system time.
	 */
	void resetToSystemTime();
	
	/**
	 * Sets the current time to a fixed point in time.
	 * 
	 * @param fixedTime the time which the ContactManager which consider as the current time.
	 */
	void setTime(Calendar fixedTime);
	
}

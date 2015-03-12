
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A class to provide and set the current time, used by implementations of ContactManager. This can be either 
 * the system time, or set to another fixed time (which is useful for testing etc.). 
 * 
 * @author Paul Day
 */

public class Clock {

	static Calendar current;
	
	/**
	 * Returns the current time, as determined by the system clock or a previously set fixed time.
	 * 
	 * @return the Calendar containing the current time.
	 */
	public static Calendar getCurrent()
	{
		if (current == null) return new GregorianCalendar();
		else return current;
	}
	
	/**
	 * Sets the current time to that of the system time.
	 */
	public static void resetToSystemTime()
	{
		current = null;
	}
	
	/**
	 * Sets the current time to a fixed point in time.
	 * 
	 * @param fixedTime the time which the ContactManager which consider as the current time.
	 */
	public static void setTime(Calendar fixedTime)
	{
		current = fixedTime;
	}
	
}

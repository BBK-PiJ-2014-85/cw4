package tests;


import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;

import impls.Clock;

import org.junit.Test;

/**
 * Tests the Clock class.
 * 
 * @author Paul Day
 */

public class TestClock {

	// TODO - set up method to round off rather than repeat.

	@Test public void testStartsOffAsSystemTime() 
	{
		Calendar current = Clock.getCurrent();
		Calendar system = new GregorianCalendar();
		
		/* This tests whether the value is currently the same as the system time. Some time may lapse between the two 
		 * measurements (assume less than a second) and therefore we correct for this below.
		 */
		
		if (system.get(Calendar.MILLISECOND) > current.get(Calendar.MILLISECOND))
		{
			system.add(Calendar.MILLISECOND,-system.get(Calendar.MILLISECOND) + current.get(Calendar.MILLISECOND));
		}
		else if (system.get(Calendar.MILLISECOND) != current.get(Calendar.MILLISECOND))
		{
			system.add(Calendar.MILLISECOND, system.get(Calendar.MILLISECOND) + 1000 - current.get(Calendar.MILLISECOND));
		}

		assertEquals(system.get(Calendar.YEAR),current.get(Calendar.YEAR));
		assertEquals(system.get(Calendar.MONTH),current.get(Calendar.MONTH));
		assertEquals(system.get(Calendar.DAY_OF_MONTH),current.get(Calendar.DAY_OF_MONTH));
		assertEquals(system.get(Calendar.HOUR),current.get(Calendar.HOUR));
		assertEquals(system.get(Calendar.MINUTE),current.get(Calendar.MINUTE));
		assertEquals(system.get(Calendar.SECOND),current.get(Calendar.SECOND));
	}
	
	@Test public void testOverwritesCorrectly() 
	{
		Clock.setTime(new GregorianCalendar(2010,5,5));
		
		assertEquals(2010,Clock.getCurrent().get(Calendar.YEAR));
		assertEquals(5,Clock.getCurrent().get(Calendar.MONTH));
		assertEquals(5,Clock.getCurrent().get(Calendar.DAY_OF_MONTH));
	}
	
	@Test public void testResetsCorrectly() 
	{
		Clock.setTime(new GregorianCalendar(2010,5,5));
		Clock.resetToSystemTime();
		
		Calendar current = Clock.getCurrent();
		Calendar system = new GregorianCalendar();
		
		if (system.get(Calendar.MILLISECOND) > current.get(Calendar.MILLISECOND))
		{
			system.add(Calendar.MILLISECOND,-system.get(Calendar.MILLISECOND) + current.get(Calendar.MILLISECOND));
		}
		else if (system.get(Calendar.MILLISECOND) != current.get(Calendar.MILLISECOND))
		{
			system.add(Calendar.MILLISECOND, system.get(Calendar.MILLISECOND) + 1000 - current.get(Calendar.MILLISECOND));
		}

		assertEquals(system.get(Calendar.YEAR),current.get(Calendar.YEAR));
		assertEquals(system.get(Calendar.MONTH),current.get(Calendar.MONTH));
		assertEquals(system.get(Calendar.DAY_OF_MONTH),current.get(Calendar.DAY_OF_MONTH));
		assertEquals(system.get(Calendar.HOUR),current.get(Calendar.HOUR));
		assertEquals(system.get(Calendar.MINUTE),current.get(Calendar.MINUTE));
		assertEquals(system.get(Calendar.SECOND),current.get(Calendar.SECOND));
	}
	
	@Test public void testOverwritingCurrentlyOverwritten() 
	{
		Clock.setTime(new GregorianCalendar(2020,3,3));
		Clock.setTime(new GregorianCalendar(2010,5,5));
		
		assertEquals(2010,Clock.getCurrent().get(Calendar.YEAR));
		assertEquals(5,Clock.getCurrent().get(Calendar.MONTH));
		assertEquals(5,Clock.getCurrent().get(Calendar.DAY_OF_MONTH));
	}
	
	
	

}

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the FutureMeetingImpl implementation of the interface FutureMeeting.
 * 
 * @author Paul Day
 */

public class TestFutureMeetingImpl {

	Meeting testMeeting;
	Set<Contact> attendees;
	
	@Before
	public void createTestMeeting()
	{
		attendees = new HashSet<Contact>();
		attendees.add(new ContactImpl(1,"Bob","A bloke."));
		attendees.add(new ContactImpl(2,"Fred","Another bloke."));
		
		Calendar date = new GregorianCalendar(1985,03,13);
		
		testMeeting = new FutureMeetingImpl(1,date, attendees);
	}

	@Test
	public void testID()
	{
		assertEquals(1,testMeeting.getId());
	}
	
	@Test
	public void testDate()
	{
		assertEquals(new GregorianCalendar(1985,03,13),testMeeting.getDate());
	}
	
	@Test
	public void testContacts()
	{
		assertEquals(attendees,testMeeting.getContacts());
	}
	
	@Test
	public void testEqualsSame()
	{
		Set<Contact> attendees2 = new HashSet<Contact>();
		attendees2.add(new ContactImpl(1,"Bob","A bloke."));
		attendees2.add(new ContactImpl(2,"Fred","Another bloke."));
		
		Calendar date2 = new GregorianCalendar(1985,03,13);
		
		FutureMeeting meeting2 = new FutureMeetingImpl(1,date2,attendees2);
		
		assertTrue(meeting2.equals(testMeeting));
	}
	
	@Test
	public void testEqualsDateDifferent()
	{
		Set<Contact> attendees2 = new HashSet<Contact>();
		attendees2.add(new ContactImpl(1,"Bob","A bloke."));
		attendees2.add(new ContactImpl(2,"Fred","Another bloke."));
		
		Calendar date2 = new GregorianCalendar(1985,04,13);
		
		FutureMeeting meeting2 = new FutureMeetingImpl(1,date2,attendees2);
		
		assertFalse(meeting2.equals(testMeeting));
	}
	
	@Test
	public void testEqualsAttendeesDifferent()
	{
		Set<Contact> attendees2 = new HashSet<Contact>();
		attendees2.add(new ContactImpl(1,"Bob","A bloke."));
		attendees2.add(new ContactImpl(2,"Freeda","Another bloke."));
		
		Calendar date2 = new GregorianCalendar(1985,03,13);
		
		FutureMeeting meeting2 = new FutureMeetingImpl(1,date2,attendees2);
		
		assertFalse(meeting2.equals(testMeeting));
	}
	
	@Test
	public void testEqualsIdsDifferent()
	{
		Set<Contact> attendees2 = new HashSet<Contact>();
		attendees2.add(new ContactImpl(1,"Bob","A bloke."));
		attendees2.add(new ContactImpl(2,"Fred","Another bloke."));
		
		Calendar date2 = new GregorianCalendar(1985,03,13);
		
		FutureMeeting meeting2 = new FutureMeetingImpl(2,date2,attendees2);
		
		assertFalse(meeting2.equals(testMeeting));
	}
	
	@Test
	public void testEqualsDifferentObject()
	{
		assertFalse(testMeeting.equals("An object"));
	}

}

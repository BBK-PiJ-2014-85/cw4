import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

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

}

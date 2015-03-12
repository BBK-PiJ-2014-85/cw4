package tests;


import impls.ContactImpl;
import impls.MeetingImpl;
import interfaces.Contact;
import interfaces.Meeting;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the MeetingImpl implementation of the interface Meeting.
 * 
 * @author Paul Day
 */

public class TestMeetingImpl {

	Meeting testMeeting;
	Set<Contact> attendees;
	
	@Before
	public void createTestMeeting()
	{
		attendees = new HashSet<Contact>();
		attendees.add(new ContactImpl(1,"Bob","A bloke."));
		attendees.add(new ContactImpl(2,"Fred","Another bloke."));
		
		Calendar date = new GregorianCalendar(1985,03,13);
		
		testMeeting = new MeetingImpl(1,date, attendees);
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the ContactManagerImpl implementation of interface ContactManager.
 * 	-Assumption on ID and constructors
 * 	-Clock used to fix time
 * 	-Multiple calendar dates chosen to ensure time is accurate
 * 
 * @author Paul Day
 *
 */

public class TestContactManagerImpl {

	final Calendar currentDate = new GregorianCalendar(2010,6,6,12,10,30);
	
	final Calendar futureDateYear= new GregorianCalendar(2011,5,5,11,9,28);
	final Calendar futureDateMonth= new GregorianCalendar(2010,7,5,11,9,28);
	final Calendar futureDateDay= new GregorianCalendar(2010,6,7,11,9,28);
	final Calendar futureDateHour= new GregorianCalendar(2010,6,6,13,9,28);
	final Calendar futureDateMinute= new GregorianCalendar(2010,6,6,12,11,28);
	final Calendar futureDateSecond= new GregorianCalendar(2010,6,6,12,11,31);
	
	final Calendar pastDateYear= new GregorianCalendar(2009,7,7,13,11,31);
	final Calendar pastDateMonth= new GregorianCalendar(2010,5,7,13,11,31);
	final Calendar pastDateDay= new GregorianCalendar(2010,6,5,13,11,31);
	final Calendar pastDateHour= new GregorianCalendar(2010,6,6,11,11,31);
	final Calendar pastDateMinute= new GregorianCalendar(2010,6,6,12,9,31);
	final Calendar pastDateSecond= new GregorianCalendar(2010,6,6,12,10,29);
	
	final File contactFile = new File("Contact.txt");
	final File cm1File = new File("cm1.txt");
	final File cm2File = new File("cm2.txt");
	final File cm3File = new File("cm3.txt");
	
	Contact c1, c2, c3;
	
	Set<Contact> contacts1, contacts2, contacts3;
	
	ContactManager cm, cm3Contacts, cm2Contacts, cm1Contacts;
	
	/*
	 * TODO: Test the notes1 notes2 etc works.
	 * TODO: Test a bad file returns with an exception.
	 * TODO: test int return on future meeting. point out assumption needing to be made for adding contacts and past meeting as id not returned although could search after
	 * TODO: Timezones.


	 */
	
	//Test reading and writing file
	
	public void copyFile(File from, File to)
	{
		Path pFrom = from.toPath();
		Path pTo = to.toPath();
		try {
			Files.copy(pFrom, pTo, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			fail("IOException found");
		}
	}
	
	public Contact getOnlyContactFromSet(Set<Contact> set)
	{
		if (set.size() == 0) fail("Set didn't return any contacts");
		else if (set.size() > 1) fail("Set returned more than one contact");
		else for(Contact con : set) return con;
		
		return null;
	}
	
	
	@Before 
	public void cleanStart()
	{
		Clock.setTime(currentDate);
		
		if (contactFile.exists()) contactFile.delete();
		if (cm1File.exists()) cm1File.delete();
		if (cm2File.exists()) cm2File.delete();
		if (cm3File.exists()) cm3File.delete();
		cm = new ContactManagerImpl();
		
		c1 = new ContactImpl(1,"Bob","Nice guy");
		c2 = new ContactImpl(2,"Fred","Talks too much");
		c3 = new ContactImpl(3, "Anon", "");
		
		contacts1 = new HashSet<Contact>();
		contacts1.add(c1);
		
		contacts2 = new HashSet<Contact>();
		contacts2.add(c1);
		contacts2.add(c2);
		
		contacts3 = new HashSet<Contact>();
		contacts3.add(c1);
		contacts3.add(c2);
		contacts3.add(c3);
		
		cm1Contacts = new ContactManagerImpl("cm1.txt");
		cm1Contacts.addNewContact("Bob","Nice guy");
		
		cm2Contacts = new ContactManagerImpl("cm2.txt");
		cm2Contacts.addNewContact("Bob","Nice guy");
		cm2Contacts.addNewContact("Fred","Talks too much");

		cm3Contacts = new ContactManagerImpl("cm3.txt");
		cm3Contacts.addNewContact("Bob","Nice guy");
		cm3Contacts.addNewContact("Fred","Talks too much");
		cm3Contacts.addNewContact("Anon","");
	}
	
	// Test reading and altering file correctly
	

	
	@Test 
	public void testFileCreatedIfNotExist() 
	{
		assertTrue(contactFile.exists());
	}

	@Test 
	public void testFileContactsReadCorrectlyEmptyContructor() 
	{
		ContactManager cm3TestContacts = new ContactManagerImpl("Contact.txt");
		cm3TestContacts.addNewContact("Bob","Nice guy");
		cm3TestContacts.addNewContact("Fred","Talks too much");
		cm3TestContacts.addNewContact("Anon","");
		cm3TestContacts.flush();
		cm3TestContacts = null;
		
		cm = new ContactManagerImpl();
		
		assertEquals(c1,getOnlyContactFromSet(cm.getContacts(1)));
		assertEquals(c2,getOnlyContactFromSet(cm.getContacts(2)));
		assertEquals(c3,getOnlyContactFromSet(cm.getContacts(3)));
		
	}
	
	@Test 
	public void testFileContactsReadCorrectlyStringContructor() 
	{
		ContactManager cm3TestContacts = new ContactManagerImpl("Contact.txt");
		cm3TestContacts.addNewContact("Bob","Nice guy");
		cm3TestContacts.addNewContact("Fred","Talks too much");
		cm3TestContacts.addNewContact("Anon","");
		cm3TestContacts.flush();
		cm3TestContacts = null;
		
		cm = new ContactManagerImpl("Contact.txt");
		
		assertEquals(c1,getOnlyContactFromSet(cm.getContacts(1)));
		assertEquals(c2,getOnlyContactFromSet(cm.getContacts(2)));
		assertEquals(c3,getOnlyContactFromSet(cm.getContacts(3)));
	}
	
	@Test 
	public void testFileContactsReadCorrectlyWithMeetings() 
	{
		ContactManager cm3TestContacts = new ContactManagerImpl();
		cm3TestContacts.addNewContact("Bob","Nice guy");
		cm3TestContacts.addNewContact("Fred","Talks too much");
		cm3TestContacts.addFutureMeeting(contacts2,futureDateMonth);
		cm3TestContacts.addNewPastMeeting(contacts2,pastDateMonth,"Some notes.");
		cm3TestContacts.addFutureMeeting(contacts2,futureDateDay);
		cm3TestContacts.addNewPastMeeting(contacts2,pastDateDay,"Some notes.");
		cm3TestContacts.addNewContact("Anon","");
		cm3TestContacts.addFutureMeeting(contacts2,futureDateYear);
		cm3TestContacts.addNewPastMeeting(contacts2,pastDateYear,"Some notes.");
		cm3TestContacts.flush();
		cm3TestContacts = null;
		
		cm = new ContactManagerImpl();
		
		assertEquals(c2,getOnlyContactFromSet(cm.getContacts(2)));
		assertEquals(c3,getOnlyContactFromSet(cm.getContacts(3)));
	}

	@Test 
	public void testFileMeetingsReadCorrectly() 
	{
		cm3Contacts.addFutureMeeting(contacts2,futureDateMonth);
		cm3Contacts.addNewPastMeeting(contacts2,pastDateMonth,"Some notes.");
		cm3Contacts.flush();
		cm3Contacts = null;
		
		cm = new ContactManagerImpl("cm3.txt");

		PastMeeting pm = new PastMeetingImpl(2,pastDateMonth,contacts2,"Some notes.");
		
		assertEquals(pm.getContacts(),cm.getMeeting(2).getContacts());
		
		cm.getMeeting(2).getDate().getTime();
		
		assertEquals(pm.getDate(),cm.getMeeting(2).getDate());
		
		
		
		assertEquals(new FutureMeetingImpl(1,futureDateMonth,contacts2),cm.getMeeting(1));
		assertEquals(new PastMeetingImpl(2,pastDateMonth,contacts2,"Some notes."),cm.getMeeting(2));
	}
	
	@Test 
	public void testFileReadNextContactIDCorrect() 
	{
		cm3Contacts.flush();
		cm3Contacts = null;
		
		Contact newContact = new ContactImpl(4,"Extra Added","Next contact");
		Set<Contact> newSet = new HashSet<Contact>();
		newSet.add(newContact);
		cm = new ContactManagerImpl("cm3.txt");
		cm.addNewContact(newContact.getName(), newContact.getNotes());
		
		assertEquals(newSet,cm.getContacts(4));
	}

	@Test 
	public void testFileReadNextFutureMeetingIDCorrect() 
	{
		cm3Contacts.addFutureMeeting(contacts2,futureDateMonth);
		cm3Contacts.flush();
		cm3Contacts = null;

		cm = new ContactManagerImpl("cm3.txt");
		cm.addFutureMeeting(contacts2,futureDateDay);
		
		assertEquals(new FutureMeetingImpl(2,futureDateDay,contacts2),cm.getMeeting(2));
	}
	
	@Test 
	public void testFileReadNextPastMeetingIDCorrect() 
	{
		cm3Contacts.addFutureMeeting(contacts2,futureDateMonth);
		cm3Contacts.flush();
		cm3Contacts = null;

		cm = new ContactManagerImpl("cm3.txt");
		cm.addNewPastMeeting(contacts2,pastDateDay,"Notes");
		
		assertEquals(new PastMeetingImpl(2,pastDateDay,contacts2,"Notes"),cm.getMeeting(2));
	}

	
	// Test addFutureMeeting()

	@Test(expected=IllegalArgumentException.class) 
	public void testAddFutureMeetingPastDateExceptionYear() {
		cm3Contacts.addFutureMeeting(contacts2,pastDateYear);
	}
	
	@Test(expected=IllegalArgumentException.class)  
	public void testAddFutureMeetingPastDateExceptionMonth() {
		cm3Contacts.addFutureMeeting(contacts2,pastDateMonth);
	}
	
	@Test(expected=IllegalArgumentException.class)  
	public void testAddFutureMeetingPastDateExceptionDay() {
		cm3Contacts.addFutureMeeting(contacts2,pastDateDay);
	}
	
	@Test(expected=IllegalArgumentException.class)  
	public void testAddFutureMeetingPastDateExceptionHour() {
		cm3Contacts.addFutureMeeting(contacts2,pastDateHour);
	}
	
	@Test(expected=IllegalArgumentException.class)  
	public void testAddFutureMeetingPastDateExceptionMinute() {
		cm3Contacts.addFutureMeeting(contacts2,pastDateMinute);
	}
	
	@Test(expected=IllegalArgumentException.class)  
	public void testAddFutureMeetingPastDateExceptionSecond() {
		cm3Contacts.addFutureMeeting(contacts2,pastDateSecond);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddFutureMeetingPastExceptionNoMeetingAdded() {
		cm3Contacts.addFutureMeeting(contacts2,pastDateMinute);
		assertTrue(cm3Contacts.getFutureMeetingList(pastDateMinute).isEmpty());
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testAddFutureMeetingUnknownContactNoContactsStoredException() {
		cm.addFutureMeeting(contacts2,futureDateYear);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testAddFutureMeetingUnknownContactContactsStoredException() {

		Set<Contact> solo = new HashSet<Contact>();
		solo.add(new ContactImpl(5,"Unknown guy","Dont take sweets from him."));
		cm3Contacts.addFutureMeeting(solo,futureDateYear);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testAddFutureMeetingUnknownContactNotFirstInListException() {
		cm2Contacts.addFutureMeeting(contacts3,futureDateYear);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testAddFutureMeetingEmptyContactsListException() {
		Set<Contact> empty = new HashSet<Contact>();
		cm2Contacts.addFutureMeeting(empty,futureDateYear);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddFutureMeetingNoMeetingAddedOnContactsException()
	{
		cm2Contacts.addFutureMeeting(contacts3,futureDateYear);
		assertTrue(cm2Contacts.getFutureMeetingList(futureDateYear).isEmpty());
	}
	
	@Test 
	public void testAddFutureMeetingIDReturnedFirst() {
		int num = cm2Contacts.addFutureMeeting(contacts1,futureDateYear);
		assertEquals(1,num);
	}
	
	@Test 
	public void testAddFutureMeetingIDReturnedSecond() {
		cm2Contacts.addFutureMeeting(contacts1,futureDateYear);
		int num = cm2Contacts.addFutureMeeting(contacts1,futureDateMonth);
		assertEquals(2,num);
	}
	
	@Test 
	public void testAddFutureMeetingContactFoundFirstInList() {
		cm2Contacts.addFutureMeeting(contacts1,futureDateYear);
	}
	
	@Test 
	public void testAddFutureMeetingContactFoundMultiple() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateYear);
	}
	
	@Test 
	public void testAddFutureMeetingStoredWhenNoCurrentMeetings() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateYear);
		ContactManager tempCM = new ContactManagerImpl("cm2.txt");
		assertEquals(new FutureMeetingImpl(1,futureDateYear,contacts2),tempCM.getFutureMeeting(1));
	}
	
	@Test 
	public void testAddFutureMeetingStoredWhenCurrentMeetings() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateYear);
		cm2Contacts.addFutureMeeting(contacts2,futureDateDay);
		ContactManager tempCM = new ContactManagerImpl("cm2.txt");
		assertEquals(new FutureMeetingImpl(2,futureDateDay,contacts2),tempCM.getFutureMeeting(2));
	}
	
	@Test public void testAddFutureMeetingFutureDateYear() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateYear);
		assertEquals(new MeetingImpl(1,futureDateYear,contacts2),cm2Contacts.getFutureMeeting(1));
	}
	@Test public void testAddFutureMeetingFutureDateMonth() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateMonth);
		assertEquals(new MeetingImpl(1,futureDateMonth,contacts2),cm2Contacts.getFutureMeeting(1));
	}
	@Test public void testAddFutureMeetingFutureDateDay() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateDay);
		assertEquals(new MeetingImpl(1,futureDateDay,contacts2),cm2Contacts.getFutureMeeting(1));
	}
	@Test public void testAddFutureMeetingFutureDateHour() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateHour);
		assertEquals(new MeetingImpl(1,futureDateHour,contacts2),cm2Contacts.getFutureMeeting(1));
	}
	@Test public void testAddFutureMeetingFutureDateMinute() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateMinute);
		assertEquals(new MeetingImpl(1,futureDateMinute,contacts2),cm2Contacts.getFutureMeeting(1));
	}
	@Test public void testAddFutureMeetingFutureDateSecond() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateSecond);
		assertEquals(new MeetingImpl(1,futureDateSecond,contacts2),cm2Contacts.getFutureMeeting(1));
	}	
	
	// Test ID increments for meeting from addFutureMeeting()
	
	@Test public void testAddFutureMeetingIDFirst() 
	{
		assertNull(cm2Contacts.getFutureMeeting(1));
		cm2Contacts.addFutureMeeting(contacts2,futureDateDay);
		assertNotNull(cm2Contacts.getFutureMeeting(1));
	}
	
	@Test public void testAddFutureMeetingIDSecond() {
		assertNull(cm2Contacts.getFutureMeeting(2));
		cm2Contacts.addFutureMeeting(contacts2,futureDateDay);
		cm2Contacts.addFutureMeeting(contacts2,futureDateMonth);
		assertNotNull(cm2Contacts.getFutureMeeting(2));
	}
	
	@Test public void testAddFutureMeetingIDSecondOnLoad() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateDay);
		ContactManager tempCM = new ContactManagerImpl("cm2.txt");
		assertNull(tempCM.getFutureMeeting(2));
		tempCM.addFutureMeeting(contacts2,futureDateMonth);
		assertNotNull(tempCM.getFutureMeeting(2));
	}
	
	// Test getPastMeeting()
	
	@Test(expected=IllegalArgumentException.class) 
	public void testGetPastMeetingFutureIdExistsExceptionFirst() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateDay);
		cm2Contacts.getPastMeeting(1);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testGetPastMeetingFutureIdExistsExceptionSecond() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateDay);
		cm2Contacts.addFutureMeeting(contacts2,futureDateMonth);
		cm2Contacts.getPastMeeting(2);
	}

	@Test 
	public void testGetPastMeetingFutureMeetingWhenAddedNowPast() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateDay);
		
		Clock.setTime(new GregorianCalendar(2050,01,01));
		
		assertEquals(new PastMeetingImpl(new MeetingImpl(1,futureDateDay,contacts2),""),cm2Contacts.getPastMeeting(1));
	}
	
	@Test 
	public void testGetPastMeetingNotExist() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateDay);
		assertNull(cm2Contacts.getPastMeeting(2));		
	}
	
	@Test 
	public void testGetPastMeetingListEmpty() {
		assertNull(cm2Contacts.getPastMeeting(2));
	}
	
	@Test 
	public void testGetPastMeetingFirstItemInList() {
		cm2Contacts.addNewPastMeeting(contacts2,pastDateDay,"First meeting in list");
		cm2Contacts.addNewPastMeeting(contacts1,pastDateMonth,"Second meeting in list");
		
		PastMeeting pm = cm2Contacts.getPastMeeting(1);
		assertEquals(pastDateDay,pm.getDate());
		assertEquals(contacts2,pm.getContacts());
	}
	
	@Test 
	public void testGetPastMeetingSecondItemInList() {
		cm2Contacts.addNewPastMeeting(contacts2,pastDateDay,"First meeting in list");
		cm2Contacts.addNewPastMeeting(contacts1,pastDateMonth,"Second meeting in list");
		
		PastMeeting pm = cm2Contacts.getPastMeeting(2);
		assertEquals(pastDateMonth,pm.getDate());
		assertEquals(contacts1,pm.getContacts());
	}
	
	@Test 
	public void testGetPastMeetingSameDayButPast() {
		cm2Contacts.addNewPastMeeting(contacts2,pastDateSecond,"First meeting in list");
		
		PastMeeting pm = cm2Contacts.getPastMeeting(1);
		assertEquals(pastDateSecond,pm.getDate());
		assertEquals(contacts2,pm.getContacts());
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testGetPastMeetingSameDayButFuture() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateSecond);
		cm2Contacts.getPastMeeting(1);
	}
	
	// Test getFutureMeeting()
	
	@Test(expected=IllegalArgumentException.class)  
	public void testGetFutureMeetingFutureIdExistsExceptionFirst() {
		cm2Contacts.addNewPastMeeting(contacts2, pastDateDay, "First Meeting in past");
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMonth, "Second Meeting in past");
		cm2Contacts.getFutureMeeting(1);
	}
	
	@Test(expected=IllegalArgumentException.class)  
	public void testGetFutureMeetingFutureIdExistsExceptionSecond() {
		cm2Contacts.addNewPastMeeting(contacts2, pastDateDay, "First Meeting in past");
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMonth, "Second Meeting in past");
		cm2Contacts.getFutureMeeting(2);
	}
	
	@Test(expected=IllegalArgumentException.class)  
	public void testGetFutureMeetingMeetingWasFutureNowInPastException() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateDay);
		
		Clock.setTime(new GregorianCalendar(2050,01,01));
		cm2Contacts.getFutureMeeting(1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetFutureMeetingThrowsExceptionForPastMeetingInFuture()
	{
		cm3Contacts.addNewPastMeeting(contacts2,futureDateMonth,"Notes");
		cm3Contacts.getFutureMeeting(1);
	}
	
	@Test 
	public void testGetFutureMeetingNotExist() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateDay);
		
		assertNull(cm2Contacts.getFutureMeeting(2));
	}
	
	@Test 
	public void testGetFutureMeetingListEmpty() {
		assertNull(cm2Contacts.getFutureMeeting(2));
	}
	
	@Test 
	public void testGetFutureMeetingFirstItemInList() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateDay);
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		
		assertEquals(contacts2,cm2Contacts.getFutureMeeting(1).getContacts());
		assertEquals(futureDateDay,cm2Contacts.getFutureMeeting(1).getDate());
	}
	
	@Test 
	public void testGetFutureMeetingSecondItemInList() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateDay);
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		
		assertEquals(contacts2,cm2Contacts.getFutureMeeting(2).getContacts());
		assertEquals(futureDateMonth,cm2Contacts.getFutureMeeting(2).getDate());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetFutureMeetingSameDayButPast() {
		cm2Contacts.addFutureMeeting(contacts2, pastDateSecond);
		cm2Contacts.getFutureMeeting(1);
	}
	
	@Test
	public void testGetFutureMeetingSameDayButFuture() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateSecond);
		assertEquals(contacts2,cm2Contacts.getFutureMeeting(1).getContacts());
		assertEquals(futureDateSecond,cm2Contacts.getFutureMeeting(1).getDate());
	}
	
	// Test getMeeting()
	
	@Test 
	public void testGetMeetingNotExist() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateSecond);
		assertNull(cm2Contacts.getMeeting(2));
	}
	
	@Test 
	public void testGetMeetingListEmpty() {
		assertNull(cm2Contacts.getMeeting(2));
	}
	
	@Test 
	public void testGetMeetingFirstItemInList() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateDay);
		assertEquals(contacts2, cm2Contacts.getMeeting(1).getContacts());
		assertEquals(futureDateDay, cm2Contacts.getMeeting(1).getDate());
	}
	
	@Test 
	public void testGetMeetingPastSecondItemInList() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateDay);
		cm2Contacts.addNewPastMeeting(contacts2, pastDateDay, "Past Meeting");
		assertEquals(contacts2, cm2Contacts.getMeeting(2).getContacts());
		assertEquals(pastDateDay, cm2Contacts.getMeeting(2).getDate());
		assertEquals("Past Meeting", ((PastMeeting)cm2Contacts.getMeeting(2)).getNotes());
	}
	
	@Test 
	public void testGetMeetingFutureSecondItemInList() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateDay);
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		assertEquals(contacts2, cm2Contacts.getMeeting(2).getContacts());
		assertEquals(futureDateMonth, cm2Contacts.getMeeting(2).getDate());
	}
	
	// Test getFutureMeetingList(Contact)
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetFutureListContactNotExistException() {
		cm2Contacts.getFutureMeetingList(c3);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetFutureListNoContactsExistException() {
		cm.getFutureMeetingList(c3);
	}
	
	@Test 
	public void testGetFutureListContactPastMeetingsOnlyExist() {
		cm2Contacts.addNewPastMeeting(contacts2, pastDateDay, "Past Meeting 1");
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMonth, "Past Meeting 2");
		
		assertTrue(cm2Contacts.getFutureMeetingList(c1).isEmpty());
	}
	
	@Test 
	public void testGetFutureListContactFutureMeetingWhenAddedNowPast() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateDay);
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		
		Clock.setTime(new GregorianCalendar(2050,01,01));
		
		assertTrue(cm2Contacts.getFutureMeetingList(c1).isEmpty());
		
	}
	
	@Test 
	public void testGetFutureListContactFutureMeetingsOnlyExist() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateDay);
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		
		List<Meeting> rtn = cm2Contacts.getFutureMeetingList(c1);
		
		assertEquals(2,rtn.size());
		assertEquals(futureDateDay, rtn.get(0).getDate());
		assertEquals(contacts2, rtn.get(0).getContacts());
		assertEquals(futureDateMonth, rtn.get(1).getDate());
		assertEquals(contacts2, rtn.get(1).getContacts());
		
	}
	
	@Test 
	public void testGetFutureListContactNoMeetingsExist() {
		cm3Contacts.addFutureMeeting(contacts2, futureDateDay);
		cm3Contacts.addFutureMeeting(contacts1, futureDateMonth);
		
		assertTrue(cm3Contacts.getFutureMeetingList(c3).isEmpty());
	}
	
	@Test 
	public void testGetFutureListContactDoesntReturnPastMeetingInFuture() {

		cm3Contacts.addNewPastMeeting(contacts2, futureDateDay,"Notes");
		List<Meeting> testList = new ArrayList<Meeting>();
		testList.add(new PastMeetingImpl(1,futureDateDay,contacts2,"Notes"));
		assertTrue(cm3Contacts.getFutureMeetingList(c2).containsAll(testList));
		assertEquals(1,cm3Contacts.getFutureMeetingList(c2).size());
	}
	
	@Test 
	public void testGetFutureListContactFutureAndPastMeetingsExist() {
		cm3Contacts.addFutureMeeting(contacts2, futureDateDay);
		cm3Contacts.addNewPastMeeting(contacts2, pastDateMonth, "A past meeting");
		cm3Contacts.addFutureMeeting(contacts3, futureDateMonth);
		
		List<Meeting> rtn = cm3Contacts.getFutureMeetingList(c2);
		assertEquals(futureDateDay, rtn.get(0).getDate());
		assertEquals(contacts2, rtn.get(0).getContacts());
		assertEquals(futureDateMonth, rtn.get(1).getDate());
		assertEquals(contacts3, rtn.get(1).getContacts());
		assertEquals(2,rtn.size());
	}
	
	@Test 
	public void testGetFutureListContactOnlyContactMatchedMeetingsReturned() {
		cm3Contacts.addFutureMeeting(contacts2, futureDateDay);
		cm3Contacts.addFutureMeeting(contacts3, futureDateMonth);
		cm3Contacts.addFutureMeeting(contacts3, futureDateYear);
		
		List<Meeting> rtn = cm3Contacts.getFutureMeetingList(c3);
		assertEquals(2,rtn.size());
		assertEquals(futureDateMonth, rtn.get(0).getDate());
		assertEquals(contacts3, rtn.get(0).getContacts());
		assertEquals(futureDateYear, rtn.get(1).getDate());
		assertEquals(contacts3, rtn.get(1).getContacts());

	}
	
	@Test 
	public void testGetFutureListContactListSortedChronologically() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		cm2Contacts.addFutureMeeting(contacts2, futureDateDay);
		cm2Contacts.addFutureMeeting(contacts2, futureDateYear);
		
		List<Meeting> rtn = cm2Contacts.getFutureMeetingList(c2);
		assertEquals(futureDateDay, rtn.get(0).getDate());
		assertEquals(contacts2, rtn.get(0).getContacts());
		assertEquals(futureDateMonth, rtn.get(1).getDate());
		assertEquals(contacts2, rtn.get(1).getContacts());
		assertEquals(futureDateYear, rtn.get(2).getDate());
		assertEquals(contacts2, rtn.get(2).getContacts());
		assertEquals(3,rtn.size());
	}
	
	@Test
	public void testGetFutureListFirstContactMatch()
	{
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		List<Meeting> rtn = cm2Contacts.getFutureMeetingList(c1);
		
		assertEquals(1,rtn.size());
		assertEquals(futureDateMonth, rtn.get(0).getDate());
		assertEquals(contacts2, rtn.get(0).getContacts());
	}
	
	@Test
	public void testGetFutureListFirstSecondMatch()
	{
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		List<Meeting> rtn = cm2Contacts.getFutureMeetingList(c2);
		
		assertEquals(1,rtn.size());
		assertEquals(futureDateMonth, rtn.get(0).getDate());
		assertEquals(contacts2, rtn.get(0).getContacts());
	}
	
	// Test getFutureMeetingList(Calendar)

	@Test 
	public void testGetFutureListDateNoMeetingsMatch() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		cm2Contacts.addFutureMeeting(contacts2, futureDateDay);
		
		assertTrue(cm2Contacts.getFutureMeetingList(futureDateYear).isEmpty());
	}
	
	@Test
	public void testGetFutureListDateNoMeetings()
	{
		assertTrue(cm2Contacts.getFutureMeetingList(futureDateYear).isEmpty());
	}
	
	@Test 
	public void testGetFutureListDateTodayBothPastAndFuture() {
		cm2Contacts.addNewPastMeeting(contacts2,pastDateMonth, "Old meeting");
		cm2Contacts.addNewPastMeeting(contacts1, pastDateMinute, "A recent past meeting");
		cm2Contacts.addFutureMeeting(contacts2, futureDateMinute);
		cm2Contacts.addFutureMeeting(contacts1, futureDateHour);
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);

		List<Meeting> rtn = cm2Contacts.getFutureMeetingList(pastDateMinute);
		assertEquals(3, rtn.size());
		
		assertEquals(pastDateMinute, rtn.get(0).getDate());
		assertEquals(contacts1, rtn.get(0).getContacts());
		assertEquals(futureDateMinute, rtn.get(1).getDate());
		assertEquals(contacts2, rtn.get(1).getContacts());
		assertEquals(futureDateHour, rtn.get(2).getDate());
		assertEquals(contacts1, rtn.get(2).getContacts());
	}
	
	@Test 
	public void testGetFutureListDateSortedChronological() {
		cm2Contacts.addFutureMeeting(contacts1, futureDateHour);
		cm2Contacts.addNewPastMeeting(contacts2,pastDateMonth, "Old meeting");
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		cm2Contacts.addFutureMeeting(contacts2, futureDateMinute);
		cm2Contacts.addNewPastMeeting(contacts1, pastDateMinute, "A recent past meeting");

		List<Meeting> rtn = cm2Contacts.getFutureMeetingList(pastDateMinute);
		assertEquals(3, rtn.size());
		
		assertEquals(pastDateMinute, rtn.get(0).getDate());
		assertEquals(contacts1, rtn.get(0).getContacts());
		assertEquals(futureDateMinute, rtn.get(1).getDate());
		assertEquals(contacts2, rtn.get(1).getContacts());
		assertEquals(futureDateHour, rtn.get(2).getDate());
		assertEquals(contacts1, rtn.get(2).getContacts());
	}
	
	@Test 
	public void testGetFutureListDateRemoveDuplicateTime(){
		//Assuming that if a meeting is same time and contacts list then is the same (i.e. IDs can be different)
		cm3Contacts.addFutureMeeting(contacts2, futureDateMinute);
		cm3Contacts.addFutureMeeting(contacts2, futureDateMinute);
		cm3Contacts.addFutureMeeting(contacts2, futureDateHour);
		
		List<Meeting> rtn = cm3Contacts.getFutureMeetingList(futureDateMinute);
		
		System.out.println(rtn.size());
		
		//two lists required as not specified which duplicate to take, so need to check if either
		List<Meeting> list1 = new ArrayList<Meeting>();
		list1.add(new FutureMeetingImpl(1,futureDateMinute,contacts2));
		list1.add(new FutureMeetingImpl(3,futureDateHour,contacts2));
		
		List<Meeting> list2 = new ArrayList<Meeting>();
		list1.add(new FutureMeetingImpl(2,futureDateMinute,contacts2));
		list1.add(new FutureMeetingImpl(3,futureDateHour,contacts2));
		
		assertEquals(2,rtn.size());
		assertTrue(rtn.containsAll(list1) || rtn.containsAll(list2));
	}
	
	@Test 
	public void testGetFutureListReturnsPastMeetingInFuture(){
		cm3Contacts.addNewPastMeeting(contacts2, futureDateDay, "Notes");
		List<Meeting> testList = new ArrayList<Meeting>();
		testList.add(new PastMeetingImpl(1,futureDateDay,contacts2,"Notes"));
		assertTrue(cm3Contacts.getFutureMeetingList(futureDateDay).containsAll(testList));
		assertEquals(1,cm3Contacts.getFutureMeetingList(futureDateDay).size());

	}
	
	// Test getPastMeetingList(Contact)
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetPastListContactNotExistException() {
		cm2Contacts.getPastMeetingList(c3);
	}
	
	@Test 
	public void testGetPastListContactPastMeetingsOnlyExist() {
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMonth, "Past meeting a month ago.");
		cm2Contacts.addNewPastMeeting(contacts2, pastDateDay, "Past meeting a day ago.");
		
		List<PastMeeting> rtn =  cm2Contacts.getPastMeetingList(c2);
		
		assertEquals(2, rtn.size());
		
		assertEquals(contacts2, rtn.get(0).getContacts());
		assertEquals(pastDateMonth, rtn.get(0).getDate());
		
		assertEquals(contacts2, rtn.get(1).getContacts());
		assertEquals(pastDateDay, rtn.get(1).getDate());
	}
	
	@Test
	public void testGetPastListContactMatchedFutureAndPastExist()
	{
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMonth, "Past meeting a day ago.");
		cm2Contacts.addFutureMeeting(contacts2, futureDateYear);
		cm2Contacts.addNewPastMeeting(contacts2, pastDateDay, "Past meeting a month ago.");
		
		List<PastMeeting> rtn =  cm2Contacts.getPastMeetingList(c2);
		
		assertEquals(2, rtn.size());
		
		assertEquals(contacts2, rtn.get(0).getContacts());
		assertEquals(pastDateMonth, rtn.get(0).getDate());
		
		assertEquals(contacts2, rtn.get(1).getContacts());
		assertEquals(pastDateDay, rtn.get(1).getDate());
	}
	
	@Test
	public void testGetPastListContactSomeMatchFutureAndPast()
	{
		cm2Contacts.addFutureMeeting(contacts1, futureDateMonth);
		cm2Contacts.addNewPastMeeting(contacts2, pastDateDay, "Past meeting a day ago.");
		cm2Contacts.addFutureMeeting(contacts2, futureDateYear);
		cm2Contacts.addNewPastMeeting(contacts1, pastDateMonth, "Past meeting a month ago.");
		
		List<PastMeeting> rtn =  cm2Contacts.getPastMeetingList(c2);
		
		assertEquals(1, rtn.size());
		
		assertEquals(contacts2, rtn.get(0).getContacts());
		assertEquals(pastDateDay, rtn.get(0).getDate());
	}
	
	@Test
	public void testGetPastListContactFutureMeetingsOnlyExist() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateYear);
		
		assertTrue(cm2Contacts.getPastMeetingList(c2).isEmpty());
	}
	
	@Test 
	public void testGetPastListContactNoMeetingsExist() {
		assertTrue(cm2Contacts.getPastMeetingList(c2).isEmpty());
	}
	
	@Test 
	public void testGetPastListContactMeetingsWithNoContactMatchExist() {
		cm2Contacts.addNewPastMeeting(contacts1, pastDateDay, "Past meeting a day ago.");
		
		assertTrue(cm2Contacts.getPastMeetingList(c2).isEmpty());
	}
	
	@Test 
	public void testGetPastListContactMeetingsWithFirstContactMatched() {
		cm2Contacts.addNewPastMeeting(contacts2, pastDateDay, "Past meeting a day ago.");
		
		List<PastMeeting> rtn = cm2Contacts.getPastMeetingList(c1);
		
		assertEquals(1, rtn.size());
		
		assertEquals(contacts2, rtn.get(0).getContacts());
		assertEquals(pastDateDay, rtn.get(0).getDate());
	}
	
	@Test 
	public void testGetPastListContactMeetingsWithSecondContactMatched() {
		cm2Contacts.addNewPastMeeting(contacts2, pastDateDay, "Past meeting a day ago.");
		
		List<PastMeeting> rtn = cm2Contacts.getPastMeetingList(c2);
		
		assertEquals(1, rtn.size());
		
		assertEquals(contacts2, rtn.get(0).getContacts());
		assertEquals(pastDateDay, rtn.get(0).getDate());
	}
	
	@Test 
	public void testGetPastListContactListSortedChronologically() {
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMonth, "Past meeting a day ago.");
		cm2Contacts.addNewPastMeeting(contacts2, pastDateDay, "Past meeting a day ago.");
		cm2Contacts.addNewPastMeeting(contacts2, pastDateYear, "Past meeting a day ago.");
		
		List<PastMeeting> rtn = cm2Contacts.getPastMeetingList(c2);
	
		assertEquals(3, rtn.size());
		
		assertEquals(contacts2, rtn.get(0).getContacts());
		assertEquals(pastDateYear, rtn.get(0).getDate());
		
		assertEquals(contacts2, rtn.get(1).getContacts());
		assertEquals(pastDateMonth, rtn.get(1).getDate());
		
		assertEquals(contacts2, rtn.get(2).getContacts());
		assertEquals(pastDateDay, rtn.get(2).getDate());
	}
	
	@Test
	public void testGetPastListContactFutureTurnsToPast()
	{
		cm2Contacts.addFutureMeeting(contacts1, futureDateMonth);
		
		Clock.setTime(new GregorianCalendar(2050,01,01));
		
		List<PastMeeting> rtn = cm2Contacts.getPastMeetingList(c1);
		
		assertEquals(1, rtn.size());
		
		assertEquals(contacts1, rtn.get(0).getContacts());
		assertEquals(futureDateMonth, rtn.get(0).getDate());
	}
	
	@Test
	public void testGetPastListContactNoDuplicates()
	{
		//assume duplicates means everything same apart from ID
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMinute, "Past meeting");
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMinute, "Past meeting");
		cm2Contacts.addNewPastMeeting(contacts2, pastDateHour, "Past meeting");
		
		List<PastMeeting> rtn = cm2Contacts.getPastMeetingList(c1);
		List<PastMeeting> list1 = new ArrayList<PastMeeting>();
		list1.add(new PastMeetingImpl(1,pastDateMinute,contacts2, "Past meeting"));
		list1.add(new PastMeetingImpl(3,pastDateHour,contacts2, "Past meeting"));
		
		List<PastMeeting> list2 = new ArrayList<PastMeeting>();
		list1.add(new PastMeetingImpl(2,pastDateMinute,contacts2, "Past meeting"));
		list1.add(new PastMeetingImpl(3,pastDateHour,contacts2, "Past meeting"));
		
		assertEquals(2,rtn.size());
		assertTrue(rtn.containsAll(list1) || rtn.containsAll(list2));	
	}
	
	// Test addNewPastMeeting()
	
	@Test(expected=NullPointerException.class)
	public void testAddPastMeetingContactsNullException() {
		cm2Contacts.addNewPastMeeting(null, pastDateMonth, "null contacts");
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddPastMeetingContactsNullExceptionNoMeetingAdded() {
		cm2Contacts.addNewPastMeeting(null, pastDateMonth, "null contacts");
		assertTrue(cm2Contacts.getFutureMeetingList(pastDateMonth).isEmpty()); //getFutureMeetingList(Calendar) returns past meetings
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddPastMeetingDateNullException() {
		cm2Contacts.addNewPastMeeting(contacts1, null, "null date");
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddPastMeetingNotesNullException() {
		cm2Contacts.addNewPastMeeting(contacts1, pastDateMonth, null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddPastMeetingNotesNullExceptionNoMeetingAdded() {
		cm2Contacts.addNewPastMeeting(contacts1, pastDateMonth, null);
		assertTrue(cm2Contacts.getFutureMeetingList(pastDateMonth).isEmpty()); //getFutureMeetingList(Calendar) returns past meetings
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddPastMeetingFirstContactNotExist() {
		Set<Contact> outsider = new HashSet<Contact>();
		outsider.add(c3);
		cm2Contacts.addNewPastMeeting(outsider, pastDateMonth, "Dont know who they guy who turned up was");
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testAddPastMeetingSecondContactNotExist() {

		cm1Contacts.addNewPastMeeting(contacts2, pastDateMonth, "Didn't know who his mate was");
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testAddPastMeetingContactListEmpty() {
		Set<Contact> noone = new HashSet<Contact>();
		cm1Contacts.addNewPastMeeting(noone, pastDateMonth, "If noone was there, did it happen?");
	}
	
	@Test 
	public void testAddPastMeetingDateInFutureWorks() {
		//Spec doesn't have an exception for adding pastMeeting in the Future, so here I have checked it is added as required 
		
		cm3Contacts.addNewPastMeeting(contacts2, futureDateMonth, "Psychic notes!");
		assertEquals(new PastMeetingImpl(1,futureDateMonth, contacts2,"Psychic notes!"),cm3Contacts.getMeeting(1));
	}
	
	
	@Test 
	public void testAddPastMeetingFirstID() {
		
		assertNull(cm2Contacts.getMeeting(1));
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMonth, "First meeting");
		assertNotNull(cm2Contacts.getMeeting(1));
	}
	
	@Test 
	public void testAddPastMeetingSecondIDAfterPastMeetingAddedFirst() {	
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMonth, "First meeting");
		assertNull(cm2Contacts.getMeeting(2));
		cm2Contacts.addNewPastMeeting(contacts2, pastDateYear, "Second meeting");
		assertNotNull(cm2Contacts.getMeeting(2));
	}
	
	@Test 
	public void testAddPastMeetingSecondIDAfterFutureMeetingAddedFirst() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		assertNull(cm2Contacts.getMeeting(2));
		cm2Contacts.addNewPastMeeting(contacts2, pastDateYear, "Second meeting");
		assertNotNull(cm2Contacts.getMeeting(2));
	}
	
	@Test 
	public void testAddPastMeetingFirst() {
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMonth, "First meeting");
		assertEquals(pastDateMonth, cm2Contacts.getMeeting(1).getDate());
		assertEquals(contacts2,cm2Contacts.getMeeting(1).getContacts());
	}
	
	@Test 
	public void testAddPastMeetingEmptyNotes() {
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMonth, "");
		assertEquals(pastDateMonth, cm2Contacts.getMeeting(1).getDate());
		assertEquals(contacts2,cm2Contacts.getMeeting(1).getContacts());
		assertEquals("",((PastMeeting)cm2Contacts.getMeeting(1)).getNotes());
	}
	
	@Test 
	public void testAddPastMeetingSecond() {
		cm2Contacts.addNewPastMeeting(contacts2, pastDateDay, "First meeting");
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMonth, "Second meeting");
		assertEquals(pastDateMonth, cm2Contacts.getMeeting(2).getDate());
		assertEquals(contacts2,cm2Contacts.getMeeting(2).getContacts());
	}
	
	@Test 
	public void testAddPastMeetingFileUpdated() {
		cm2Contacts.addNewPastMeeting(contacts2, pastDateDay, "First meeting");
		
		ContactManager cmTest = new ContactManagerImpl("cm2.txt");
		assertEquals(new PastMeetingImpl(1,pastDateDay,contacts2,"First meeting"),cmTest.getMeeting(1));
	}
	
	// Test addMeetingNotes()

	@Test(expected=IllegalArgumentException.class)
	public void testAddMeetingNotesMeetingNotExistException() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		cm2Contacts.addMeetingNotes(2,"Meeting didnt exist");
	}
	
	@Test(expected=IllegalStateException.class)
	public void testAddMeetingNotesDateFutureException() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		cm2Contacts.addMeetingNotes(1,"Meeting hadnt yet happened");
	}
	
	
	@Test(expected=NullPointerException.class) 
	public void testAddMeetingNotesNotesNullException() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		Clock.setTime(new GregorianCalendar(2050,01,01));
		
		cm2Contacts.addMeetingNotes(1,null);
	}
	
	@Test 
	public void testAddMeetingNotesMeetingNotesEmptyFirstMeeting() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		Clock.setTime(new GregorianCalendar(2050,01,01));
		
		cm2Contacts.addMeetingNotes(1,"That was fun");
		
		assertEquals("That was fun", ((PastMeeting)cm2Contacts.getMeeting(1)).getNotes());
	}
	
	@Test 
	public void testAddMeetingNotesMeetingNotesEmptySecondMeeting() {
		cm2Contacts.addFutureMeeting(contacts2, futureDateYear);
		cm2Contacts.addFutureMeeting(contacts2, futureDateMonth);
		Clock.setTime(new GregorianCalendar(2050,01,01));
		
		cm2Contacts.addMeetingNotes(2,"That was fun");
		
		assertEquals("That was fun", ((PastMeeting)cm2Contacts.getMeeting(2)).getNotes());
	}
	
	@Test 
	public void testAddMeetingNotesOverwritesNotes() {
		//Assumption that this overwrites notes, as no exception is returned for not doing so
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMonth,"Original notes.");
		cm2Contacts.addMeetingNotes(1, "Changed notes.");
		assertEquals("Changed notes.",cm2Contacts.getPastMeeting(1).getNotes());
	}
	
	@Test 
	public void testAddMeetingNotesFileUpdated() {
		cm2Contacts.addNewPastMeeting(contacts2, pastDateMonth,"Some notes.");
		cm2Contacts.addMeetingNotes(1, "Changed notes.");
		
		ContactManagerImpl cmTemp = new ContactManagerImpl("cm2.txt");
		assertEquals("Changed notes.",cmTemp.getPastMeeting(1).getNotes());
	}

	// Test addNewContact()
	
	@Test(expected=NullPointerException.class)
	public void testAddNewContactNameNullException() {
		cm2Contacts.addNewContact(null, "Little to say about him");
	}

	
	@Test(expected=NullPointerException.class) 
	public void testAddNewContactNotesNullException() {
		cm2Contacts.addNewContact("Bob", null);
	}
	
	@Test(expected=NullPointerException.class) 
	public void testAddNewContactNotesNullExceptionContactNotAdded() {
		cm2Contacts.addNewContact("Unique Name", null);
		assertTrue(cm2Contacts.getContacts("Unique Name").isEmpty());
		
	}
	
	@Test 
	public void testAddNewContactFirstContactIDNum() {
		
		cm.addNewContact("Bob","First");
		cm.getContacts(1);
	}
	
	@Test 
	public void testAddNewContactSecondContactIDNum() {
		cm1Contacts.addNewContact("Bob","Second");
		cm1Contacts.getContacts(2);
	}
	
	@Test 
	public void testAddNewContactValueFirst() {
		cm.addNewContact(c1.getName(),c1.getNotes());
		Set<Contact> ct = cm.getContacts(1);
		
		assertEquals(1,ct.size());
	    
		assertTrue(ct.contains(c1));
	}
	
	@Test 
	public void testAddNewContactValueSecond() {
		cm1Contacts.addNewContact(c2.getName(),c2.getNotes());
		Set<Contact> ct = cm1Contacts.getContacts(2);
		
		assertEquals(1,ct.size());
	    
		assertTrue(ct.contains(c2));
	}
	
	@Test 
	public void testAddNewContactValueWithQuotations() {
		Contact quoted = new ContactImpl(2, "Shayne \"The Pain \" Name", "He's not the only one who doesn't like his nickname");
		cm1Contacts.addNewContact(quoted.getName(),quoted.getNotes());
		
		Set<Contact> ct = cm1Contacts.getContacts(2);
		assertEquals(1,ct.size());
		assertTrue(ct.contains(quoted));
	}
	
	@Test 
	public void testAddNewContactFileUpdatedFirst() {
		cm.addNewContact("Bob", "Unoriginal parents.");
		ContactManager cmTemp = new ContactManagerImpl("Contact.txt");
		Set<Contact> set = new HashSet<Contact>();
		set.add(new ContactImpl(1,"Bob","Unoriginal parents."));
		assertEquals(set,cmTemp.getContacts(1));
	}
	
	@Test 
	public void testAddNewContactFileUpdatedSecond() {
		cm1Contacts.addNewContact("Bob", "Unoriginal parents.");
		ContactManager cmTemp = new ContactManagerImpl("cm1.txt");
		Set<Contact> set = new HashSet<Contact>();
		set.add(new ContactImpl(2,"Bob","Unoriginal parents."));
		assertEquals(set,cmTemp.getContacts(2));
	}
	
	// Test getContacts(int)
	
	@Test 
	public void testGetContactsIntOneParameterFirst() {
		Set<Contact> ct = cm2Contacts.getContacts(1);
		
		assertEquals(1,ct.size());
		assertTrue(ct.contains(c1));
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testGetContactsIntOneParameterNotExist() {
		cm2Contacts.getContacts(3);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testGetContactsIntOneParameterNotExistZeroID() {
		cm2Contacts.getContacts(0);
	}
	
	@Test 
	public void testGetContactsIntOneParameterSecond() {
		Set<Contact> ct = cm2Contacts.getContacts(2);
		
		assertEquals(1,ct.size());
		assertTrue(ct.contains(c2));
	}

	
	@Test 
	public void testGetContactsIntMultipleParametersFirstInListIncluded() {
		Set<Contact> ct = cm3Contacts.getContacts(1,2,3);
		
		assertEquals(3,ct.size());
		assertTrue(ct.contains(c1));
		assertTrue(ct.contains(c2));
		assertTrue(ct.contains(c3));
	}
	
	@Test 
	public void testGetContactsIntMultipleParametersFirstInListNotIncluded() {
		Set<Contact> ct = cm3Contacts.getContacts(2,3);
		
		assertEquals(2,ct.size());
		assertTrue(ct.contains(c2));
		assertTrue(ct.contains(c3));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetContactsFirstParameterNotExist() {
		Set<Contact> ct = cm3Contacts.getContacts(4,2,3);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testGetContactsSecondParameterNotExist() {
		Set<Contact> ct = cm3Contacts.getContacts(1,4,3);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetContactsNoParameters()
	{
		cm2Contacts.getContacts();
	}
	
	// Test getContacts(String)

	@Test(expected=NullPointerException.class) 
	public void testGetContactsStringNullParamterException() {
		String nullString = null;
		cm3Contacts.getContacts(nullString);
	}

	@Test(expected=NullPointerException.class)
	public void testGetContactsEmptyString() {
	//Stated in forum to treat this same as null
		Set<Contact> ct = cm3Contacts.getContacts("");
	}
	
	@Test 
	public void testGetContactsStringMatchFirstContact() {
		Set<Contact> ct = cm2Contacts.getContacts("ob");
		
		assertEquals(1,ct.size());
		assertTrue(ct.contains(c1));
	}
	
	@Test 
	public void testGetContactsStringMatchSecondContact() {
		Set<Contact> ct = cm2Contacts.getContacts("re");
		
		assertEquals(1,ct.size());
		assertTrue(ct.contains(c2));
	}
	
	@Test 
	public void testGetContactsStringLongerSoNoMatch() {
		Set<Contact> ct = cm2Contacts.getContacts("Bobs");
		
		assertEquals(0,ct.size());
	}
	
	@Test 
	public void testGetContactStringMatchMultipleIncludingFirst() {
		
		Contact first = new ContactImpl(1,"Mrs White", "Had a candlestick");
		Contact second = new ContactImpl(2,"Mr Brown", "Was not there");
		Contact third = new ContactImpl(3,"Miss Scarlet", "Was in the drawing room");
		Contact fourth = new ContactImpl(4,"Mr Plum", "Felt aggrevated by not being called by his proper title");
		
		cm.addNewContact(first.getName(), first.getNotes());
		cm.addNewContact(second.getName(), second.getNotes());
		cm.addNewContact(third.getName(), third.getNotes());
		cm.addNewContact(fourth.getName(), fourth.getNotes());
		
		Set<Contact> ct = cm.getContacts("Mr");
		
		assertEquals(3,ct.size());
		assertTrue(ct.contains(first));
		assertTrue(ct.contains(second));
		assertTrue(ct.contains(fourth));
	}
	
	@Test 
	public void testGetContactStringMatchMultipleNotIncludingFirst() {
		Contact first = new ContactImpl(1,"Mrs White", "Had a candlestick");
		Contact second = new ContactImpl(2,"Mr Brown", "Was not there");
		Contact third = new ContactImpl(3,"Miss Scarlet", "Was in the drawing room");
		Contact fourth = new ContactImpl(4,"Mr Plum", "Felt aggrevated by not being called by his proper title");
		
		cm.addNewContact(first.getName(), first.getNotes());
		cm.addNewContact(second.getName(), second.getNotes());
		cm.addNewContact(third.getName(), third.getNotes());
		cm.addNewContact(fourth.getName(), fourth.getNotes());
		
		Set<Contact> ct = cm.getContacts("Mr ");
		
		assertEquals(2,ct.size());
		assertTrue(ct.contains(second));
		assertTrue(ct.contains(fourth));
	}

	@Test 
	public void testGetContactNoMatch() {
		Set<Contact> ct = cm3Contacts.getContacts("Elizabeth");
		
		assertTrue(ct.isEmpty());
	}
	
	@Test 
	public void testGetContactListContactsEmpty() {
		Set<Contact> ct = cm.getContacts("Elizabeth");
		
		assertTrue(ct.isEmpty());
	}
	
	@Test 
	public void testGetContactMatchWithinWord() {
		Set<Contact> ct = cm2Contacts.getContacts("o");
		
		assertEquals(1,ct.size());
		assertTrue(ct.contains(c1));
	}
	
	@Test 
	public void testGetContactRequiresMatchedCase() {
		Contact first = new ContactImpl(1,"Mrs White", "Had a candlestick");

		
		cm.addNewContact(first.getName(), first.getNotes());

		
		Set<Contact> ct = cm.getContacts("MRS");
		
		assertEquals(0,ct.size());
	}

	
	
	

}

package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import impls.Clock;
import impls.ContactImpl;
import impls.ContactManagerImpl;
import impls.FutureMeetingImpl;
import impls.MeetingImpl;
import impls.PastMeetingImpl;
import interfaces.Contact;
import interfaces.ContactManager;
import interfaces.Meeting;
import interfaces.PastMeeting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the ContactManagerImpl implementation of interface ContactManager.
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
	final Calendar pastDateMinute= new GregorianCalendar(2010,6,6,12,11,31);
	final Calendar pastDateSecond= new GregorianCalendar(2010,6,6,12,10,29);
	

	
	final File contactFile = new File("Contact.txt");
	
	/* 
	 * testContactsFile.txt contains no meetings and two contacts:
	 * 	1) id=1 name="Bob" notes="Nice guy"
	 * 	2) id=2 name="Fred" notes="Talks too much"
	 */
	final File testContactsFile = new File("tests/testContactsFile.txt");
	
	/*
	 * testMeetingsFile.txt contains two contacts and two meetings:
	 * Contacts:
	 * 	1) id=1 name="Bob" notes="Nice guy"
	 * 	2) id=2 name="Fred" notes="Talks too much"
	 * 	3) id=3 name="Anon" notes=""
	 * 
	 * 4 Meetings:
	 * 	1) 
	 */
	final File testMeetingsFile = new File("tests/testMeetingsFile.txt");
	final File testConAndMeetFile = new File("tests/testConAndMeetFile.txt");
	
	//final Path contactPath = contactFile.toPath();
	//final Path testContactsPath = testContactsFile.toPath();
	//final Path testMeetingsPath = testMeetingsFile.toPath();
	//final Path testConAndMeetPath = testConAndMeetFile.toPath();
	
	Contact c1, c2, c3;
	
	Set<Contact> contacts1, contacts2, contacts3;
	
	Meeting m1, m2, m3, m4;
	
	
	ContactManager cm, cm3Contacts, cm2Contacts, cm1Contacts;
	
	/*
	 * TODO: What if the meeting is set on the same day, do we signify time as well?
	 * TODO: getContactsInt, what if no input?
	 * TODO: should getContacts(String) match be case sensitive?
	 * TODO: Should meetings be autoupdated when they become past?
	 * TODO: how do we do something on program close? Will need to test this in several places, for now, just have a flush
	 * 			after every method that can run?
	 * TODO: Can we assume that multiple cannot be run at the same time?
	 * TODO: Do Meetings automatically convert into a past meeting dependent on date? It looks like it may only be done when notes are added. 
	 * 			- For example, List<PastMeeting> getPastMeeting only to return those with notes added? (And not those added in future but now past)
	 * 
	 * 
	 * TODO: ME: Test things dont get removed when getting
	 * TODO: ME: Add an equals method to contact to make comparing easy 
	 * TODO: Test meeting swiched to past meeting where expected
	 * TODO: Can test things ticking over from future to past by checking system time after, failing the test should it not be able to be completed in time (after trying several times)
	 * TODO: Measure chronologically within seconds
	 * TODO: TEst updated with each method that can be run and change
	 * TODO: Test files after all excpetions
	 * TODO: Times to test, 11 months,27 days - for year, +1 year - 1 second
	 * 				- for month - 
	 * TODO: Create own clock to use, and a test file checking that clock works properly, also testing that, without settings
	 * 			it uses a proper date (put in a past and future meeting and check it sees it as such)
	 * TODO: ME: getFutureMeeting test cases when have pastmeeting between two futures to ensure it checks on
	 * TODO: ME: An equals statement within classes would let the comaprisons be much easier
	 * TODO ME: Make sure null notes are ok from future meeting
	 * 
	 * Things to test:
	 * 
	 * does the set compare properly for contacts to keep unique?
	 * 
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
		
		cm1Contacts.addNewContact("Bob","Nice guy");
		
		cm2Contacts.addNewContact("Bob","Nice guy");
		cm2Contacts.addNewContact("Fred","Talks too much");
		
		cm3Contacts.addNewContact("Bob","Nice guy");
		cm3Contacts.addNewContact("Fred","Talks too much");
		cm3Contacts.addNewContact("Anon","");
		
		
		m1 = new MeetingImpl(1,null,null); //TODO: Add proper meetings in here
		m2 = new PastMeetingImpl(); //TODO: Add proper meetings in here
		m3 = new PastMeetingImpl(); //TODO: Add proper meeting, without notes yet, in here
		m4 = new FutureMeetingImpl(); //TODO: Add proper meetings in here
	}
	
	// Test reading and altering file correctly
	
	@Test 
	public void testFileCreatedIfNotExist() 
	{
		assertTrue(contactFile.exists());
	}
	
	@Test 
	public void testFileContactsReadCorrectly() 
	{
		copyFile(testContactsFile,contactFile);
		
		cm = new ContactManagerImpl();
		
		assertEquals(c1,getOnlyContactFromSet(cm.getContacts(1)));
		assertEquals(c2,getOnlyContactFromSet(cm.getContacts(2)));
		assertEquals(c3,getOnlyContactFromSet(cm.getContacts(3)));
		
	}
	
	@Test 
	public void testFileContactsReadCorrectlyWithMeetings() 
	{
		copyFile(testMeetingsFile,contactFile);
		
		cm = new ContactManagerImpl();
		
		assertEquals(c1,getOnlyContactFromSet(cm.getContacts(1)));
		assertEquals(c2,getOnlyContactFromSet(cm.getContacts(2)));
		assertEquals(c3,getOnlyContactFromSet(cm.getContacts(3)));
		
	}
	
	@Test 
	public void testFileMeetingsReadCorrectly() 
	{
		copyFile(testMeetingsFile,contactFile);
		
		cm = new ContactManagerImpl();
		
		assertEquals(m1,cm.getMeeting(1));
		assertEquals(m2,cm.getMeeting(2));
		assertEquals(m3,cm.getMeeting(3));
		assertEquals(m4,cm.getMeeting(4));
	}
	
	@Test 
	public void testFileReadNextContactIDCorrect() 
	{
		copyFile(testMeetingsFile,contactFile);
		
		cm = new ContactManagerImpl();
		
		cm.addNewContact("Extra Added", "Next contact");
		assertEquals(4,getOnlyContactFromSet(cm.getContacts("Extra Added")).getId());
	}
	
	@Test 
	public void testFileReadNextMeetingIDCorrect() 
	{
		copyFile(testMeetingsFile,contactFile);
		
		cm = new ContactManagerImpl();
		
		Set<Contact> contacts = new HashSet<Contact>();
		contacts.add(c1);
		contacts.add(c2);
		
		cm.addFutureMeeting(contacts,futureDateYear);
		assertEquals(5, cm.getFutureMeetingList(futureDateYear).get(0).getId());
	}
	
	@Test 
	public void testFileReadNextContactIDCorrectFirst() 
	{
		cm.addNewContact("Extra Added", "Next contact");
		assertEquals(1,getOnlyContactFromSet(cm.getContacts("Extra Added")).getId());
	}
	
	@Test 
	public void testFileReadNextMeetingIDCorrectFirst() 
	{
		copyFile(testContactsFile,contactFile);
		
		cm = new ContactManagerImpl();
		
		Set<Contact> contacts = new HashSet<Contact>();
		contacts.add(c1);
		contacts.add(c2);
		
		cm.addFutureMeeting(contacts,futureDateYear);
		assertEquals(1, cm.getFutureMeetingList(futureDateYear).get(0).getId());
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
		ContactManager tempCM = new ContactManagerImpl();
		assertEquals(new MeetingImpl(1,futureDateYear,contacts2),tempCM.getFutureMeeting(1));
	}
	
	@Test 
	public void testAddFutureMeetingStoredWhenCurrentMeetings() {
		cm2Contacts.addFutureMeeting(contacts2,futureDateYear);
		cm2Contacts.addFutureMeeting(contacts2,futureDateDay);
		ContactManager tempCM = new ContactManagerImpl();
		assertEquals(new MeetingImpl(1,futureDateDay,contacts2),tempCM.getFutureMeeting(2));
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
		ContactManager tempCM = new ContactManagerImpl();
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
		
		assertEquals(new PastMeetingImpl(new MeetingImpl(1,futureDateDay,contacts2)),cm2Contacts.getPastMeeting(1));
	}
	
	@Test 
	public void testGetPastMeetingNotExist() {
		cm2Contacts.addFutureMeeting(contacts2,pastDateDay);
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
	
	@Test public void testGetFutureListContactNotExistException() {}
	
	@Test public void testGetFutureListContactPastMeetingsOnlyExist() {}
	
	@Test public void testGetFutureListContactFutureMeetingsOnlyExist() {}
	
	@Test public void testGetFutureListContactNoMeetingsExist() {}
	
	@Test public void testGetFutureListContactFutureAndPastMeetingsExist() {}
	
	@Test public void testGetFutureListContactListSortedChronologically() {}
	
	// Test getFutureMeetingList(Calendar)
	
	@Test public void testGetFutureListDateNoMeetings() {}
	
	@Test public void testGetFutureListDateTodayBothPastAndFuture() {}
	
	@Test public void testGetFutureListDateSortedChronological() {}
	
	// Test getPastMeetingList(Contact)
	
	@Test public void testGetPastListContactNotExistException() {}
	
	@Test public void testGetPastListContactPastMeetingsOnlyExist() {}
	
	@Test public void testGetPastListContactFutureMeetingsOnlyExist() {}
	
	@Test public void testGetPastListContactNoMeetingsExist() {}
	
	@Test public void testGetPastListContactFutureAndPastMeetingsExist() {}
	
	@Test public void testGetPastListContactListSortedChronologically() {}
	
	// Test addNewPastMeeting()
	
	@Test public void testAddPastMeetingContactsNullException() {}
	
	@Test public void testAddPastMeetingDateNullException() {}
	
	@Test public void testAddPastMeetingTestNullException() {}
	
	@Test public void testAddPastMeetingFirstContactNotExist() {}
	
	@Test public void testAddPastMeetingSecondContactNotExist() {}
	
	@Test public void testAddPastMeetingContactListEmpty() {}
	
	@Test public void testAddPastMeetingFirstID() {}
	
	@Test public void testAddPastMeetingSecondID() {}
	
	@Test public void testAddPastMeetingFirst() {}
	
	@Test public void testAddPastMeetingSecond() {}
	
	@Test public void testAddPastMeetingFileUpdated() {}
	
	// Test addMeetingNotes()
	
	@Test public void testAddMeetingNotesMeetingNotExistException() {}
	
	@Test public void testAddMeetingNotesDateFutureException() {}
	
	@Test public void testAddMeetingNotesDateFutureButSameDayException() {}
	
	@Test public void testAddMeetingNotesNotesNullException() {}
	
	@Test public void testAddMeetingNotesMeetingNotesEmpty() {}
	
	@Test public void testAddMeetingNotesMeetingNotesNotEmpty() {}
	
	@Test public void testAddMeetingNotesMeetingNotesEmptyNewNotesEmpty() {}
	
	@Test public void testAddMeetingNotesMeetingNoteNotEmptyNotesEmpty() {}
	
	@Test public void testAddMeetingNotesFileUpdated() {}
	
	// Test addNewContact()
	
	@Test public void testAddNewContactNameNullException() {}
	
	@Test public void testAddNewContactNotesNullException() {}
	
	@Test public void testAddNewContactFirstContactIDNum() {}
	
	@Test public void testAddNewContactSecondContactIDNum() {}
	
	@Test public void testAddNewContactValue() {}
	
	@Test public void testAddNewContactFileUpdatedFirst() {}
	
	@Test public void testAddNewContactFileUpdatedSecond() {}
	
	// Test getContacts(int)
	
	@Test public void testGetContactsIntOneParameter() {}
	
	@Test public void testGetContactsIntMultipleParametersFirstInListIncluded() {}
	
	@Test public void testGetContactsFirstParameterNotExist() {}
	
	@Test public void testGetContactsSecondParameterNotExist() {}
	
	@Test public void testGetContactsContactListEmpty() {}
	
	// Test getContacts(String)

	@Test public void testGetContactsStringNullParamterException() {}

	@Test public void testGetContactsEmptyString() {}
	
	@Test public void testGetContactsStringMatchFirstContact() {}
	
	@Test public void testGetContactsStringMatchSecondContact() {}
	
	@Test public void testGetContactStringMatchMultipleIncludingFirst() {}

	@Test public void testGetContactNoMatch() {}
	
	@Test public void testGetContactListContactsEmpty() {}
	
	@Test public void testGetContactMatchWithinWord() {}
	
	@Test public void testGetContactDoesntMatchCase() {}
	
	// Test flush()
	
	@Test public void testFlushRunsWhenProgramClosed() {}
	
	@Test public void testFlushStoresContactsChanges() {}
	
	@Test public void testFlushStoresMeetingChanges() {}
	
	

}

package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import impls.ContactImpl;
import impls.ContactManagerImpl;
import impls.FutureMeetingImpl;
import impls.MeetingImpl;
import impls.PastMeetingImpl;
import interfaces.Contact;
import interfaces.ContactManager;
import interfaces.Meeting;

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

	Calendar currentDate;
	
	Calendar futureDateMonth;
	Calendar futureDateYear;
	Calendar futureDateMinute;
	Calendar pastDateMonth;
	Calendar pastDateYear;
	Calendar pastDateMinute;

	
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
	
	Meeting m1, m2, m3, m4;
	
	
	ContactManager cm;
	
	/*
	 * TODO: What if the meeting is set on the same day, do we signify time as well?
	 * TODO: getContactsInt, what if no input?
	 * TODO: should getContacts(String) match be case sensitive?
	 * TODO: Should meetings be autoupdated when they become past?
	 * TODO: how do we do something on program close? Will need to test this in several places, for now, just have a flush
	 * 			after every method that can run?
	 * TODO: Can we assume that multiple cannot be run at the same time?
	 * 
	 * TODO: ME: Test things dont get removed when getting
	 * TODO: ME: Add an equals method to contact to make comparing easy 
	 * TODO: Test meeting swiched to past meeting where expected
	 * TODO: Can test things ticking over from future to past by checking system time after, failing the test should it not be able to be completed in time (after trying several times)
	 * TODO: Measure chronologically within seconds
	 * TODO: TEst updated with each method that can be run and change
	 * TODO: Test files after all excpetions
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
	
	public void setUpDates()
	{
		currentDate = new GregorianCalendar();
		(futureDateYear= new GregorianCalendar()).add(Calendar.YEAR,1);
		(futureDateMonth= new GregorianCalendar()).add(Calendar.MONTH,1);
		(futureDateMinute= new GregorianCalendar()).add(Calendar.MINUTE,1);	
		(pastDateYear= new GregorianCalendar()).add(Calendar.YEAR,-1);
		(pastDateMonth= new GregorianCalendar()).add(Calendar.MONTH,-1);
		(pastDateMinute= new GregorianCalendar()).add(Calendar.MINUTE,-1);
	}
	
	
	@Before 
	public void cleanStart()
	{
		setUpDates();
		
		if (contactFile.exists()) contactFile.delete(); 
		cm = new ContactManagerImpl();
		
		c1 = new ContactImpl(1,"Bob","Nice guy");
		c2 = new ContactImpl(2,"Fred","Talks too much");
		c3 = new ContactImpl(3, "Anon", "");
		
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
	
	@Test public void testAddFutureMeetingPastDateException() {}
	
	@Test public void testAddFutureMeetingUnknownContactNoContactsStoredException() {}
	
	@Test public void testAddFutureMeetingUnknownContactContactsStoredException() {}
	
	@Test public void testAddFutureMeetingUnknownContactNotFirstInListException() {}
	
	@Test public void testAddFutureMeetingContactFoundNotFirstInList() {}
	
	@Test public void testAddFutureMeetingStoredWhenNoCurrentMeetings() {}
	
	@Test public void testAddFutureMeetingStoredWhenCurrentMeetings() {}
	
	@Test public void testAddFutureMeetingTodaysDatePast() {}
	
	@Test public void testAddFutureMeetingTodaysDateFuture() {}
	
	@Test public void testAddFutureMeetingFileUpdatedFirstMeeting() {}
	
	@Test public void testAddFutureMeetingFileUpdatedSecondMeeting() {}
	
	
	// Test ID increments for meeting from addFutureMeeting()
	
	@Test public void testAddFutureMeetingIDFirst() {}
	
	@Test public void testAddFutureMeetingIDSecond() {}
	
	// Test getPastMeeting()
	
	@Test public void testGetPastMeetingFutureIdExistsException() {}
	
	@Test public void testGetPastMeetingNotExist() {}
	
	@Test public void testGetPastMeetingListEmpty() {}
	
	@Test public void testGetPastMeetingFirstItemInList() {}
	
	@Test public void testGetPastMeetingSecondItemInList() {}
	
	@Test public void testGetPastMeetingSameDayButPast() {}
	
	@Test public void testGetPastMeetingSameDayButFuture() {}
	
	// Test getFutureMeeting()
	
	@Test public void testGetFutureMeetingFutureIdExistsException() {}
	
	@Test public void testGetFutureMeetingNotExist() {}
	
	@Test public void testGetFutureMeetingListEmpty() {}
	
	@Test public void testGetFutureMeetingFirstItemInList() {}
	
	@Test public void testGetFutureMeetingSecondItemInList() {}
	
	@Test public void testGetFutureMeetingSameDayButPast() {}
	
	@Test public void testGetFutureMeetingSameDayButFuture() {}
	
	// Test getMeeting()
	
	@Test public void testGetMeetingNotExist() {}
	
	@Test public void testGetMeetingListEmpty() {}
	
	@Test public void testGetMeetingFirstItemInList() {}
	
	@Test public void testGetMeetingSecondItemInList() {}
	
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

package tests;

import org.junit.Test;

/**
 * Tests the ContactManagerImpl implementation of interface ContactManager.
 * 
 * @author Paul Day
 *
 */

public class TestContactManagerImpl {

	/*
	 * TODO: What if the meeting is set on the same day, do we signify time as well?
	 * TODO: getContactsInt, what if no input?
	 * TODO: should getContacts(String) match be case sensitive?
	 * 
	 * Things to test:
	 * 
	 * does the set compare properly for contacts to keep unique?
	 * 
	 */
	
	//Test reading and writing file
	
	@Test public void testFileCreatedIfNotExist() {}
	
	@Test public void testFileContactsReadCorrectly() {}
	
	@Test public void testFileContactsReadCorrectlyEmpty() {}
	
	@Test public void testFileMeetingsReadCorrectly() {}
	
	@Test public void testFileMeetingsReadCorrectlyEmpty() {}
	
	@Test public void testFileReadNextContactIDCorrect() {}
	
	@Test public void testFileReadNextMeetingIDCorrect() {}
	
	@Test public void testFileSavedCorrectly() {}
	
	@Test public void testFileSavedCorrectlyFileCurrentlyExists() {}
	
	@Test public void testFileSavedCorrectlyContactsEmpty() {}
	
	@Test public void testFileSavedCorrectlyMeetingsEmpty() {}
	
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
	
	// Test ID increments for meeting from addFutureMeeting()
	
	@Test public void testAddFutureMeetingIDFirst() {}
	
	@Test public void testAddFutureMeetingIDSecond() {}
	
	@Test public void testAddFutureMeetingIDIncrementWhenFirstRunAfterLoad() {} 
	
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
	
	// Test addMeetingNotes()
	
	@Test public void testAddMeetingNotesMeetingNotExistException() {}
	
	@Test public void testAddMeetingNotesDateFutureException() {}
	
	@Test public void testAddMeetingNotesDateFutureButSameDayException() {}
	
	@Test public void testAddMeetingNotesNotesNullException() {}
	
	@Test public void testAddMeetingNotesMeetingNotesEmpty() {}
	
	@Test public void testAddMeetingNotesMeetingNotesNotEmpty() {}
	
	@Test public void testAddMeetingNotesMeetingNotesEmptyNewNotesEmpty() {}
	
	@Test public void testAddMeetingNotesMeetingNoteNotEmptyNotesEmpty() {}
	
	// Test addNewContact()
	
	@Test public void testAddNewContactNameNullException() {}
	
	@Test public void testAddNewContactNotesNullException() {}
	
	@Test public void testAddNewContactFirstContactIDNum() {}
	
	@Test public void testAddNewContactSecondContactIDNum() {}
	
	@Test public void testAddNewContactValue() {}
	
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

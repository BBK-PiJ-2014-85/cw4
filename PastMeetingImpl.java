

import java.util.Calendar;
import java.util.Set;

/**
 * An implementation of PastMeeting by extending MeetingImpl.
 * 
 * Holds and returns the id, date, list of attendees and notes of the meeting. 
 * 
 * This class does not ensure that ID's are unique, and therefore has a dependency on another class to ensure that IDs provided to this are unique.
 * 
 * ContactManagerImpl ensures IDs provided to this are unique.
 * 
 * @author Paul Day
 */

public class PastMeetingImpl extends MeetingImpl implements PastMeeting {

	String notes = "";
	
	/**
	 * A constructor to turn a Meeting into and PastMeeting. Note this does not delete the previous meeting but does duplicate its
	 * ID. The user should therefore ensure deletion of the previous meeting.
	 * 
	 * @param currentMeeting the meeting to be turned into a PastMeeting
	 * @param notes the notes for this meeting
	 */
	
	public PastMeetingImpl(Meeting currentMeeting, String notes) {
		super(currentMeeting.getId(),currentMeeting.getDate(),currentMeeting.getContacts());
		if (notes != null) this.notes = notes;
	}
	
	/**
	 * A constructor to produce a new PastMeeting. ID not guaranteed to be unique.
	 * 
	 * @param id the id of the meeting
	 * @param date the date the meeting took place
	 * @param attendees the attendees of the meeting
	 * @param notes the note of the meeting
	 */
	
	public PastMeetingImpl(int id, Calendar date, Set<Contact> attendees, String notes) {
		super(id, date, attendees);
		if (notes != null) this.notes = notes;
	}

	@Override
	public String getNotes() {
		return notes;
	}
	
	@Override
	public boolean equals(Object other)
	{
		try{
			if (((PastMeeting)other).getDate().equals(date) 
					&& ((PastMeeting)other).getId() == id 
					&& ((PastMeeting)other).getContacts().equals(attendees)
					&& ((PastMeeting)other).getNotes().equals(notes)
					) return true;
		}
		catch (ClassCastException e)
		{
			return false;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return date.hashCode() + attendees.hashCode() + notes.hashCode() + id;
	}

}

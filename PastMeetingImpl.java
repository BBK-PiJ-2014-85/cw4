

import java.util.Calendar;
import java.util.List;
import java.util.Set;


public class PastMeetingImpl extends MeetingImpl implements PastMeeting {

	String notes = "";
	
	public PastMeetingImpl(Meeting currentMeeting, String notes) {
		super(currentMeeting.getId(),currentMeeting.getDate(),currentMeeting.getContacts());
		this.notes = notes;
	}

	public PastMeetingImpl(int id, Calendar date, Set<Contact> attendees) {
		super(id, date, attendees);
	}
	
	public PastMeetingImpl(int id, Calendar date, Set<Contact> attendees, String notes) {
		super(id, date, attendees);
		if (notes != null) this.notes = notes;
	}

	public PastMeetingImpl(PastMeeting meeting, String notes) {
		super(meeting.getId(), meeting.getDate(), meeting.getContacts());
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

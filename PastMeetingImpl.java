

import java.util.Calendar;
import java.util.List;
import java.util.Set;


public class PastMeetingImpl extends MeetingImpl implements PastMeeting {

	String notes = "";
	
	public PastMeetingImpl(Meeting currentMeeting) {
		super(currentMeeting.getId(),currentMeeting.getDate(),currentMeeting.getContacts());
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

}

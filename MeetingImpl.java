
import java.util.Calendar;
import java.util.Set;


/**
 * Implementation of the class Meeting.
 * 
 * Holds and returns the id, date and list of attendees of the meeting. 
 * 
 * Assumes id, date and attendees are not empty, as implementations of ContactManager ensure this.
 * 
 * @author Paul Day
 */

public class MeetingImpl implements Meeting {

	int id;
	Calendar date;
	Set<Contact> attendees;
	
	public MeetingImpl(int id, Calendar date, Set<Contact> attendees)
	{
		this.id = id;
		this.date = date;
		this.attendees = attendees;
	}
	
	@Override
	public int getId() {return id;}

	@Override
	public Calendar getDate() {return date;}

	@Override
	public Set<Contact> getContacts() {return attendees;}

}

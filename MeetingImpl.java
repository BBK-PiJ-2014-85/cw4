
import java.util.Calendar;
import java.util.Set;


/**
 * An Implementation of the class Meeting.
 * 
 * Holds and returns the id, date and list of attendees of the meeting. 
 * 
 * This class does not ensure that ID's are unique, and therefore has a dependency on another class to ensure that IDs provided to this are unique.
 * 
 * ContactManagerImpl ensures IDs provided to this are unique.
 * 
 * @author Paul Day
 */

/* Reason for ID being managed within ContactManagerImpl (copied from code of ContactManagerImpl):
 *  It was assumed that ContactManager should ensure the IDs are unique. The main reason for this was that Contact did not have methods to update and manage the file (for example, via a flush()), 
 *  while to ensure that IDs remain unique it would need access to this file and write every contact it creates to this file. Otherwise, when reading in a contact manager's file, should
 *  Contact have already created a contact it would be likely to contain duplicate IDs would be unique. It is also impossible to add a Contact to the ContactManager with a specific ID,
 *  which means that any Contact/Meeting created outside the ContactManager is unable to be imported. A similar thought argument applies to Meeting's as well. This, combined with the
 *  spec not making it clear that Contact/Meeting was meant to manage the IDs it was therefore assumed that the ContactManager was intended to manage the IDs, as to use a Contact/Meeting 
 *  elsewhere and within ContactManager it would need to be added via ContactManager first. 
 */

public class MeetingImpl implements Meeting {

	int id;
	Calendar date;
	Set<Contact> attendees;
	
	/**
	 * Creates a new MeetingImpl. It should be ensured that ID passed into this is unique elsewhere as this class does not ensure uniqueness.
	 * 
	 * @param id the ID of the meeting
	 * @param date the date of the meeting
	 * @param attendees the attendees of the meeting
	 */
	
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
	
	@Override
	public boolean equals(Object other)
	{
		try{
			if (((Meeting)other).getDate().equals(date) 
					&& ((Meeting)other).getId() == id 
					&& ((Meeting)other).getContacts().equals(attendees)) return true;
		}
		catch (ClassCastException e)
		{
			return false;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return date.hashCode() + attendees.hashCode() + id;
	}

}

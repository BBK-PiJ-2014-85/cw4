
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



public class ContactManagerImpl implements ContactManager {
	
    Comparator<Meeting> chronological = (m1, m2) -> m1.getDate().compareTo(m2.getDate());
	
	int countContact = 1, meetingCount = 1;
	Clock clock = new Clock();
	List<Contact> contacts = new ArrayList<Contact>();
	List<Meeting> meetings = new ArrayList<Meeting>();
	

	@Override
	public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
		
		if (Clock.getCurrent().compareTo(date) >=0 ) throw new IllegalArgumentException("Date is in the past");
		if (!this.contacts.containsAll(contacts)) throw new IllegalArgumentException("Contact not found");
		
		meetings.add(new FutureMeetingImpl(meetingCount,date,contacts));
		meetingCount++;
		return meetingCount - 1;
	}

	@Override
	public PastMeeting getPastMeeting(int id) {
		if (id < 0 || id > meetings.size()) return null;
		if (Clock.getCurrent().compareTo(meetings.get(id - 1).getDate()) <= 0) throw new IllegalArgumentException("Meeting is in the past");

		PastMeeting rtn;
		
		try {
			rtn = (PastMeeting)meetings.get(id - 1);
		}
		catch (ClassCastException e)
		{
			meetings.set(id - 1, new PastMeetingImpl(meetings.get(id - 1),""));
			rtn = (PastMeeting)meetings.get(id - 1);
		}
		
		return rtn;
	}


	@Override
	public FutureMeeting getFutureMeeting(int id) {
		if (id < 0 || id > meetings.size()) return null;
		
		if (Clock.getCurrent().compareTo(meetings.get(id - 1).getDate()) >= 0) throw new IllegalArgumentException("Meeting is in the past");
		
		return (FutureMeeting) meetings.get(id - 1);
	}

	@Override
	public Meeting getMeeting(int id) {
		if (id <=0 || id> meetings.size()) return null;
		return meetings.get(id - 1);
	}

	@Override
	public List<Meeting> getFutureMeetingList(Contact contact) {
		if (!contacts.contains(contact)) throw new IllegalArgumentException("Contact not known");
		
		List<Meeting> rtn = meetings.stream()
					.filter(x -> Clock.getCurrent().compareTo(x.getDate()) <= 0)
					.filter(x -> x.getContacts().contains(contact))
					.sorted(chronological)
					.collect(Collectors.toList());
		return rtn;
	}

	@Override
	public List<Meeting> getFutureMeetingList(Calendar date) {
		
		List<Meeting> rtn = meetings.stream()
				.filter(x -> x.getDate().get(Calendar.YEAR) == date.get(Calendar.YEAR))
				.filter(x -> x.getDate().get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR))
				.sorted(chronological)
				.collect(Collectors.toList());
		
		return rtn;
	}

	@Override
	public List<PastMeeting> getPastMeetingList(Contact contact) {
		if (!contacts.contains(contact)) throw new IllegalArgumentException("Contact not known");
		
		List<Meeting> pm = meetings.stream()
				.filter(x -> Clock.getCurrent().compareTo(x.getDate()) >= 0)
				.filter(x -> x.getContacts().contains(contact))
				.sorted(chronological)
				.collect(Collectors.toList());
	
		List<PastMeeting> rtn = new ArrayList<PastMeeting>();
		
		for (Meeting m : pm)
		{
			try{
				rtn.add((PastMeeting)m);
			} catch (ClassCastException e)
			{
				int location = m.getId() - 1;
				meetings.set(location, new PastMeetingImpl(m,""));
				rtn.add((PastMeeting)meetings.get(location));
			}	
		}
		
		return rtn;
	}

	@Override
	public void addNewPastMeeting(Set<Contact> contacts, Calendar date,String text) {
		
		//Assumed that past meetings can be added in the future, as in the spec this does not throw exceptions for date being in future
		
		if (contacts == null || date == null || text == null) throw new NullPointerException("A parameter is null");
		if (contacts.isEmpty() || !this.contacts.containsAll(contacts)) throw new IllegalArgumentException("Contacts either empty or at least one doesn't exist");
		meetings.add(new PastMeetingImpl(meetingCount, date, contacts, text));
		meetingCount++;
	}

	@Override
	public void addMeetingNotes(int id, String text) {
		if (id <= 0 || id > meetings.size()) throw new IllegalArgumentException("Meeting doesn't exist.");
		if (Clock.getCurrent().compareTo(meetings.get(id - 1).getDate()) >=0) throw new IllegalStateException("Meeting set for time in future.");
		if (text == null) throw new NullPointerException("Text is null.");
		
		//Assumed as not specified that setting notes for a meeting with already notes will overwrite these
		meetings.set(id - 1, new PastMeetingImpl(meetings.get(id - 1), text));
	}

	@Override
	public void addNewContact(String name, String notes) {
		
		if (name==null || notes==null) throw new NullPointerException("Name or notes are null");
		else {
			contacts.add(new ContactImpl(countContact,name,notes));
			countContact++;
			}
	}

	@Override
	public Set<Contact> getContacts(int... ids) {
		
		Set<Contact> rtn = new HashSet<Contact>();
		
		//Assumed that entering no integers should return empty list as there is no id not to match
		if (ids.length == 0) return rtn;
		
		for (int i : ids) 
		{
			if (i <= 0 || i > contacts.size()) throw new IllegalArgumentException("Contact ID does not exist");
			else rtn.add(contacts.get(i - 1));
		}
		
		return rtn;
	}

	@Override
	public Set<Contact> getContacts(String name) {
		
		if (name == null) throw new NullPointerException("Name string is null.");
		
		Set<Contact> rtn = new HashSet<Contact>();
		
		if (name != "") //Assumed that searching for "" should match nothing not everything, as not mentioned in spec.
		{
			for (Contact c : contacts)
			{
				// Assumed that name search is case sensitive as not specified
				if (c.getName().contains(name)) rtn.add(c);
			}
		}
		
		return rtn;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

}

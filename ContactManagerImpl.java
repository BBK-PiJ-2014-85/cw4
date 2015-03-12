
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



public class ContactManagerImpl implements ContactManager {
	
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FutureMeeting getFutureMeeting(int id) {
		if (id < 0 || id > meetings.size()) return null;
		
		if (Clock.getCurrent().compareTo(meetings.get(id - 1).getDate()) >= 0) throw new IllegalArgumentException("Meeting is in the past");
		
		return (FutureMeeting) meetings.get(id - 1);
	}

	@Override
	public Meeting getMeeting(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Meeting> getFutureMeetingList(Contact contact) {
		if (!contacts.contains(contact)) throw new IllegalArgumentException("Contact not known");
		
	    Comparator<Meeting> chronological = (m1, m2) -> m1.getDate().compareTo(m2.getDate());

		List<Meeting> rtn = meetings.stream()
					.filter(x -> Clock.getCurrent().compareTo(x.getDate()) >= 0)
					.filter(x -> x.getContacts().contains(contact))
					.sorted(chronological)
					.collect(Collectors.toList());
		
		return rtn;
	}

	@Override
	public List<Meeting> getFutureMeetingList(Calendar date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PastMeeting> getPastMeetingList(Contact contact) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addNewPastMeeting(Set<Contact> contacts, Calendar date,
			String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addMeetingNotes(int id, String text) {
		// TODO Auto-generated method stub

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
		
		if (name != "")
		{
			for (Contact c : contacts)
			{
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

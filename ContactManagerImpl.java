
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class ContactManagerImpl implements ContactManager {
	
	int countContact = 1;
	List<Contact> contacts = new ArrayList<Contact>();
	

	@Override
	public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PastMeeting getPastMeeting(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FutureMeeting getFutureMeeting(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Meeting getMeeting(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Meeting> getFutureMeetingList(Contact contact) {
		// TODO Auto-generated method stub
		return null;
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
		
		for (Contact c : contacts)
		{
			if (c.getName().contains(name)) rtn.add(c);
		}
		
		return rtn;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

}

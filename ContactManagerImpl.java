
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
	
	int countContact = 1, countMeeting = 1;
	Clock clock = new Clock();
	List<Contact> contacts = new ArrayList<Contact>();
	List<Meeting> meetings = new ArrayList<Meeting>();
	

	@Override
	public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
		
		if (Clock.getCurrent().compareTo(date) >=0 ) throw new IllegalArgumentException("Date is in the past");
		if (!this.contacts.containsAll(contacts)) throw new IllegalArgumentException("Contact not found");
		
		meetings.add(new FutureMeetingImpl(countMeeting,date,contacts));
		countMeeting++;
		return countMeeting - 1;
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
		meetings.add(new PastMeetingImpl(countMeeting, date, contacts, text));
		countMeeting++;
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

	public String createFile()
	{
		String output = "";
		
		for (Contact c : contacts) output += contactToFileString(c) + "\n";
		
		for (Meeting m : meetings) output += meetingToFileString(m) + "\n";

		output += "<end>";
		
		return output;
	}
	
	private String contactToFileString(Contact c)
	{		
		String notInName = "Name";
		
		//these could be a lot prettier with lambdas
		int nameIterator = 1;
		while (c.getName().contains(notInName) || c.getNotes().contains(notInName))
		{
			notInName = "Name" + nameIterator;
			nameIterator++;
		}
		
		String notInNotes = "Notes";
		
		int notesIterator = 1;
		while (c.getName().contains(notInNotes) || c.getNotes().contains(notInNotes))
		{
			notInNotes = "Notes" + notesIterator;
			notesIterator++;
		}
		
		String output = "<Contact>" 
						+ "<NameTag>" + notInName + "<\\NameTag>" 
						+ "<NotesTag>" + notInNotes + "<\\NotesTag>" 
						+ "<ID>" + c.getId() + "<\\ID>"
						+ "<" + notInName + ">" + c.getName() + "<\\" + notInName + ">"
						+ "<" + notInNotes + ">" + c.getNotes() + "<\\" + notInNotes + ">"
						+ "<\\Contact>";
		
		return output;
	}
	
	private String meetingToFileString(Meeting m)
	{
		String stringId = "<ID>" + m.getId() + "<\\ID>";

		String stringCtId ="<Contacts>";
		for (Contact c : m.getContacts()) stringCtId += c.getId() + ",";
		stringCtId = stringCtId.substring(0, stringCtId.length() - 1) + "<\\Contacts>";
		
		String stringDate = "<Date>" + m.getDate().get(Calendar.YEAR) + "," 
										+ m.getDate().get(Calendar.MONTH) + ","
										+ m.getDate().get(Calendar.DAY_OF_MONTH) + "," 
										+ m.getDate().get(Calendar.HOUR) + ","
										+ m.getDate().get(Calendar.MINUTE) + ","
										+ m.getDate().get(Calendar.SECOND) + "<\\Date>";
		
		String stringNotes = "", stringClassBegin, stringClassEnd;
		
		try
		{
			String notInNotes = "Notes";
			int notesIterator = 1;
			while (((PastMeeting)m).getNotes().contains(notInNotes))
			{
				notInNotes = "Notes" + notesIterator;
				notesIterator++;
			}
			
			stringNotes = "<" + notInNotes + ">" + ((PastMeeting)m).getNotes() + "<\\" + notInNotes + ">";
			stringClassBegin = "<PastMeeting><NoteTag>" + notInNotes + "<\\NoteTag>";
			stringClassEnd = "<\\PastMeeting>";
		} catch (ClassCastException e)
		{
			stringNotes = "";
			stringClassBegin = "<FutureMeeting>";
			stringClassEnd = "<\\FutureMeeting>";
		}
			
		return stringClassBegin + stringId + stringCtId + stringDate + stringNotes + stringClassEnd;
	}

	private void launch()
	{
		//Check if file exists() - if not, create it, otherwise, read from it.
		
		
	}
	
	private void readFile()
	{
	
	}
	
	public boolean readLine(String line)
	{
		if (line.equals("<end>")) return true;
		else if (getTagWithinArrows(line,0).equals("Contact")) 
		{
			String nameTag = getStringByTag(line,"NameTag");
			String notesTag = getStringByTag(line,"NotesTag");
			
			
		}
		else if (getTagWithinArrows(line,0).equals("PastMeeting")) {/*read in pastMeeting*/}
		else if (getTagWithinArrows(line,0).equals("FutureMeeting")) {/*read in pastMeeting*/}		
		
		return false;
	}
	
	public String getStringByTag(String line, String tag)
	{
		int firstLoc = line.indexOf("<" + tag + ">") + 2 + tag.length();
		int lastLoc = line.indexOf("<\\" + tag + ">");
		
		return line.substring(firstLoc, lastLoc);
	}
	
	public int[] getIntsByTag(String line, String tag)
	{
		String numList = getStringByTag(line, tag);

		int numCount = 1;
		for (int i=0; i< numList.length(); i++) if (numList.charAt(i) == ',') numCount++; 
		int[] rtn = new int[numCount];
		
		String numString = "";
		int added=0;
		for (int i = 0 ; i< numList.length() ; i++)
		{
			if ( Character.isDigit(numList.charAt(i))) 
				{
				numString += numList.charAt(i);
				if (i == numList.length() - 1) rtn[added] = Integer.parseInt(numString);
				}
			else 
			{
				rtn[added] = Integer.parseInt(numString);
				added++;
				numString = "";
			}
		}
		
		return rtn;
	}
	
	private String getTagWithinArrows(String line, int startLocation)
	{
		int wordLength = 0;
		while (line.charAt(startLocation + 1 + wordLength) != '>') wordLength++;
		return line.substring(startLocation+1, startLocation+1+wordLength);
	}
}

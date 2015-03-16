
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

//Spec make no mention of being used alongside similar implementations. There is no way of
// adding a contact as a contact, or specufying the id via the contact manager, which supports that
// a contact can only exist on one contact manager


/* It was suggested on the forums that Contact and Meeting should contian the IDs as we ahouldnt assume
 * relationship between our classes. However, there is no way of managing ID within contact using the given
 * ,methods. For example, no exceptions are thrown from Contact, yet we need to define IDs in a constructor
 * which must be public, to be able to reuse them. Given this notice was also released very late on,
 * I've continued with the assumption that it is the contactManager which manages IDs.
 * 
 * Example, you cannot have a method to create iD's, and have this loaded in via a file for repeatblity,
 * without having a method to define a specific ID. However, with no exceptions being thrown in the interface,
 * it would be impossible to manage.
 * 
 * Without exceptions, and having ID directly definable via a contructor, it is impoosible to maintin
 * to the spec uniqueness. I have therefore continued to use contact manager to manage the uniqueness.
 * 
 * The ID method chosen has been designed therefore to allow direct access is using an array. As contacts
 * cannot be deleted from a contact manager, for quick reference the location in the list is used as the ID.
 * 
 * 

Asumtion: Not need to cater to different timezones


*/

public class ContactManagerImpl implements ContactManager {
	
	private final File CONTACTS_FILE;
	
    Comparator<Meeting> chronological = (m1, m2) -> m1.getDate().compareTo(m2.getDate());
        
	int countContact = 1, countMeeting = 1;
	Clock clock = new Clock();
	List<Contact> contacts = new ArrayList<Contact>();
	List<Meeting> meetings = new ArrayList<Meeting>();
	
	Comparator<Meeting> duplicateMeeting = (m,p) -> {if (m.getDate() == p.getDate() && m.getContacts() == p.getContacts()) return 1; else return 0;};
	
	public ContactManagerImpl()
	{
		CONTACTS_FILE = new File("Contact.txt");
		launch();
	}
	
	public ContactManagerImpl(String fileLocation)
	{
		CONTACTS_FILE= new File (fileLocation); 
		launch();
	}
	
	@Override
	public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
		
		if (Clock.getCurrent().compareTo(date) >=0 ) throw new IllegalArgumentException("Date is in the past");
		if (!this.contacts.containsAll(contacts)) throw new IllegalArgumentException("Contact not found");
		
		meetings.add(new FutureMeetingImpl(countMeeting,date,contacts));
		countMeeting++;
		flush();
		return countMeeting - 1;
	}

	@Override
	public PastMeeting getPastMeeting(int id) {
		
		/*I assume here that getting a PAST MEETING is referenced by time rather than class, as
			it specifies the exception if the meeting exists but is happening in the future, not 
			a FutureMeeting.
			
			It is also clear in the spec that PastMeetings may not have notes, which is only possible
			if they turn from a future to a PastMeeting via something other than the addNotes.
			
			Should a meeting therefore exist in the past and have been requested, it will be converted
			to a past meeting with no notes, and then returned.
		*/

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
		
		List<Meeting> withDups = meetings.stream()
				.filter(x -> x.getDate().get(Calendar.YEAR) == date.get(Calendar.YEAR))
				.filter(x -> x.getDate().get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR))
				.sorted(chronological)
				.collect(Collectors.toList());
		
		List<Meeting> rtn = new ArrayList<Meeting>();
		
		//Assumed duplicates are where time and contacts match for two meetings (it's impossible for id's to match)
		//can't use .distinct() as this uses equals(), which compares id's.
		//Have therefore kept the last meeting in the list of every contact and time match.

		for (int i = 0; i < withDups.size(); i++)
		{
			boolean duplicate = false;
			for (int j=i+1; j<withDups.size(); j++) if(duplicateMeeting.compare(withDups.get(i), withDups.get(j))==1) duplicate=true;
			if (!duplicate) rtn.add(withDups.get(i));
		}
			
		return rtn;
	}

	@Override
	public List<PastMeeting> getPastMeetingList(Contact contact) {
		if (!contacts.contains(contact)) throw new IllegalArgumentException("Contact not known");
		
		List<Meeting> pmWithDups = meetings.stream()
				.filter(x -> Clock.getCurrent().compareTo(x.getDate()) >= 0)
				.filter(x -> x.getContacts().contains(contact))
				.sorted(chronological)
				.collect(Collectors.toList());
	
		List<PastMeeting> rtn = new ArrayList<PastMeeting>();
		List<Meeting> pmNoDups = new ArrayList<Meeting>();
		
		//Assumed only contacts and time need to be matched (and not notes) as people cant be in two places at once
		for (int i = 0; i < pmWithDups.size(); i++)
		{
			boolean duplicate = false;
			for (int j=i+1; j<pmWithDups.size(); j++) if(duplicateMeeting.compare(pmWithDups.get(i), pmWithDups.get(j))==1) duplicate=true;
			if (!duplicate) pmNoDups.add(pmWithDups.get(i));
		}
		
		// I have assumed that, if a meeting took place in the past but is not yet a pastMeeting, 
		//that it should be converted to one
		
		for (Meeting m : pmNoDups)
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
		flush();
		countMeeting++;
	}

	@Override
	public void addMeetingNotes(int id, String text) {
		if (text == null) throw new NullPointerException("Text is null.");
		if (id <= 0 || id > meetings.size()) throw new IllegalArgumentException("Meeting doesn't exist.");
		if (Clock.getCurrent().compareTo(meetings.get(id - 1).getDate()) <=0) throw new IllegalStateException("Meeting set for time in future.");

		
		//Assumed as not specified that setting notes for a meeting with already notes will overwrite these
		meetings.set(id - 1, new PastMeetingImpl(meetings.get(id - 1), text));
		flush();
	}

	@Override
	public void addNewContact(String name, String notes) {
		
		if (name==null || notes==null) throw new NullPointerException("Name or notes are null");
		else {
			contacts.add(new ContactImpl(countContact,name,notes));
			countContact++;
			flush();
			}
	}

	@Override
	public Set<Contact> getContacts(int... ids) {
		
		Set<Contact> rtn = new HashSet<Contact>();
		
		//Assumed that entering no integers should throw illegal argument exception as mentioned in forum.
		if (ids.length == 0) throw new IllegalArgumentException("Empty input");
		
		for (int i : ids) 
		{
			if (i <= 0 || i > contacts.size()) throw new IllegalArgumentException("Contact ID does not exist");
			else rtn.add(contacts.get(i - 1));
		}
		
		return rtn;
	}

	@Override
	public Set<Contact> getContacts(String name) {
		
		if (name == null || name=="") throw new NullPointerException("Name string is null.");
		
		Set<Contact> rtn = new HashSet<Contact>();
		
			for (Contact c : contacts)
			{
				// Assumed that name search is case sensitive as not specified
				if (c.getName().contains(name)) rtn.add(c);
			}
		
		return rtn;
	}

	@Override
	public void flush() {

		CONTACTS_FILE.delete();
		
		try (PrintWriter out = new PrintWriter(CONTACTS_FILE)){
			CONTACTS_FILE.createNewFile();
			out.write(createFile());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String createFile()
	{
		String output = "";
		//will need to neaten up \n bits as will have gaps in text
		for (Contact c : contacts) output += contactToFileString(c) + "\n";
		
		for (Meeting m : meetings) output += meetingToFileString(m) + "\n";

	//	output += "<end>";
		
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
		
		String stringDate = "<Date>" + m.getDate().getTimeInMillis() + "<\\Date>";
		
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

//can probably remove the first part of the if statement as exception will catch it		
		if (!CONTACTS_FILE.exists())
			try {
				CONTACTS_FILE.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
		{
			BufferedReader in;
			try {
				in = new BufferedReader(new FileReader(CONTACTS_FILE));
				String line;
				while ((line = in.readLine()) != null) 
					{
					readLine(line);
					}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
		
	}
	
	public boolean readLine(String line)
	{
		if (getTagWithinArrows(line,0).equals("Contact")) 
		{
			String nameTag = getStringByTag(line,"NameTag");
			String notesTag = getStringByTag(line,"NotesTag");

			contacts.add(new ContactImpl(getIntsByTag(line,"ID")[0],getStringByTag(line,nameTag),getStringByTag(line,notesTag)));
			countContact++;
		}
		else if (getTagWithinArrows(line,0).equals("FutureMeeting") || getTagWithinArrows(line,0).equals("PastMeeting"))
		{
			int id = getIntsByTag(line,"ID")[0];
			
			Set<Contact> cts = new HashSet<Contact>();
			int[] ctIds = getIntsByTag(line,"Contacts");
			for (int i = 0; i < ctIds.length; i++) cts.add(contacts.get(ctIds[i]-1));
			
			Calendar date = new GregorianCalendar();
			Long time = Long.parseLong(getStringByTag(line,"Date"));
			date.setTimeInMillis(time);
			
			if (getTagWithinArrows(line,0).equals("PastMeeting"))
			{
				String notes = getStringByTag(line,getStringByTag(line,"NoteTag"));
				meetings.add(new PastMeetingImpl(id,date,cts,notes));
			} else{
				meetings.add(new FutureMeetingImpl(id,date,cts));
			}
			
			countMeeting++;
		}
		
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

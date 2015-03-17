
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This is an implementation of the ContactManager interface. Several assumptions have been made for this implementation of ContactManager:
 * <ul>
 * 	<li>Duplicates: Where methods specify removing duplicates from a list, meetings are determined as duplicate if their time and attendees match (i.e. IDs and notes can  be different).</li>
 * 	<li>Unique IDs are managed by ContactManager: It was assumed that ContactManager should determine the Meeting and Contact ID's (reasons provided within code).</li>
 * 	<li>PastMeeting's can be in future: addPastMeeting() does not return an exception when adding a meeting with a future date. PastMeeting's with future dates  
 * 		will be returned in the getFutureMeeting lists but will not be provided by getFutureMeeting(id) as it would need to be cast as a FutureMeeting </li>
 * 	<li>FutureMeeting's changed to PastMeeting: within the getPastMeeting type methods where it is seen that a FutureMeeting is now in the Past due to time passing, 
 * 		it will be converted to a PastMeeting within the ContactManager.</li>
 * 	<li>Interpretation of past and future is by date not type: Generally speaking, where a search for a past of future meeting is made it is searched by date, not by class type. 
 * 	<li>Assigning to notes to something already with notes will overwrite the previous notes.</li>
 * 	<li>Any changes (i.e. meetings and contacts being added) are saved to the file immediately when they are made</li>
 * 	<li>Bespoke Calendar implementations (not Gregorian Calendar) such as those created by the user will lose any functionality not provided by Calendar when reloaded by ContactManager.</li>
 * 	<li>Null inputs: addFutureMeeting(Contacts,Date) does not specify a NullPointerException if any of the input parameters are null. Null parameters therefore return an IllegalArgumentException as this is within the specification, although a NullPointerException would be more appropriate.</li>
 * </ul>
 * 
 * @author Paul Day
 */

/*
 * Notes above that provided above to help understand and/or maintain the code:
 *  	-	It was assumed that ContactManager should ensure the IDs are unique. The main reason for this was that Contact did not have methods to update and manage the file (for example, via a flush()), 
 *  		while to ensure that IDs remain unique it would need access to this file and write every contact it creates to this file. Otherwise, when reading in a contact manager's file, should
 *  		Contact have already created a contact it would be likely to contain duplicate IDs would be unique. It is also impossible to add a Contact to the ContactManager with a specific ID,
 *  		which means that any Contact/Meeting created outside the ContactManager is unable to be imported. A similar thought argument applies to Meeting's as well. This, combined with the
 *  		spec not making it clear that Contact/Meeting was meant to manage the IDs it was therefore assumed that the ContactManager was intended to manage the IDs, as to use a Contact/Meeting 
 *  		elsewhere and within ContactManager it would need to be added via ContactManager first. 
 *		
 *		-	Given the assumption above, IDs are chosen to start at 1 and increment by 1 for each new Meeting/Contact (separate counts for each). This means that, given a known ID, direct access is
 *			possible as each entry is stored in the list at position ID - 1. This is possible as there is no way to delete contacts/meetings. This approach was chosen as it is faster, however this
 *			does have the downside that a lot of the code would need to change should extra features such as removing Contacts/Meetings want to be implmented at a alter date.
 */
 

public class ContactManagerImpl implements ContactManager {
	
	private final File CONTACTS_FILE;
	
	// Used for determining which of the two meetings has an earlier date
    Comparator<Meeting> chronological = (m1, m2) -> m1.getDate().compareTo(m2.getDate());

    // Used for determining if two meetings are duplicate (same contacts and time only)
	Comparator<Meeting> duplicateMeeting = (m,p) -> {if (m.getDate() == p.getDate() && m.getContacts() == p.getContacts()) return 1; else return 0;};
    
    // Used to provide the current time 
    Clock clock = new Clock();
    
    // Holds the nextID number for a contacts and meetings respectively
	int countContact = 1, countMeeting = 1;

	List<Contact> contacts = new ArrayList<Contact>();
	List<Meeting> meetings = new ArrayList<Meeting>();
	
	/**
	 * Default constructor using the default "Contact.txt" file.
	 * 
	 * @throws IllegalStateException if the data file is not in the correct format
	 */
	public ContactManagerImpl()
	{
		CONTACTS_FILE = new File("Contact.txt");
		try{	
			launch();
		} catch (RuntimeException e)
		{
			throw new IllegalStateException("Input file not in correct format for this ContactManager.");
		}

	}
	
	/**
	 * Constructor where the user sets the location of the save file.
	 * 
	 * @param fileLocation the location of the file to be used.
	 * @throws IllegalStateException if the data file is not in the correct format
	 */
	public ContactManagerImpl(String fileLocation) 
	{
		CONTACTS_FILE= new File (fileLocation); 
		try{	
			launch();
		} catch (RuntimeException e)
		{
			throw new IllegalStateException("Input file not in correct format for this ContactManager.");
		}
		
	}
	
	/**{@inheritDoc}
	 * 
	 * Returns an IllegalArgumentException if any of the inputs are null. 
	 * 
	 */
	
	@Override
	public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
		
		if (contacts == null || date == null) throw new IllegalArgumentException("Input is null");
		if (Clock.getCurrent().compareTo(date) >=0 ) throw new IllegalArgumentException("Date is in the past");
		if (!this.contacts.containsAll(contacts) || contacts.isEmpty()) throw new IllegalArgumentException("Contact not found");
		
		meetings.add(new FutureMeetingImpl(countMeeting,date,contacts));
		countMeeting++;
		flush();
		return countMeeting - 1;
	}

	/**{@inheritDoc}
	 * 
	 * Should a FutureMeeting exist which is now in the past, the meeting will be converted to as PastMeeting with no notes
	 * when this method is run.
	 * 
	 */
	@Override
	public PastMeeting getPastMeeting(int id) {
		
		if (id < 0 || id > meetings.size()) return null;
		if (Clock.getCurrent().compareTo(meetings.get(id - 1).getDate()) <= 0) throw new IllegalArgumentException("Meeting is in the past");

		PastMeeting rtn;
		
		try {
			rtn = (PastMeeting)meetings.get(id - 1);
		}
		catch (ClassCastException e) //meeting is a FutureMeeting with a date in the past will get converted.
		{
			meetings.set(id - 1, new PastMeetingImpl(meetings.get(id - 1),""));
			rtn = (PastMeeting)meetings.get(id - 1);
		}
		
		return rtn;
	}

	/**{@inheritDoc}
	 * 
	 * Should a meeting be in the future but be of the class PastMeeting, an IllegalArugmentException will be thrown.
	 * 
	 */
	@Override
	public FutureMeeting getFutureMeeting(int id) {
		if (id < 0 || id > meetings.size()) return null;
		
		if (Clock.getCurrent().compareTo(meetings.get(id - 1).getDate()) >= 0) throw new IllegalArgumentException("Meeting is in the past");
		
		try
		{
		return (FutureMeeting) meetings.get(id - 1);
		} catch (ClassCastException e)
		{
			throw new IllegalArgumentException("Meeting is a PastMeeting");
		}
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

	/**{@inheritDoc}
	 * 
	 * Duplicate meetings, determined by have the same contacts and time, are removed.
	 * 
	 */
	@Override
	public List<Meeting> getFutureMeetingList(Calendar date) {
		
		List<Meeting> withDups = meetings.stream()
				.filter(x -> x.getDate().get(Calendar.YEAR) == date.get(Calendar.YEAR))
				.filter(x -> x.getDate().get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR))
				.sorted(chronological)
				.collect(Collectors.toList());
		
		List<Meeting> rtn = new ArrayList<Meeting>();
		
		for (int i = 0; i < withDups.size(); i++)
		{
			boolean duplicate = false;
			for (int j=i+1; j<withDups.size(); j++) if(duplicateMeeting.compare(withDups.get(i), withDups.get(j))==1) duplicate=true;
			if (!duplicate) rtn.add(withDups.get(i));
		}
			
		return rtn;
	}
	
	/**{@inheritDoc}
	 * 
	 * Duplicate meetings, determined by have the same contacts and time, are removed.
	 * 
	 * Should a meeting be in the past but be of class FutureMeeting, it will be changed within ContactManager to a PastMeeting.
	 * 
	 */
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
		
		for (int i = 0; i < pmWithDups.size(); i++) //Remove duplicates
		{
			boolean duplicate = false;
			for (int j=i+1; j<pmWithDups.size(); j++) if(duplicateMeeting.compare(pmWithDups.get(i), pmWithDups.get(j))==1) duplicate=true;
			if (!duplicate) pmNoDups.add(pmWithDups.get(i));
		}
		
		for (Meeting m : pmNoDups) //Change to PastMeeting if in the future
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

	/**{@inheritDoc}
	 * 
	 * PastMeeting's can be added in the future.
	 * 
	 */
	
	@Override
	public void addNewPastMeeting(Set<Contact> contacts, Calendar date,String text) {
		if (contacts == null || date == null || text == null) throw new NullPointerException("An argument was null");
		if (contacts.isEmpty() || !this.contacts.containsAll(contacts)) throw new IllegalArgumentException("Contacts either empty or at least one doesn't exist");
		meetings.add(new PastMeetingImpl(countMeeting, date, contacts, text));
		flush();
		countMeeting++;
	}

	/**{@inheritDoc}
	 * 
	 * Running this method on a meeting with notes already will overwrite the current notes of the PastMeeting.
	 * 
	 */
	
	@Override
	public void addMeetingNotes(int id, String text) {
		if (text == null) throw new NullPointerException("Text is null.");
		if (id <= 0 || id > meetings.size()) throw new IllegalArgumentException("Meeting doesn't exist.");
		if (Clock.getCurrent().compareTo(meetings.get(id - 1).getDate()) <=0) throw new IllegalStateException("Meeting set for time in future.");
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
		
		if (ids.length == 0) throw new IllegalArgumentException("Empty input");
		
		for (int i : ids) 
		{
			if (i <= 0 || i > contacts.size()) throw new IllegalArgumentException("Contact ID does not exist");
			else rtn.add(contacts.get(i - 1));
		}
		
		return rtn;
	}

	/**{@inheritDoc}}
	 * 
	 * Searches by name are case sensitive.
	 * 
	 */
	@Override
	public Set<Contact> getContacts(String name) {
		
		if (name == null || name=="") throw new NullPointerException("Name string is null.");
		
		Set<Contact> rtn = new HashSet<Contact>();
		
			for (Contact c : contacts)
			{
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

	/*
	 * This creates the output to be written to file. 
	 */
	private String createFile()
	{
		String output = "";
		for (Contact c : contacts) output += contactToFileString(c) + "\n";
		for (Meeting m : meetings) output += meetingToFileString(m) + "\n";
		return output;
	}
	
	/*
	 * Converts contacts to a String. It's stored in a similar fashion to xml. Because open text fields (name and notes) could contain Name or Notes, the method searches for a similar word
	 * which is not contained in open text fields.
	 */
	private String contactToFileString(Contact c) 
	{		
		String notInName = "Name";
		
		int nameIterator = 1;
		while (c.getName().contains(notInName) || c.getNotes().contains(notInName)) //Find a unique word for name not contained within the name or notes fields to make finding it in file easy
		{
			notInName = "Name" + nameIterator;
			nameIterator++;
		}
		
		String notInNotes = "Notes";
		
		int notesIterator = 1;
		while (c.getName().contains(notInNotes) || c.getNotes().contains(notInNotes)) //Find a unique word for notes not contained within the name or notes fields to make finding it in file easy
		{
			notInNotes = "Notes" + notesIterator;
			notesIterator++;
		}
		
		String output = "<Contact>" 
						+ "<NameTag>" + notInName + "</NameTag>" 
						+ "<NotesTag>" + notInNotes + "</NotesTag>" 
						+ "<ID>" + c.getId() + "</ID>"
						+ "<" + notInName + ">" + c.getName() + "</" + notInName + ">"
						+ "<" + notInNotes + ">" + c.getNotes() + "</" + notInNotes + ">"
						+ "</Contact>";
		return output;
	}
	
	
	/*
	 * Converts a meeting to a string to be printed. Stored in a similar fashion to xml. Because "notes" is an open text field and therefore may contain any notes, the  method finds a 
	 * unique name for notes which is not contained in the open text field.
	 */
	private String meetingToFileString(Meeting m)
	{
		String stringId = "<ID>" + m.getId() + "</ID>";

		String stringCtId ="<Contacts>";
		for (Contact c : m.getContacts()) stringCtId += c.getId() + ",";
		stringCtId = stringCtId.substring(0, stringCtId.length() - 1) + "</Contacts>";
		
		String stringDate = "<Date>" + m.getDate().getTimeInMillis() + "</Date>";
		
		String stringNotes = "", stringClassBegin, stringClassEnd;
		
		try // If meeting is a PastMeeting then add notes.
		{
			String notInNotes = "Notes";
			int notesIterator = 1;
			while (((PastMeeting)m).getNotes().contains(notInNotes)) //Get a tag for notes which is not contained in the open text field
			{
				notInNotes = "Notes" + notesIterator;
				notesIterator++;
			}
			
			stringNotes = "<" + notInNotes + ">" + ((PastMeeting)m).getNotes() + "</" + notInNotes + ">";
			
			stringClassBegin = "<PastMeeting><NoteTag>" + notInNotes + "</NoteTag>"; //Set the tag at start and end to inform the class
			stringClassEnd = "</PastMeeting>";
		} catch (ClassCastException e)
		{
			stringNotes = "";
			stringClassBegin = "<FutureMeeting>"; //set the tag at the start and end of line to inform class
			stringClassEnd = "</FutureMeeting>";
		}
			
		return stringClassBegin + stringId + stringCtId + stringDate + stringNotes + stringClassEnd;
	}
	
	private void launch()
	{		
		if (!CONTACTS_FILE.exists())
			try {
				CONTACTS_FILE.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
		{
			
			try (BufferedReader in = new BufferedReader(new FileReader(CONTACTS_FILE))) 
			{
				String line;
				while ((line = in.readLine()) != null) 
					{
					readLine(line);
					}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	/*
	 * Reads a line from the input file and creates the object described by the line. 
	 */
	
	private boolean readLine(String line)
	{
		if (getRowClassType(line).equals("Contact")) //Read in a contact
		{
			String nameTag = getStringByTag(line,"NameTag");
			String notesTag = getStringByTag(line,"NotesTag");

			contacts.add(new ContactImpl(getIntsByTag(line,"ID")[0],getStringByTag(line,nameTag),getStringByTag(line,notesTag)));
			countContact++;
		}
		else if (getRowClassType(line).equals("FutureMeeting") || getRowClassType(line).equals("PastMeeting")) //Read in a meeting
		{
			int id = getIntsByTag(line,"ID")[0];
			
			Set<Contact> cts = new HashSet<Contact>(); 
			int[] ctIds = getIntsByTag(line,"Contacts"); //Get array containing Contact Ids
			for (int i = 0; i < ctIds.length; i++) cts.add(contacts.get(ctIds[i]-1)); //Add each contact to the set
			
			Calendar date = new GregorianCalendar(); 
			Long time = Long.parseLong(getStringByTag(line,"Date"));
			date.setTimeInMillis(time);
			
			if (getRowClassType(line).equals("PastMeeting")) //Get notes if a PastMeeting
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
	
	/*
	 * Given a line of input ("line"), it will find the String contained within "tag" such that <tag>String<\tag> 
	 */
	private String getStringByTag(String line, String tag)
	{
		int firstLoc = line.indexOf("<" + tag + ">") + 2 + tag.length();
		int lastLoc = line.indexOf("</" + tag + ">");
		
		return line.substring(firstLoc, lastLoc);
	}
	
	/*
	 * Given a line of input ("line"), it will find the the numbers contained within "tag" such that <tag>x,y,z<\tag> returned as the int[] array of form [x,y,z]
	 */
	private int[] getIntsByTag(String line, String tag)
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

	/*
	 * Returns a String describing the class of the line of input "line"
	 */
	private String getRowClassType(String line)
	{
		int wordLength = 0;
		while (line.charAt(1 + wordLength) != '>') wordLength++;
		return line.substring(1, 1+wordLength);
	}
}

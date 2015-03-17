

/**
 * Implementation of the Contact interface.
 * 
 * This class does not ensure that ID's are unique, and therefore has a dependency on another class to ensure that IDs provided to this are unique.
 * 
 * ContactManagerImpl ensures IDs provided to this are unique,
 * 	
 * Several assumptions have been made in the creation of this class:
 * <ul>
 * 	<li>Adding notes when notes already exist will, if the current notes and new string is nonempty, append these
 * 		with a space in the middle. Adding empty spaces will be appended in the same way. This has no impact for use 
 * 		within ContactManager as there is no way of altering notes once a Contact is created.</li>
 * 	<li>Nulls are allowed to be entered for name and notes. It has not been specified to throw any exceptions. The
 * 		class ContactManager deals with nulls and empty strings.</li>
 * 	<li>Empty names and notes can be stored.</li>
 * </ul>
 * @author Paul Day
 *
 */

/* Reason for ID being managed within ContactManagerImpl (copied from code of ContactManagerImpl):
 *  It was assumed that ContactManager should ensure the IDs are unique. The main reason for this was that Contact did not have methods to update and manage the file (for example, via a flush()), 
 *  while to ensure that IDs remain unique it would need access to this file and write every contact it creates to this file. Otherwise, when reading in a contact manager's file, should
 *  Contact have already created a contact it would be likely to contain duplicate IDs would be unique. It is also impossible to add a Contact to the ContactManager with a specific ID,
 *  which means that any Contact/Meeting created outside the ContactManager is unable to be imported. A similar thought argument applies to Meeting's as well. This, combined with the
 *  spec not making it clear that Contact/Meeting was meant to manage the IDs it was therefore assumed that the ContactManager was intended to manage the IDs, as to use a Contact/Meeting 
 *  elsewhere and within ContactManager it would need to be added via ContactManager first. 
 */

public class ContactImpl implements Contact {
	
	String name;
	String notes;
	int id;
	
	/**
	 * Creates a Contact. Name and Notes can be null. ID should be ensured that it is unique before it is passed into this method.
	 * 
	 * @param id the ID for the contact
	 * @param name the name of the contact
	 * @param notes the notes about the contact
	 */
	
	public ContactImpl(int id, String name, String notes)
	{
		this.id = id;
		this.name = name;
		this.notes = notes;
	}
	
	@Override
	public int getId() { return id; }

	@Override
	public String getName() { return name;}

	@Override
	public String getNotes() { return notes;}

	/**{@inheritDoc}
	 * 
	 * If the note parameter is null, no changes to notes shall be made.
	 * 
	 * Otherwise, adding notes when notes already exist will append the new notes to the old notes, with a space in between.
	 */
	
	@Override
	public void addNotes(String note) {
		if (note != null)
		{
			if (notes == null) notes = note;
			else if (notes.length() > 0) notes = notes + " " + note;
			else notes = note;
		}

	}
	
	@Override
	public boolean equals(Object other)
	{
		try{
			if (((Contact)other).getName().equals(name) && ((Contact)other).getId() == id 
					&& ((Contact)other).getNotes().equals(notes)) return true;
		}
		catch (ClassCastException e)
		{
			return false;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode() + notes.hashCode() + id;
	}

}

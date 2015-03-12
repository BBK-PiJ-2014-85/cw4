

/**
 * Implementation of the Contact interface.
 * 
 * Several assumptions have been made in the creation of this class:
 * 	- Adding notes when notes already exist will, if the current notes and new string is nonempty, append these
 * 		with a space in the middle. Adding empty spaces will be appended in the same way.
 * 	- nulls are allowed to be entered for name and notes. It has not been specified to throw any exceptions, and the
 * 		class contact manager deals with nulls and empty strings.
 * 	- Empty names and notes can be stored.
 * 
 * @author Paul Day
 *
 */

public class ContactImpl implements Contact {
	
	String name;
	String notes;
	int id;
	
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

	@Override
	public void addNotes(String note) {
		if (note != null)
		{
			if (notes == null) notes = note;
			else if (notes.length() > 0) notes = notes + " " + note;
			else notes = note;
		}

	}

}

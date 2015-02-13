package impls;

import interfaces.Contact;

/**
 * Implementation of the Contact interface.
 * 
 * Several assumptions have been made in the creation of this class:
 * 	- Adding notes when notes already exist will, if the current notes and new string is nonempty, append these
 * 	- nulls are allowed to be entered for name and notes. It has not been specified to throw any exceptions, and the
 * 		class contact manager deals with nulls and empty strings.
 * 
 * @author Paul Day
 *
 */

public class ContactImpl implements Contact {
	
	public ContactImpl(int iD, String name, String notes)
	{
		
	}
	
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNotes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addNotes(String note) {
		// TODO Auto-generated method stub

	}

}

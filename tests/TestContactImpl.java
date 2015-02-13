package tests;

import org.junit.Test;

/**
 * Tests the ContactImpl implementation of interface Contact.
 * 
 * @author Paul Day
 *
 */

public class TestContactImpl {

	/*
	 * 
	 * Thoughts on class:
	 * 	- ID will be set by constructor, managed by the contact manger. Would rather use contact manger rather than a static within contact to allow multiple contact managers being run at once
	 * 	- Name will be set by constructor, as provided by the user
	 * 	- Note is not necessary to add via constructor given addNote() method. 
	 * 
	 * This class then ends up pretty simple. Only need to test the add and remove for ID and Name, and adding notes, not adding notes, and adding multiple notes.
	 * 
	 * methods to test
	 * 
	 * getId
	 * 	- assume set 
	 * 
	 * 
	 * getName
	 * 
	 * getNotes
	 * 	- TODO does adding notes to already existing notes append, overwrite, or be impossible?
	 * 		- Current assumption is that it appends so you can add multiple notes about a contact
	 * 
	 * addNotes
	 * 
	 * 
	 */
	
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}

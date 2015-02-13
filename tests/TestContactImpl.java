package tests;

import interfaces.Contact;
import impls.ContactImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
	 * 	- Note looks necessary in contactManager method as well, as the only way of adding a contact requires a non empty input for notes. 
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
	 *  - TODO should nulls be allowed in contact?
	 *  
	 * 
	 * addNotes
	 * 
	 * What to do with nulls?
	 * 	- The interface does not state that the class will throw null pointer exceptions, whereas the class contact manager
	 * 		will. I therefore will allow setting nulls for name and notes, as I will need to do this in contactmanager anyhow
	 * 		as otherwise it wouldnt be able to deal with other implemetnations of contact which do not throw exceptions. 
	 * 
	 */
	
	
	@Test
	public void testID()
	{
		Contact ct = new ContactImpl(65,"Fred","A nice chap.");
		assertEquals(65,ct.getId());
	}
	
	@Test
	public void testIDNameNull()
	{
		Contact ct = new ContactImpl(65,null,"A nice chap.");
		assertEquals(65,ct.getId());
	}
	
	@Test
	public void testIDNoteNull()
	{
		Contact ct = new ContactImpl(65,"Fred",null);
		assertEquals(65,ct.getId());
	}
	
	@Test
	public void testName()
	{
		Contact ct = new ContactImpl(65,"Fred","A nice chap.");
		assertEquals("Fred",ct.getName());
	}
	
	@Test
	public void testNameWithSpaces()
	{
		Contact ct = new ContactImpl(65,"Fred dy","A nice chap.");
		assertEquals("Fred dy",ct.getName());
	}
	
	@Test
	public void testNullName()
	{
			Contact ct = new ContactImpl(65,null,"A nice chap.");
			assertNull(ct.getName());
	}
	
	
	@Test
	public void testNameNotesNull()
	{
			Contact ct = new ContactImpl(65,"Fred",null);
			assertEquals("Fred",ct.getName());
	}
	
	@Test
	public void testNameBothNameAndNotesNull()
	{
		Contact ct = new ContactImpl(65,null,null);
		assertNull(ct.getName());
	}
	
	@Test
	public void testEmptyNameString() 
	{
		Contact ct = new ContactImpl(65,"","Some notes.");
		assertEquals("",ct.getName());
	}
	
	@Test
	public void testNotesFromConstructor()
	{
		Contact ct = new ContactImpl(65,"","Some notes.");
		assertEquals("Some notes.",ct.getNotes());
	}
	
	@Test
	public void testNotesFromConstructorNameNull()
	{
		Contact ct = new ContactImpl(65,null,"Some notes.");
		assertEquals("Some notes.",ct.getNotes());
	}
	
	@Test
	public void testEmptyNotesFromConstructor()
	{
		Contact ct = new ContactImpl(65,"Bob","");
		assertEquals("",ct.getNotes());
	}
	
	@Test
	public void testNullNotesFromConstructor()
	{
		Contact ct = new ContactImpl(65,"Bob",null);
		assertNull(ct.getNotes());
	}
	
	@Test
	public void testAddNotesToEmpty()
	{
		Contact ct = new ContactImpl(65,"Bob","");
		ct.addNotes("This should be the first line.");
		assertEquals("This should eb the first line.", ct.getNotes());
	}
	
	@Test
	public void testAddNotesToNull()
	{
		Contact ct = new ContactImpl(65,"Bob",null);
		ct.addNotes("This should be the first line.");
		assertEquals("This should eb the first line.", ct.getNotes());
	}
	
	@Test
	public void testAddNotesToNonEmpty()
	{
		Contact ct = new ContactImpl(65,"Bob","Line 1.");
		ct.addNotes("Line 2.");
		assertEquals("Line1. Line2.", ct.getNotes());
	}
	


}

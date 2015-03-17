
import java.util.Calendar;
import java.util.Set;

/**
 * An implementation of the FutureMeeting interface.
 * 
 * @author Paul Day
 *
 */

public class FutureMeetingImpl extends MeetingImpl implements FutureMeeting  {

	public FutureMeetingImpl(int id, Calendar date, Set<Contact> attendees) {
		super(id, date, attendees);
	}
}

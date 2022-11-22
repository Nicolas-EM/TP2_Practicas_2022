package exceptionsUtils;

public class ExceptionsUtils {
	public static final String INVALID_NEGATIVE = "must be a positive value";
	public static final String NULL_PARAMETER = "cannot be null";
	public static final String INVALID_VPOSITION = "Vehicle position must be 0";
	public static final String INVALID_VSpeed = "Vehicle speed must be 0";
	
	public static final String INVALID_CONTAMINATION = "The ’contamination class’ must be a value between 0 and 10";
	public static final String INVALID_ITENERARY_SIZE = "The ’itenerary’ must have more than 2 items";
	public static final String INVALID_MAX_SPEED = "The ’max speed’ must be a positive value.";
	//Used in Events
	public static final String EMPTY_EVENTS = "The ’events list’ is empty.";

	// Used in RoadMap
	public static final String DUPLICATE_ID = "id is already in use - failed to add to map";
}

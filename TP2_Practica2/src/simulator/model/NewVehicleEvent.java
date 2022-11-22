package simulator.model;

import java.util.ArrayList;
import java.util.List;

public class NewVehicleEvent extends Event {
	private String id;
	private int maxSpeed;
	private int contClass;
	private List<String> itineraryIDs;

	public NewVehicleEvent(int time, String id, int maxSpeed, int contClass, List<String> itinerary) {
		super(time);
		
		this.id = id;
		this.maxSpeed = maxSpeed;
		this.contClass = contClass;
		this.itineraryIDs = itinerary;
	}

	@Override
	void execute(RoadMap map) {
		List<Junction> itinerary = new ArrayList<>();
		for(String id : this.itineraryIDs) {
			itinerary.add(map.getJunction(id));
		}
		
		Vehicle v = new Vehicle(id, maxSpeed, contClass, itinerary);
		
		try {
			map.addVehicle(v);
			v.moveToNextRoad();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object getPriority() {
		// TODO Auto-generated method stub
		return null;
	}
	public String toString() {
		return String.format("New Vehicle '%s'", id);
	}
	
}
package simulator.factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.Event;
import simulator.model.NewVehicleEvent;

public class NewVehicleEventBuilder extends Builder<Event>{
	private static final String type = "new_vehicle";

	public NewVehicleEventBuilder() {
		super(type);
	}

	@Override
	protected Event createTheInstance(JSONObject data) {
		if(data.has("time") && data.has("id") && data.has("maxspeed") && data.has("class") && data.has("itinerary")) {	
			return new NewVehicleEvent(data.getInt("time"), data.getString("id"), data.getInt("maxspeed"), data.getInt("class"), getItinerary(data.getJSONArray("itinerary")));
		}
		else
			return null;
	}

	// Extracts List<String> from JSONArray
	private List<String> getItinerary(JSONArray data){
		List<String> itinerary = new ArrayList<>();
		
		for(int i = 0; i < data.length(); i++) itinerary.add(data.getString(i));
		
		return itinerary;
	}
}

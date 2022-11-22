package simulator.factories;

import org.json.JSONObject;
import simulator.model.Event;
import simulator.model.Weather;

public abstract class NewRoadEventBuilder extends Builder<Event>{	
	NewRoadEventBuilder(String type) {
		super(type);
	}

	@Override
	protected Event createTheInstance(JSONObject data) {
		if(data.has("time") && data.has("id") && data.has("src") && data.has("dest") && data.has("length") && data.has("co2limit") && data.has("maxspeed") && data.has("weather"))
			return createRoad(data.getInt("time"), data.getString("id"), data.getString("src"), data.getString("dest"), data.getInt("length"), data.getInt("co2limit"), data.getInt("maxspeed"), Weather.valueOf((data.getString("weather")).toUpperCase()));
		else return null;
	}
	
	protected abstract Event createRoad(int time, String id, String srcJun, String destJunc, int length, int co2Limit, int maxSpeed, Weather weather);
}

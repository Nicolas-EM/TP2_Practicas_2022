package simulator.factories;

import simulator.model.Event;
import simulator.model.NewInterCityRoadEvent;
import simulator.model.Weather;

public class NewInterCityRoadEventBuilder extends NewRoadEventBuilder{
	private final static String type = "new_inter_city_road"; 
	
	public NewInterCityRoadEventBuilder() {
		super(type);
	}
	
	protected Event createRoad(int time, String id, String srcJun, String destJunc, int length, int co2Limit, int maxSpeed, Weather weather) {
		return new NewInterCityRoadEvent(time, id, srcJun, destJunc, length, co2Limit, maxSpeed, weather);
	}
}

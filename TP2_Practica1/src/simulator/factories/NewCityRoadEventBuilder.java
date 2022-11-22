package simulator.factories;

import simulator.model.Event;
import simulator.model.NewCityRoadEvent;
import simulator.model.Weather;

public class NewCityRoadEventBuilder extends NewRoadEventBuilder{
	private final static String type = "new_city_road";

	public NewCityRoadEventBuilder() {
		super(type);
	}
	
	protected Event createRoad(int time, String id, String srcJun, String destJunc, int length, int co2Limit, int maxSpeed, Weather weather) {
		return new NewCityRoadEvent(time, id, srcJun, destJunc, length, co2Limit, maxSpeed, weather);
	}
}
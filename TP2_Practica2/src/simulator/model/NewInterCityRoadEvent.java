package simulator.model;

public class NewInterCityRoadEvent extends NewRoadEvent {

	public NewInterCityRoadEvent(int time, String id, String srcJun, String destJunc, int length, int co2Limit, int maxSpeed,
			Weather weather) {
		super(time, id, srcJun, destJunc, length, co2Limit, maxSpeed, weather);
	}


	@Override
	void execute(RoadMap map) {
		try {
			map.addRoad(new InterCityRoad(id, getJunction(map, srcJunc), getJunction(map, destJunc), maxSpeed, co2Limit, length, weather));
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
		return String.format("New Inter City Road '%s'", id);
	}
}

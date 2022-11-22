package simulator.model;

public class CityRoad extends Road {

	public CityRoad(String id, Junction srcJunc, Junction destJunc, int maxSpeed, int contLimit, int length, Weather weather) {
		super(id, srcJunc, destJunc, maxSpeed, contLimit, length, weather);
	}
	
	@Override
	public void reduceTotalContamination(){
		if(this._totalCont - this.weatherToX(this._weather) < 0) this._totalCont = 0;
		else this._totalCont -= this.weatherToX(this._weather);
	}	
	
	@Override
	int calculateVehicleSpeed(Vehicle v) {
		return ((11 - v.getContClass()) * this._speedLimit) / 11;
	}

	@Override
	void updateSpeedLimit() {
		this._speedLimit = this._maxSpeed;
	}
	
	public int weatherToX(Weather w) {
		switch(w) {
		case SUNNY: 
		case CLOUDY:
		case RAINY:
			return 2;
		case WINDY:
		case STORM:
			return 10;
		default:
			throw new IllegalArgumentException("How did we even get here? [CityRoad.weatherToX()]");
		}
	}
}

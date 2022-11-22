package simulator.model;

public class InterCityRoad extends Road {
	public InterCityRoad(String id, Junction srcJunc, Junction destJunc, int maxSpeed, int contLimit, int length, Weather weather) {
		super(id, srcJunc, destJunc, maxSpeed, contLimit, length, weather);
	}

	@Override
	public void reduceTotalContamination(){
		this._totalCont = ((100 - this.weatherToX(this._weather))*this._totalCont) / 100;
	}
	
	@Override
	void updateSpeedLimit() {
		if(this._totalCont > this._contLimit)
			this._speedLimit = this._maxSpeed / 2;
		else
			this._speedLimit = this._maxSpeed;
	}

	@Override
	int calculateVehicleSpeed(Vehicle v) {
		if(this._weather == Weather.STORM) return (this._speedLimit * 8)/10;
		else return this._speedLimit;
	}
	
	public int weatherToX(Weather w) {
		switch(w) {
		case SUNNY: 
			return 2;
		case CLOUDY:
			return 3;
		case RAINY:
			return 10;
		case WINDY:
			return 15;
		case STORM:
			return 20;
		default:
			throw new IllegalArgumentException("How did we even get here? [InterCityRoad.weatherToX()]");
		}
	}
}

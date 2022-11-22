package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import exceptionsUtils.ExceptionsUtils;

public abstract class Road extends SimulatedObject {
	protected Junction _srcJunc;	// cruce origen
	protected Junction _destJunc;	// cruce destino
	protected int _length;			// longitud carretera
	protected int _maxSpeed;		// velocidad maxima
	protected int _speedLimit;		// limite actual de velocidad
	protected int _contLimit;		// limite de contaminacion 
	protected Weather _weather;		// condiciones ambientales en carretera
	protected int _totalCont;		// contaminacion acumulada
	protected List<Vehicle> _vehicles;	// lista de vehiculos en carretera

	public Road(String id, Junction _srcJunc, Junction _destJunc, int _maxSpeed, int _contLimit, int _length,
			Weather _weather) {
		// Check if parameters are valid
		super(id);
		if (_maxSpeed <= 0)
			throw new IllegalArgumentException(String.format("[ERROR]: ", ExceptionsUtils.INVALID_MAX_SPEED));
		else if (_contLimit <= 0)
			throw new IllegalArgumentException(
					String.format("[ERROR]: %s %s", "The ’contamination’", ExceptionsUtils.INVALID_NEGATIVE));
		else if (_length <= 0) {
			throw new IllegalArgumentException(
					String.format("[ERROR]: %s %s", "The ’_length’", ExceptionsUtils.INVALID_NEGATIVE));
		} else if (_srcJunc == null) {
			throw new IllegalArgumentException(
					String.format("[ERROR]: %s %s", "Source junction", ExceptionsUtils.NULL_PARAMETER));
		} else if (_destJunc == null) {
			throw new IllegalArgumentException(
					String.format("[ERROR]: %s %s", "Destination junction", ExceptionsUtils.NULL_PARAMETER));
		} else if (_weather == null) {
			throw new IllegalArgumentException(
					String.format("[ERROR]: %s %s", "Weather", ExceptionsUtils.NULL_PARAMETER));
		} else {
			// All parameters are valid
			this._srcJunc = _srcJunc;
			
			this._destJunc = _destJunc;
			
			this._maxSpeed = _maxSpeed;
			this._speedLimit=_maxSpeed;
			this._contLimit = _contLimit;
			this._length = _length;
			this._weather = _weather;
			
			this._vehicles = new ArrayList<>();
			
			this._srcJunc.addOutGoingRoad(this);
			this._destJunc.addIncomingRoad(this);
		}
	}

	protected void enter(Vehicle v) throws IllegalArgumentException {
		if (v.getLocation() != 0)
			throw new IllegalArgumentException(String.format("[ERROR]: ", ExceptionsUtils.INVALID_VPOSITION));
		else if (v.getSpeed() != 0)
			throw new IllegalArgumentException(String.format("[ERROR]: ", ExceptionsUtils.INVALID_VSpeed));
		else {
			// Add and sort
			this._vehicles.add(v);
		}
	}

	protected void exit(Vehicle v) {
		List<Vehicle> temp = new ArrayList<Vehicle>();
		for (Vehicle ve : _vehicles) {
			if (!ve.equals(v)) 
				temp.add(ve);
		}
		this._vehicles = temp;
	}

	protected void setWeather(Weather w) throws IllegalArgumentException {
		if (w != null) {
			this._weather = w;
		} else {
			throw new IllegalArgumentException(String.format("[ERROR]: %s %s", "Weather", ExceptionsUtils.NULL_PARAMETER));
		}
	}

	protected void addContamination(int c) throws IllegalArgumentException {
		if (c >= 0)
			this._totalCont += c;
		else
			throw new IllegalArgumentException(String.format("[ERROR]: %s %s", "The ’contamination’", ExceptionsUtils.INVALID_NEGATIVE));
	}

	// Implementadas por subclasses
	@Override
	abstract void reduceTotalContamination();
	@Override
	abstract void updateSpeedLimit();
	@Override
	abstract int calculateVehicleSpeed(Vehicle v);

	@Override
	void advance(int time) {
		this.reduceTotalContamination();
		this.updateSpeedLimit();
		for (Vehicle ve : _vehicles) {
			ve.setSpeed(this.calculateVehicleSpeed(ve));
			ve.advance(0);
		}
		this._vehicles.sort(Comparator.comparing(Vehicle::getLocation).reversed());
	}

	@Override
	public JSONObject report() {
		JSONObject jsonO = new JSONObject();

		jsonO.put("id", this.getId());
		jsonO.put("speedlimit", this._speedLimit);
		jsonO.put("weather", this._weather.toString());
		jsonO.put("co2", _totalCont);
		jsonO.put("vehicles", this.getVehicleIDs());

		return jsonO;
	}

	private JSONArray getVehicleIDs() {
		JSONArray vehicleIDs = new JSONArray();
		for(Vehicle v : this._vehicles) {
			vehicleIDs.put(v.getId());
		}
		return vehicleIDs;
	}

	public int getLength() {
		return this._length;
	}

	public Junction getDest() {
		return this._destJunc;
	}

	public Junction getSrc() {
		return this._srcJunc;
	}

	public Weather getWeather() {
		return this._weather;
	}

	public int getContLimit() {
		return this._contLimit;
	}

	public int getMaxSpeed() {
		return this._maxSpeed;
	}

	public double getTotalCO2() {
		return this._totalCont;
	}

	public int getSpeedLimit() {
		return this._speedLimit;
	}

	public java.util.List<Vehicle> getVehicles() {
		return Collections.unmodifiableList(_vehicles);
	}
}
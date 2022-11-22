package simulator.model;

import org.json.JSONObject;
import exceptionsUtils.ExceptionsUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vehicle extends SimulatedObject {
	private java.util.List<Junction> _itinerario; // lista de cruces
	private int _index; // indice indicando ultimo cruce del itinerario
	private int _maxSpeed; // Velocidad máxima
	private int _currentSpeed; // Velocidad actual
	private VehicleStatus _status; // Pending, Traveling, Waiting o Arrived
	private Road _road; // carretera actual
	private int _location; // Localización
	private int _contClass; // Grado de Contaminación
	private int _totalCont; // Contaminación total
	private int _distance; // Distancia total recorrida

	Vehicle(String id, int _maxSpeed, int _contClass, List<Junction> itinerary) {
		super(id);

		if (_maxSpeed <= 0)
			throw new IllegalArgumentException(String.format("[ERROR]: ", ExceptionsUtils.INVALID_MAX_SPEED));
		else if (_contClass < 0 || _contClass > 10)
			throw new IllegalArgumentException(String.format("[ERROR]: ", ExceptionsUtils.INVALID_CONTAMINATION));
		else if (itinerary.size() < 2) {
			throw new IllegalArgumentException(String.format("[ERROR]: ", ExceptionsUtils.INVALID_ITENERARY_SIZE));
		} else {
			this._itinerario = Collections.unmodifiableList(new ArrayList<>(itinerary));
			this._maxSpeed = _maxSpeed;
			this._currentSpeed = 0;
			this._status = VehicleStatus.PENDING;
			this._road = null;
			this._location = 0;
			this._contClass = _contClass;
			this._totalCont = 0;
			this._distance = 0;
		}
	}

	protected void setSpeed(int s) throws IllegalArgumentException {
		if (s < 0)
			throw new IllegalArgumentException(String.format("[ERROR]: ", ExceptionsUtils.INVALID_MAX_SPEED));
		else if (this._status == VehicleStatus.TRAVELING)
			_currentSpeed = Math.min(_maxSpeed, s);
	}

	protected void setContClass(int c) throws IllegalArgumentException {
		if (c < 0 || c > 10)
			throw new IllegalArgumentException(String.format("[ERROR]: ", ExceptionsUtils.INVALID_CONTAMINATION));
		else
			_contClass = c;
	}

	@Override
	protected void advance(int time) {
		if (this._status.equals(VehicleStatus.TRAVELING)) {
			int posPrevia = _location;

			// Actualizar localización
			this._location = Math.min(this._location + this._currentSpeed, this._road.getLength());

			// Actualizar distancia
			this._distance += this._location - posPrevia;

			// Calcula contaminación
			int co2 = _contClass * (_location - posPrevia);
			_totalCont += co2;

			this._road.addContamination(co2);

			// Ver si se añade a cola de cruce
			if (_location >= this._road.getLength()) {
				this._itinerario.get(this._index + 1).enter(this);
				this._status = VehicleStatus.WAITING;
				this._currentSpeed = 0;
			}
		}
	}

	protected void moveToNextRoad() {
		if (this._status.equals(VehicleStatus.PENDING) || this._status.equals(VehicleStatus.WAITING)) {
			this._location = 0;

			// Si NO está pending, salir de road actual
			if (!this._status.equals(VehicleStatus.PENDING))
				this._road.exit(this);

			// Actualizar indice de itinerario
			if (this._status.equals(VehicleStatus.PENDING))
				this._index = 0;
			else
				this._index++;

			if (this._index >= this._itinerario.size() - 1)
				this._status = VehicleStatus.ARRIVED;
			else {
				// Entrar en road a prox. cruce
				Road nextRoad = this._itinerario.get(_index).roadTo(this._itinerario.get(_index + 1));
				nextRoad.enter(this);
				this._road = nextRoad;

				this._status = VehicleStatus.TRAVELING;
			}
		}
	}

	@Override
	public JSONObject report() {
		JSONObject jsonO = new JSONObject();

		jsonO.put("id", this.getId());
		jsonO.put("speed", this.getSpeed());
		jsonO.put("distance", this._distance);
		jsonO.put("co2", this._totalCont);
		jsonO.put("class", this.getContClass());
		jsonO.put("status", this.getStatus().toString());

		if (!this._status.equals(VehicleStatus.PENDING) && !this._status.equals(VehicleStatus.ARRIVED)) {
			jsonO.put("road", this._road.getId());
			jsonO.put("location", this._location);
		}

		return jsonO;
	}

	public int getLocation() {
		return this._location;
	}

	public int getSpeed() {
		return this._currentSpeed;
	}

	public int getMaxSpeed() {
		return this._maxSpeed;
	}

	public int getContClass() {
		return this._contClass;
	}

	public VehicleStatus getStatus() {
		return this._status;
	}

	public int getTotalCO2() {
		return this._totalCont;
	}
	public int getDistance() {
		return this._distance;
	}

	public java.util.List<Junction> getItinerary() {
		return this._itinerario;
	}

	public Road getRoad() {
		return this._road;
	}

	// no hacen nada por ahora
	@Override
	void updateSpeedLimit() {
	}

	@Override
	int calculateVehicleSpeed(Vehicle v) {
		return 0;
	}

	@Override
	void reduceTotalContamination() {
	}
}

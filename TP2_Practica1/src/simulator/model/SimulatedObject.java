package simulator.model;

import org.json.JSONObject;

public abstract class SimulatedObject {
	protected String _id;

	SimulatedObject(String id) {
		if ( id == null || id.length() == 0)
			throw new IllegalArgumentException("The �id� must be a nonempty string.");
		else
			_id = id;
	}

	public String getId() {
		return _id;
	}

	@Override
	public String toString() {
		return _id;
	}

	abstract void advance(int time);
	abstract public JSONObject report();
	abstract void updateSpeedLimit();
	abstract int calculateVehicleSpeed(Vehicle v);
	abstract void reduceTotalContamination();
}

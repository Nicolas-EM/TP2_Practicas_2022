package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import exceptionsUtils.ExceptionsUtils;

public class RoadMap {
	List<Junction> cruces;
	List<Road> roads;
	List<Vehicle> vehicles;
	Map<String, Junction> mapCruces;
	Map<String, Road> mapRoads;
	Map<String, Vehicle> mapVehicles;

	public RoadMap() {
		this.cruces = new ArrayList<>();
		this.roads = new ArrayList<>();
		this.vehicles = new ArrayList<>();
		
		// Usamos HashMap porque get() y containsKey son O(1)
		this.mapCruces = new HashMap<>();
		this.mapRoads = new HashMap<>();
		this.mapVehicles = new HashMap<>();
	}

	protected void addJunction(Junction j) throws Exception {
		if (this.mapCruces.containsKey(j.getId()))
			throw new Exception(String.format("Junction %s", ExceptionsUtils.DUPLICATE_ID));
		else {
			this.cruces.add(j);
			this.mapCruces.put(j.getId(), j);
		}
	}

	protected void addRoad(Road r) throws Exception {
		if (this.mapRoads.containsKey(r.getId()))
			throw new Exception(String.format("Road %s", ExceptionsUtils.DUPLICATE_ID));
		else {
			this.roads.add(r);
			this.mapRoads.put(r.getId(), r);
		}
	}

	protected void addVehicle(Vehicle v) throws Exception {
		if (this.mapVehicles.containsKey(v.getId()))
			throw new Exception(String.format("Vehicle %s", ExceptionsUtils.DUPLICATE_ID));
		else if(!itineraryValid(v.getItinerary())) {
			throw new Exception("Itinerary is invalid");
		}
		else {
			this.vehicles.add(v);
			this.mapVehicles.put(v.getId(), v);
		}
	}

	private boolean itineraryValid(List<Junction> itinerary) {
		for(int i = 1; i < itinerary.size(); i++) {
			Junction a = itinerary.get(i-1);
			Junction b = itinerary.get(i);
			if(a.roadTo(b) == null) 
				return false;
		}
		return true;
	}

	public Junction getJunction(String id) {
		return this.mapCruces.get(id);
	}

	public Road getRoad(String id) {
		return this.mapRoads.get(id);
	}

	public Vehicle getVehicle(String id) {
		return this.mapVehicles.get(id);
	}

	public List<Junction> getJunctions() {
		return Collections.unmodifiableList(new ArrayList<>(this.cruces));
	}

	public List<Road> getRoads() {
		return Collections.unmodifiableList(new ArrayList<>(this.roads));
	}

	public List<Vehicle> getVehilces() {
		return Collections.unmodifiableList(new ArrayList<>(this.vehicles));
	}

	protected void reset() {
		cruces.clear();
		roads.clear();
		vehicles.clear();
		mapCruces.clear();
		mapRoads.clear();
		mapVehicles.clear();
	}

	public JSONObject report() {
		JSONObject jsonO = new JSONObject();

		jsonO.put("junctions", collectionReport(this.getJunctions()));
		jsonO.put("roads", collectionReport(this.getRoads()));
		jsonO.put("vehicles", collectionReport(this.getVehilces()));

		return jsonO;
	}
	
	private <T extends SimulatedObject> JSONArray collectionReport(List<T> list) {
		JSONArray report = new JSONArray();
		for(T i : list) {
			report.put(i.report());
		}
		return report;
	}
}

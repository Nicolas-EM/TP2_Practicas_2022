package simulator.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;
import simulator.misc.SortedArrayList;

public class TrafficSimulator {
	private RoadMap _roadsMap;
	private List<Event> _events;
	private int _time = 0;

	public TrafficSimulator() {
		this._roadsMap = new RoadMap();
		this._events = new SortedArrayList<>();
		this._time = 0;
	}

	public void addEvent(Event e) {
		this._events.add(e);
	}

	public void advance() {
		this._time += 1;
		for(int i = 0; i < _events.size(); i++) {
			Event e = _events.get(i);
			if (e._time == this._time) {
				e.execute(_roadsMap);
				_events.remove(e);
				i--;
			}
		}
		for(Junction j : this._roadsMap.getJunctions())
			j.advance(_time);
		for(Road r : this._roadsMap.getRoads())
			r.advance(_time);
	}

	public void reset() {
		this._events.clear();
		this._time = 0;
		this._roadsMap.reset();
	}

	public JSONObject report() {
		JSONObject jsonO = new JSONObject();
		JSONObject state = new JSONObject();
		
		state.put("junctions", collectionReport(this._roadsMap.getJunctions()));
		state.put("roads", collectionReport(this._roadsMap.getRoads()));
		state.put("vehicles", collectionReport(this._roadsMap.getVehicles()));
		
		jsonO.put("time", this._time);
		jsonO.put("state", state);

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

package simulator.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import simulator.misc.SortedArrayList;

public class TrafficSimulator implements Observable<TrafficSimObserver> {
	private RoadMap _roadsMap;
	private List<Event> _events;
	private int _time = 0;
	private List<TrafficSimObserver> observerList;

	public TrafficSimulator() {
		this._roadsMap = new RoadMap();
		this._events = new SortedArrayList<>();
		this._time = 0;
		this.observerList = new ArrayList<TrafficSimObserver>();
	}

	public void addEvent(Event e) {
		this._events.add(e);
		Collections.sort(this._events);  
		this.onEventAdded(this._roadsMap, this._events, e, this._time);
	}

	public void advance() {
		this._time += 1;

		onAdvanceStart(this._roadsMap, this._events, this._time);

		for (int i = 0; i < _events.size(); i++) {
			Event e = _events.get(i);
			if (e._time == this._time) {
				e.execute(_roadsMap);
				_events.remove(e);
				i--;
			}
		}
		for (Junction j : this._roadsMap.getJunctions())
			j.advance(_time);
		for (Road r : this._roadsMap.getRoads())
			r.advance(_time);

		onAdvanceEnd(this._roadsMap, this._events, this._time);
	}

	public void reset() {
		this._events.clear();
		this._time = 0;
		this._roadsMap.reset();

		this.onReset(_roadsMap, _events, _time);
	}

	public JSONObject report() {
		JSONObject jsonO = new JSONObject();
		JSONObject state = new JSONObject();

		state.put("junctions", collectionReport(this._roadsMap.getJunctions()));
		state.put("roads", collectionReport(this._roadsMap.getRoads()));
		state.put("vehicles", collectionReport(this._roadsMap.getVehilces()));

		jsonO.put("time", this._time);
		jsonO.put("state", state);

		return jsonO;
	}

	private <T extends SimulatedObject> JSONArray collectionReport(List<T> list) {
		JSONArray report = new JSONArray();
		for (T i : list) {
			report.put(i.report());
		}
		return report;
	}

	@Override
	public void addObserver(TrafficSimObserver o) {
		this.observerList.add(o);
		this.onRegister(_roadsMap, _events, _time);
	}

	@Override
	public void removeObserver(TrafficSimObserver o) {
		this.observerList.remove(o);
	}

	private void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		for (TrafficSimObserver o : this.observerList) {
			o.onAdvanceStart(map, events, time);
		}
	}

	private void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		for (TrafficSimObserver o : this.observerList) {
			o.onAdvanceEnd(map, events, time);
		}
	}

	private void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		for (TrafficSimObserver o : this.observerList) {
			o.onEventAdded(map, events, e, time);
		}
	}

	private void onReset(RoadMap map, List<Event> events, int time) {
		for (TrafficSimObserver o : this.observerList) {
			o.onReset(map, events, time);
		}
	}

	private void onRegister(RoadMap map, List<Event> events, int time) {
		for (TrafficSimObserver o : this.observerList) {
			o.onRegister(map, events, time);
		}
	}

	public void onError(String error) {

	}
}

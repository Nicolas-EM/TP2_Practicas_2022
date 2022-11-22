package simulator.control;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import exceptionsUtils.ExceptionsUtils;
import simulator.factories.Factory;
import simulator.model.Event;
import simulator.model.TrafficSimObserver;
import simulator.model.TrafficSimulator;

public class Controller {
	private TrafficSimulator traffic_simulator;
	private Factory<Event> events_factory;

	public Controller(TrafficSimulator sim, Factory<Event> eventsFactory) throws Exception {
		if (sim == null || eventsFactory == null) {
			throw new NullPointerException(String.format("Controller Parameters %s", ExceptionsUtils.NULL_PARAMETER));
		} else {
			this.traffic_simulator = sim;
			this.events_factory = eventsFactory;
		}
	}

	public void loadEvents(InputStream in) throws Exception {
		JSONObject jo = new JSONObject(new JSONTokener(in));
		if (jo.has("events")) {
			JSONArray jo2 = jo.getJSONArray("events");
			for (int i = 0; i < jo2.length(); i++) {
				this.traffic_simulator.addEvent(this.events_factory.createInstance(jo2.getJSONObject(i)));
			}
		} else {
			throw new Exception(String.format("%s", ExceptionsUtils.EVENTS_ERROR));
		}
	}

	public void run(int n, OutputStream out) {
		PrintStream o = new PrintStream(out);
		o.println("{");
		o.println(" \"states\": [");
		for (int i = 0; i < n; i++) {
			this.traffic_simulator.advance();
			o.print(this.traffic_simulator.report().toString(3));
			if (i != n - 1)
				o.println(",");
		}
		o.println("]");
		o.println("}");
	}

	public void run(int n) {
		for (int i = 0; i < n; i++) {
			this.traffic_simulator.advance();
		}
	}

	public void reset() {
		this.traffic_simulator.reset();
	}

	public void addObserver(TrafficSimObserver o) {
		this.traffic_simulator.addObserver(o);
	}

	public void removeObserver(TrafficSimObserver o) {
		this.traffic_simulator.removeObserver(o);
	}

	public void addEvent(Event e) {
		this.traffic_simulator.addEvent(e);
	}
}
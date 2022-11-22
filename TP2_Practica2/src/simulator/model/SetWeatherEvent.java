package simulator.model;

import java.util.List;

import exceptionsUtils.ExceptionsUtils;
import simulator.misc.Pair;

public class SetWeatherEvent extends Event{
	private List<Pair<String,Weather>> ws;	
	
	public SetWeatherEvent(int time, List<Pair<String,Weather>> ws) {
		super(time);
		
		if(ws != null)
			this.ws = ws;
	}

	@Override
	void execute(RoadMap map) {
		for(Pair<String,Weather> w : this.ws) {
			try {
				Road r = map.getRoad(w.getFirst());
				if(r == null)
					throw new Exception(String.format("Road with specified ID %i %s", w.getFirst(), ExceptionsUtils.NULL_PARAMETER));
				else {
					r.setWeather(w.getSecond());
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Object getPriority() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toString() {
		String string = "Change road Weather: [";
		for(int i = 0; i < ws.size(); i++) {
			Pair<String, Weather> p = ws.get(i);
			if(i != 0)
				string += ", ";
			string += String.format("(%s, %s)", p.getFirst(), p.getSecond().toString());
		}
		string += "]";
		return string;
	}
}
package simulator.factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Pair;
import simulator.model.Event;
import simulator.model.SetWeatherEvent;
import simulator.model.Weather;

public class SetWeatherEventBuilder extends Builder<Event>{
	private static final String type = "set_weather";
	
	public SetWeatherEventBuilder() {
		super(type);
	}

	@Override
	protected Event createTheInstance(JSONObject data) {
		if(data.has("time") && data.has("info"))
			try {
				return new SetWeatherEvent(data.getInt("time"), getWeatherInfo(data.getJSONArray("info")));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		else
			return null;
	}

	private List<Pair<String, Weather>> getWeatherInfo(JSONArray jsonArray) {
		List<Pair<String, Weather>> ws = new ArrayList<>();
		
		for(int i = 0; i < jsonArray.length(); i++) {
			JSONObject o = jsonArray.getJSONObject(i);
			ws.add(new Pair<>(o.getString("road"), Weather.valueOf((o.getString("weather")).toUpperCase())));
		}
		
		return ws;
	}

}

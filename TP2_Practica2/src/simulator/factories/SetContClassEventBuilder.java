package simulator.factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Pair;
import simulator.model.Event;
import simulator.model.SetContClassEvent;

public class SetContClassEventBuilder extends Builder<Event>{
	private static final String type = "set_cont_class";

	public SetContClassEventBuilder() {
		super(type);
	}

	@Override
	protected Event createTheInstance(JSONObject data) {
		if(data.has("time") && data.has("info"))
			try {
				return new SetContClassEvent(data.getInt("time"), getContInfo(data.getJSONArray("info")));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		else
			return null;
	}

	private List<Pair<String, Integer>> getContInfo(JSONArray jsonArray) {
		List<Pair<String, Integer>> cs = new ArrayList<>();
		
		for(int i = 0; i < jsonArray.length(); i++) {
			JSONObject o = jsonArray.getJSONObject(i);
			cs.add(new Pair<>(o.getString("vehicle"), o.getInt("class")));
		}
		
		return cs;
	}
}
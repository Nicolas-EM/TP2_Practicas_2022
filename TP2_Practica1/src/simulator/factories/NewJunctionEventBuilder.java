package simulator.factories;

import org.json.JSONObject;

import simulator.model.DequeuingStrategy;
import simulator.model.Event;
import simulator.model.LightSwitchingStrategy;
import simulator.model.NewJunctionEvent;

public class NewJunctionEventBuilder extends Builder<Event>{
	private static final String type = "new_junction";
	private Factory<LightSwitchingStrategy> lssFactory;
	private Factory<DequeuingStrategy> dqsFactory;

	public NewJunctionEventBuilder(Factory<LightSwitchingStrategy> lssFactory, Factory<DequeuingStrategy> dqsFactory) {
		super(NewJunctionEventBuilder.type);
		this.lssFactory = lssFactory;
		this.dqsFactory = dqsFactory;
	}

	@Override
	protected Event createTheInstance(JSONObject data) {
		if(data.has("time") && data.has("id") && data.has("ls_strategy") && data.has("dq_strategy") && data.has("coor"))
			return new NewJunctionEvent(data.getInt("time"), data.getString("id"), lssFactory.createInstance(data.getJSONObject("ls_strategy")), dqsFactory.createInstance(data.getJSONObject("dq_strategy")), data.getJSONArray("coor").getInt(0), data.getJSONArray("coor").getInt(1));
		else return null;
	}
}
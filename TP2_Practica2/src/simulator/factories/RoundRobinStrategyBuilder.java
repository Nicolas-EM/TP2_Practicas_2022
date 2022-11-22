	package simulator.factories;

import org.json.JSONObject;

import simulator.model.LightSwitchingStrategy;
import simulator.model.RoundRobinStrategy;

public class RoundRobinStrategyBuilder extends Builder<LightSwitchingStrategy>{
	private static final String type = "round_robin_lss";
	
	public RoundRobinStrategyBuilder() {
		super(RoundRobinStrategyBuilder.type);
	}

	@Override
	protected LightSwitchingStrategy createTheInstance(JSONObject data) {
		if(data.has("timeslot"))
			return new RoundRobinStrategy(data.getInt("timeslot"));
		else
			return new RoundRobinStrategy(1);
	}
}
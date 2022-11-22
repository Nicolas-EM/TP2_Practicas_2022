package simulator.factories;

import org.json.JSONObject;
import simulator.model.LightSwitchingStrategy;
import simulator.model.MostCrowdedStrategy;

public class MostCrowdedStrategyBuilder  extends Builder<LightSwitchingStrategy>{
	private static final String type = "most_crowded_lss";
	
	public MostCrowdedStrategyBuilder() {
		super(MostCrowdedStrategyBuilder.type);
	}

	@Override
	protected LightSwitchingStrategy createTheInstance(JSONObject data) {
		if(data.has("timeslot"))
			return new MostCrowdedStrategy(data.getInt("timeslot"));
		else
			return new MostCrowdedStrategy(1);
	}
}
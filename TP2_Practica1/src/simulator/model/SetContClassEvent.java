package simulator.model;

import java.util.List;

import exceptionsUtils.ExceptionsUtils;
import simulator.misc.Pair;

public class SetContClassEvent extends Event{
	private List<Pair<String,Integer>> cs;	
	
	public SetContClassEvent(int time, List<Pair<String,Integer>> cs){
		super(time);
		
		if(cs != null)
			this.cs = cs;
	}

	@Override
	void execute(RoadMap map) {
		for(Pair<String, Integer> c : this.cs) {
			try {
				Vehicle v = map.getVehicle(c.getFirst());
				if(v == null)
					throw new Exception(String.format("Vehicle with specified ID %i %s", c.getFirst(), ExceptionsUtils.NULL_PARAMETER));
				else {
					v.setContClass(c.getSecond());
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
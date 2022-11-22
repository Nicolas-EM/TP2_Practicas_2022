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

	@Override
	public Object getPriority() {
		// TODO Auto-generated method stub
		return null;
	}
	public String toString() {
		String string = "Change CO2 class: [";
		for(int i = 0; i < cs.size(); i++) {
			Pair<String, Integer> p = cs.get(i);
			if(i != 0)
				string += ", ";
			string += String.format("(%s, %s)", p.getFirst(), p.getSecond());
		}
		string += "]";
		return string;
	}
}
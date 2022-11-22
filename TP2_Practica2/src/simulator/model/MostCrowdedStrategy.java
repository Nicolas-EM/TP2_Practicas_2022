package simulator.model;

import java.util.List;

public class MostCrowdedStrategy implements LightSwitchingStrategy {
	private int timeSlot;
	
	public MostCrowdedStrategy(int timeSlot){
		this.timeSlot = timeSlot;
	}

	@Override
	public int chooseNextGreen(List<Road> roads, List<List<Vehicle>> qs, int currGreen, int lastSwitchingTime, int currTime) {
		if(roads.isEmpty()) return -1;
		else if(currGreen == -1) return circularQueueIndex(qs, -1);
		else if(currTime - lastSwitchingTime < this.timeSlot) return currGreen;
		else return circularQueueIndex(qs, currGreen);
	}
	
	// searches for list with longest queue starting at currGreen, in a circular manner
	// returns index
	private int circularQueueIndex(List<List<Vehicle>> qs, int currGreen) {
		int max = (currGreen + 1) % qs.size();
		int x = max;
		for(int i = x; i < qs.size(); i++)
			if(qs.get(i).size() > qs.get(max).size()) max = i;
		for(int i = 0; i < x; i++)
			if(qs.get(i).size() > qs.get(max).size()) max = i;
		return max;
	}
}

package simulator.model;

import java.util.ArrayList;
import java.util.List;

public class MoveFirstStrategy implements DequeuingStrategy {

	// devuelve una lista que incluye el primer vehículo de q.
	@Override
	public List<Vehicle> dequeue(List<Vehicle> q) {
		List<Vehicle> temp = new ArrayList<Vehicle>();
		if(!q.isEmpty()) temp.add(q.get(0));
		return temp;
	}
}

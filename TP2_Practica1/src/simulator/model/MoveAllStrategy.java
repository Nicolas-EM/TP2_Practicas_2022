package simulator.model;

import java.util.ArrayList;
import java.util.List;

public class MoveAllStrategy implements DequeuingStrategy {

	// devuelve la lista de todos los vehículos que están en q (no devuelve q). 
	// El orden debe ser el mismo que cuando se itera q
	@Override
	public List<Vehicle> dequeue(List<Vehicle> q) {
		List<Vehicle> temp = new ArrayList<Vehicle>();
		for (Vehicle ve : q) {
			temp.add(ve);
		}
		return temp;
	}
}
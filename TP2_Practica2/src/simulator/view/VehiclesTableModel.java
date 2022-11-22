package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;
import simulator.model.Vehicle;
import simulator.model.VehicleStatus;

public class VehiclesTableModel extends AbstractTableModel implements TableModel, TrafficSimObserver {
	private static final long serialVersionUID = 1L;
	private List<Vehicle> _vehicles;

	public VehiclesTableModel(Controller ctrl) {
		this._vehicles = new ArrayList<>();
		ctrl.addObserver(this);
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		this._vehicles = map.getVehilces();
		fireTableDataChanged();
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		this._vehicles = map.getVehilces();
		fireTableDataChanged();
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		this._vehicles = new ArrayList<Vehicle>();
		fireTableDataChanged();
	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
		this._vehicles = map.getVehilces();
		fireTableDataChanged();
	}

	@Override
	public void onError(String err) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getRowCount() {
		return this._vehicles == null ? 0 : this._vehicles.size();
	}

	@Override
	public int getColumnCount() {
		return 8;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object s = null;

		switch (columnIndex) {
		case 0:
			s = _vehicles.get(rowIndex).getId();
			break;
		case 1:
			if(_vehicles.get(rowIndex).getStatus() != VehicleStatus.ARRIVED)
				s = _vehicles.get(rowIndex).getRoad().toString() + ":" + _vehicles.get(rowIndex).getLocation();
			else
				s = VehicleStatus.ARRIVED.toString();
			break;
		case 2:
			s = _vehicles.get(rowIndex).getItinerary();
			break;
		case 3:
			s = _vehicles.get(rowIndex).getContClass();
			break;
		case 4:
			s = _vehicles.get(rowIndex).getMaxSpeed();
			break;
		case 5:
			s = _vehicles.get(rowIndex).getSpeed();
			break;
		case 6:
			s = (int) _vehicles.get(rowIndex).getTotalCO2();
			break;
		default:
			s = _vehicles.get(rowIndex).getDistance();
			break;
		}
		return s;
	}

	@Override
	public String getColumnName(int col) {
		String s;
		switch (col) {
		case 0:
			s = "Id";
			break;
		case 1:
			s = "Location";
			break;
		case 2:
			s = "Itinerary";
			break;
		case 3:
			s = "CO2 Class";
			break;
		case 4:
			s = "Max.Speed";
			break;
		case 5:
			s = "Speed";
			break;
		case 6:
			s = "Total CO2";
			break;
		default:
			s = "Distance";
			break;
		}
		return s;
	}
}

package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.Junction;
import simulator.model.Road;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;

@SuppressWarnings("serial")
public class JunctionsTableModel extends AbstractTableModel implements TableModel, TrafficSimObserver {
	private String[] _colNames = { "Id", "Green", "Queues" };
	private List<Junction> _junctions;

	public JunctionsTableModel(Controller ctrl) {
		this._junctions = new ArrayList<>();
		ctrl.addObserver(this);
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		this._junctions = map.getJunctions();
		fireTableDataChanged();
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		this._junctions = map.getJunctions();
		fireTableDataChanged();
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		this._junctions = new ArrayList<>();
		fireTableDataChanged();

	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
		this._junctions = map.getJunctions();
		fireTableDataChanged();
	}

	@Override
	public void onError(String err) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getColumnName(int i) {
		return _colNames[i];
	}

	@Override
	public int getRowCount() {
		return _junctions == null ? 0 : _junctions.size();
	}

	@Override
	public int getColumnCount() {
		return _colNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String s = "";

		switch (columnIndex) {
		case 0:
			s = _junctions.get(rowIndex).getId();
			break;
		case 1:
			s = _junctions.get(rowIndex).getGreenLightIndex() == -1 ? "NONE"
					: _junctions.get(rowIndex).getInRoads().get(_junctions.get(rowIndex).getGreenLightIndex()).getId();
			break;
		case 2:
			for (Road r : _junctions.get(rowIndex).getInRoads()) {
				s = s + " " + r.getId() + ":" + r.getVehicles().toString();
			}
			break;
		}

		return s;
	}
}
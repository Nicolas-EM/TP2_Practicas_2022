package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.Road;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;

public class RoadsTableModel extends AbstractTableModel implements TableModel, TrafficSimObserver {
	private static final long serialVersionUID = 1L;
	private List<Road> _roads;
	
	public RoadsTableModel(Controller ctrl) {
		this._roads = new ArrayList<>();
		ctrl.addObserver(this);
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		this._roads = map.getRoads();
		fireTableDataChanged();

	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		this._roads = map.getRoads();
		fireTableDataChanged();
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		this._roads = map.getRoads();
		fireTableDataChanged();
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		this._roads= new ArrayList<Road>();
		fireTableDataChanged();
	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
		this._roads = map.getRoads();
		fireTableDataChanged();
	}

	@Override
	public void onError(String err) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getRowCount() {
		return this._roads == null ? 0 : this._roads.size();
	}

	@Override
	public int getColumnCount() {
		return 7;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object s = null;

		switch (columnIndex) {
		case 0:
			s = _roads.get(rowIndex).getId();
			break;
		case 1:
			s = _roads.get(rowIndex).getLength();
			break;
		case 2:
			s = _roads.get(rowIndex).getWeather();
			break;
		case 3:
			s = _roads.get(rowIndex).getMaxSpeed();
			break;
		case 4:
			s = _roads.get(rowIndex).getSpeedLimit();
			break;
		case 5:
			s = (int) _roads.get(rowIndex).getTotalCO2();
			break;
		case 6:
			s = (int) _roads.get(rowIndex).getContLimit();
			break;
		}

		return s;
	}

	@Override
	public String getColumnName(int col) {
		String s;
		  switch (col) {
          case 0:
              s= "Id";
              break;
          case 1:
        	  s=  "Length";
        	  break;
          case 2:
        	  s=  "Weather";
        	  break;
          case 3:
        	  s=  "Max.Speed";
        	  break;
          case 4:
        	  s=  "Speed Limit";
        	  break;
          case 5:
        	  s=  "Total CO2";
        	  break;
          default:
        	  s=  "CO2 Limit";
        	  break;
      }
		  return s;
	}

}

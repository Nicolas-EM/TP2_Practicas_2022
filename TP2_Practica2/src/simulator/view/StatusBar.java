package simulator.view;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;

public class StatusBar extends JPanel implements TrafficSimObserver {
	private static final long serialVersionUID = 1L;
	private JLabel _time;
	private JLabel _event;

	public StatusBar(Controller ctrl) {
		ctrl.addObserver(this);
		this.InitGUI();
	}

	private void InitGUI() {
		setBorder(BorderFactory.createBevelBorder(1));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this._time = new JLabel("Time: 0");
		this._event = new JLabel("Welcome!");
		setBorder(BorderFactory.createBevelBorder(1));
		
		this.add(this._time);
		this.add(new JSeparator(SwingUtilities.VERTICAL));
		this.add(this._event);
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		this._time.setText("Time: " + time);
		this._event.setText("");
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		this._time.setText("Time: " + time);
		this._event.setText("");
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		this._time.setText("Time: " + time);
		this._event.setText("Event added: " + e.toString());
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		this._time.setText("Time: " + time);
		this._event.setText("");
	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
	}

	@Override
	public void onError(String err) {
		// TODO Auto-generated method stub

	}
}

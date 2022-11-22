package simulator.view;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import simulator.model.Road;
import simulator.model.SimulatedObject;
import simulator.model.Weather;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

class ChangueWeatherDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private int _status;
	private JComboBox<String> _roadIds;
	private JComboBox<Weather> _weather;
	private List<Road> _roads;
	private JSpinner _timeSpinner;
	
	public ChangueWeatherDialog(Frame frame) {
		super(frame, true);
		initGUI();
	}

	private void initGUI() {

		_status = 0;

		setTitle("Changue Road Weather");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		JLabel helpMsg = new JLabel("Schedule an event to change the weather of a road after a given number of simulation ticks from now.");
		helpMsg.setAlignmentX(CENTER_ALIGNMENT);

		mainPanel.add(helpMsg);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		JPanel viewsPanel = new JPanel();
		viewsPanel.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(viewsPanel);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(buttonsPanel);

		this._roadIds = new JComboBox<>();
		_roadIds.setPreferredSize(new Dimension(100, 25));		
		_timeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
		_timeSpinner.setToolTipText("Time left until aply changes");
		_timeSpinner.setPreferredSize(new Dimension(80, 25));

		viewsPanel.add(this._roadIds);
		_weather = new JComboBox<Weather>();
		_weather.setPreferredSize(new Dimension(80, 25));
		_weather.addItem(Weather.SUNNY);
		_weather.addItem(Weather.WINDY);
		_weather.addItem(Weather.CLOUDY);
		_weather.addItem(Weather.RAINY);
		_weather.addItem(Weather.STORM);
		
		viewsPanel.add(new JLabel("Road: "));
		viewsPanel.add(_roadIds);
		viewsPanel.add(new JLabel("Weather: "));
		viewsPanel.add(_weather);
		viewsPanel.add(new JLabel("Ticks: "));
		viewsPanel.add(_timeSpinner);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_status = 0;
				ChangueWeatherDialog.this.setVisible(false);
			}
		});
		buttonsPanel.add(cancelButton);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (_roadIds.getSelectedItem() != null) {
					_status = 1;
					ChangueWeatherDialog.this.setVisible(false);
				}
			}
		});
		buttonsPanel.add(okButton);

		setPreferredSize(new Dimension(500, 200));
		pack();
		setResizable(false);
		setVisible(false);
	}

	public int open(List<Road> roads) {
		this._roads = roads;
		this._roadIds.removeAllItems();
		// update the comboxBox model -- if you always use the same no
		// need to update it, you can initialize it in the constructor.
		//
		for (Road r : roads) {
			this._roadIds.addItem(r.toString());
		}

		// You can change this to place the dialog in the middle of the parent window.
		// It can be done using using getParent().getWidth, this.getWidth(),
		// getParent().getHeight, and this.getHeight(), etc.
		//
		Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((pantalla.getWidth() - this.getWidth()) / 2);
		int y = (int) ((pantalla.getHeight() - this.getHeight()) / 2);
		this.setLocation(x, y);	

		setVisible(true);
		return _status;
	}

	public SimulatedObject getRoad() {
		for(Road r : _roads) {
			if(r.getId() == this._roadIds.getSelectedItem())
				return (SimulatedObject) r;
		}
		return null;
	}

	public Weather getWeather() {
		return (Weather) _weather.getSelectedItem();
	}

	public int getTicks() {
		return (int) this._timeSpinner.getValue();
	}

	public boolean cancelled() {
		return _status == 0;
	}
}
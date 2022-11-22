package simulator.view;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import simulator.misc.Pair;
import simulator.model.SimulatedObject;
import simulator.model.Vehicle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

class ChangeCO2ClassDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private int _status;
	private JComboBox<String> _vehicles;
	private JComboBox<Integer> _co2;
	private JSpinner _timeSpinner;
	
	public ChangeCO2ClassDialog(Frame frame) {
		super(frame, true);
		initGUI();
	}

	private void initGUI() {

		_status = 0;

		setTitle("Changue CO2 class");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		JLabel helpMsg = new JLabel("Schedule an event to change the CO2 of a vehicle after a given number of simulation ticks from now.");
		helpMsg.setAlignmentX(CENTER_ALIGNMENT);

		mainPanel.add(helpMsg);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		JPanel viewsPanel = new JPanel(new FlowLayout());
		viewsPanel.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(viewsPanel);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(buttonsPanel);

		this._vehicles = new JComboBox<>();
		this._vehicles.setPreferredSize(new Dimension(100, 25));		
		_timeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
		_timeSpinner.setToolTipText("Time left until aply changes");
		_timeSpinner.setPreferredSize(new Dimension(80, 25));
		this._co2 = new JComboBox<Integer>();
		this._co2.setPreferredSize(new Dimension(80, 25));
		for (int i = 0; i <= 10; i++) {
			this._co2.addItem(i);
		}
		
		viewsPanel.add(new JLabel("Vehicle: "));
		viewsPanel.add(_vehicles);
		viewsPanel.add(new JLabel("CO2 Class: "));
		viewsPanel.add(this._co2);
		viewsPanel.add(new JLabel("Ticks: "));
		viewsPanel.add(_timeSpinner);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_status = 0;
				ChangeCO2ClassDialog.this.setVisible(false);
			}
		});
		buttonsPanel.add(cancelButton);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (_vehicles.getSelectedItem() != null) {
					_status = 1;
					ChangeCO2ClassDialog.this.setVisible(false);
				}
			}
		});
		buttonsPanel.add(okButton);

		setPreferredSize(new Dimension(500, 200));
		pack();
		setResizable(false);
		setVisible(false);
	}

	public int open(List<Vehicle> vehicles) {
		this._vehicles.removeAllItems();		
		for (Vehicle v : vehicles) {
			this._vehicles.addItem(v.toString());
		}

		Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((pantalla.getWidth() - this.getWidth()) / 2);
		int y = (int) ((pantalla.getHeight() - this.getHeight()) / 2);
		this.setLocation(x, y);	

		setVisible(true);
		
		return _status;
	}

	public SimulatedObject getVehicle() {
		return (SimulatedObject) this._vehicles.getSelectedItem();
	}
	public Pair<String, Integer> getVehicleandClass() {
		return new Pair<String, Integer>((String) _vehicles.getSelectedItem(), (Integer)_co2.getSelectedItem());
	}
	public int getCo2() {
		return this._co2.getSelectedIndex();
	}

	public int getTicks() {
		return (int) this._timeSpinner.getValue();
	}

	public boolean cancelled() {
		return _status == 0;
	}
}

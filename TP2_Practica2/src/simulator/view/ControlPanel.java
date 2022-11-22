package simulator.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import simulator.control.Controller;
import simulator.misc.Pair;
import simulator.model.Event;
import simulator.model.RoadMap;
import simulator.model.SetContClassEvent;
import simulator.model.SetWeatherEvent;
import simulator.model.TrafficSimObserver;
import simulator.model.Weather;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel implements TrafficSimObserver {
	private boolean _stopped;
	private Controller _ctrl;
	private RoadMap _map;
	private int _time;
	private File fichero;
	private JButton loadB;
	private JFileChooser selector;
	private JButton co2B, weatherB, runB, stopB, restartB, exitB;
	private JSpinner tickB, delayB;
	private JToolBar toolbar;
	private ChangeCO2ClassDialog co2Dialog;
	private ChangueWeatherDialog weatherDialog;
	private volatile Thread _thread;

	public ControlPanel(Controller ctrl) {
		super();
		this._ctrl = ctrl;
		this.initGUI();
		ctrl.addObserver(this);
		this._time = (int) tickB.getValue();
	}

	private void initGUI() {
		this.setLayout(new BorderLayout());
		this.toolbar = new JToolBar();
		add(this.toolbar);
		this.LoadButton();
		this.toolbar.addSeparator();
		this.CO2Button();
		this.WeatherButton();
		this.toolbar.addSeparator();
		this.RunButton();
		this.StopButton();
		this.toolbar.addSeparator();
		this.TickButton();
		this.toolbar.addSeparator();
		this.DelayButton();
		this.toolbar.addSeparator();
		toolbar.add(Box.createHorizontalGlue());
		this.RestartButton();
		this.ExitButton();
	}

	private void LoadButton() {
		this.loadB = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().createImage("resources/icons/open.png")));
		this.loadB.setHorizontalAlignment(JLabel.LEFT);
		this.loadB.setToolTipText("Loads a file");
		
		this.selector = new JFileChooser();
		selector.setCurrentDirectory(new File("resources/examples/"));
		
		this.loadB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JFileChooser.APPROVE_OPTION == selector.showOpenDialog(selector)) {
					fichero = selector.getSelectedFile();
					if (fichero != null)
						loadFichero();
					else
						JOptionPane.showMessageDialog(selector, "Unable to find selected file");
				}
			}

		});
		this.toolbar.add(this.loadB);
	}

	private void loadFichero() {
		try {
			InputStream input = new FileInputStream(fichero);
			_ctrl.reset();
			tickB.setValue(10);
			try {
				_ctrl.loadEvents(input);
				setToolBarStatus(true);
			} catch (Exception e1) {
				onError("Unable to load events: " + e1.getMessage());
			}
			try {
				input.close();
			} catch (IOException e1) {
				onError("Unable to close file: " + e1.getMessage());
			}
		} catch (FileNotFoundException e2) {
			onError("Unable to load the file: " + e2.getMessage());
		}
	}
	
	private void WeatherButton() {
		this.weatherB = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().createImage("resources/icons/weather.png")));
		this.weatherB.setHorizontalAlignment(JLabel.LEFT);
		this.weatherB.setToolTipText("Change weather of a road");
		
		this.weatherB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				windowAncestorWeather();

				if (!weatherDialog.cancelled()) {
					List<Pair<String, Weather>> ws = new ArrayList<Pair<String, Weather>>();
					ws.add(new Pair<String, Weather>((weatherDialog.getRoad()).getId(), weatherDialog.getWeather()));
					_ctrl.addEvent(new SetWeatherEvent(weatherDialog.getTicks() + _time, ws));
				}
			}
		});
		
		this.weatherB.setEnabled(false);
		this.toolbar.add(this.weatherB);
	}

	private void windowAncestorWeather() {
		weatherDialog = new ChangueWeatherDialog((Frame) SwingUtilities.getWindowAncestor(this));
		weatherDialog.open(_map.getRoads());
	}

	private void CO2Button() {
		this.co2B = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().createImage("resources/icons/co2class.png")));
		this.co2B.setHorizontalAlignment(JLabel.LEFT);
		this.co2B.setToolTipText("Change CO2 Class of a vehicle");

		this.co2B.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				windowAncestorCo2();

				if (!co2Dialog.cancelled()) {
					List<Pair<String, Integer>> cs = new ArrayList<Pair<String, Integer>>();

					cs.add(co2Dialog.getVehicleandClass());
					_ctrl.addEvent(new SetContClassEvent(co2Dialog.getTicks() + _time, cs));
				}

			}
		});
		
		this.co2B.setEnabled(false);
		this.toolbar.add(this.co2B);
	}

	private void windowAncestorCo2() {
		co2Dialog = new ChangeCO2ClassDialog((Frame) SwingUtilities.getWindowAncestor(this));
		co2Dialog.open(_map.getVehilces());
	}

	private void RunButton() {
		this.runB = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().createImage("resources/icons/run.png")));
		this.runB.setHorizontalAlignment(JLabel.LEFT);
		this.runB.setToolTipText("Runs the simulator");

		this.runB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_stopped = false;
				setToolBarStatus(_stopped);
				_thread = new Thread() {
					public void run() {
						run_sim((int) tickB.getValue(), ((Number) delayB.getValue()).longValue());
						setToolBarStatus(true);
						_stopped = true;
					}
				};
				_thread.start();
			}
		});
		
		this.runB.setEnabled(false);
		this.toolbar.add(this.runB);
	}

	private void StopButton() {
		this.stopB = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().createImage("resources/icons/stop.png")));
		this.stopB.setHorizontalAlignment(JLabel.LEFT);
		this.stopB.setToolTipText("Stops simulator");
		this.stopB.setEnabled(false);
		
		this.stopB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_thread != null) {
					_thread.interrupt();
				}
			}
		});

		this.toolbar.add(this.stopB);
	}

	private void RestartButton() {
		this.restartB = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().createImage("resources/icons/restart.png")));
		this.restartB.setHorizontalAlignment(JLabel.RIGHT);
		this.restartB.setToolTipText("Restart simulation");
		
		this.restartB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFichero();
			}
		});

		this.restartB.setEnabled(false);
		this.toolbar.add(this.restartB);
	}
	
	private void ExitButton() {
		this.exitB = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().createImage("resources/icons/exit.png")));
		this.exitB.setHorizontalAlignment(JLabel.RIGHT);
		this.exitB.setToolTipText("Close simulator");
		this.exitB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int opt = JOptionPane.showConfirmDialog(null, "Are sure you want to quit?", "Quit",
						JOptionPane.YES_NO_OPTION);
				if (opt == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});

		this.toolbar.add(this.exitB);
	}

	private void TickButton() {
		this.tickB = new JSpinner(new SpinnerNumberModel(1, 0, 10000, 1));
		this.tickB.setValue(10);
		this.tickB.setMinimumSize(new Dimension(100, 20));
		this.tickB.setMaximumSize(new Dimension(200, 20));
		this.tickB.setPreferredSize(new Dimension(100, 20));
		this.tickB.setToolTipText("Changes time");
		this.toolbar.add(new JLabel("Ticks: "));
		this.toolbar.add(this.tickB);
	}

	private void DelayButton() {
		this.delayB = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
		this.delayB.setMinimumSize(new Dimension(100, 20));
		this.delayB.setMaximumSize(new Dimension(200, 20));
		this.delayB.setPreferredSize(new Dimension(100, 20));
		this.delayB.setToolTipText("Changes delay time");
		this.toolbar.add(new JLabel("Delay: "));
		this.toolbar.add(this.delayB);
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
		this._map = map;
		this._time = time;
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		this._map = map;
		this._time = time;
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		this._map = map;
		this._time = time;
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		this._map = map;
		this.tickB.setValue(time);
	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
		this._map = map;
		this._time = time;
	}

	@Override
	public void onError(String err) {
		System.out.println(err);
	}

	private void run_sim(int n, long delay) {
		while (n > 0 && !this._stopped) {
			try {
				setToolBarStatus(false);
				_ctrl.run(1);
			} catch (Exception e) {
				this._stopped = true;
				setToolBarStatus(true);
				return;
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				this._stopped = true;
				setToolBarStatus(true);
				return;
			}

			n--;
		}
	}

	private void setToolBarStatus(boolean enabled) {
		loadB.setEnabled(enabled);
		co2B.setEnabled(enabled);
		weatherB.setEnabled(enabled);
		runB.setEnabled(enabled);
		restartB.setEnabled(enabled);
		exitB.setEnabled(enabled);
		stopB.setEnabled(!enabled);
	}
}

package simulator.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import simulator.control.Controller;
import simulator.model.Event;
import simulator.model.Road;
import simulator.model.RoadMap;
import simulator.model.TrafficSimObserver;
import simulator.model.Vehicle;
import simulator.model.VehicleStatus;

public class MapByRoadComponent extends JComponent implements TrafficSimObserver {
	private RoadMap _map;
	private Image _car;
	private static final long serialVersionUID = 1L;

	private static final Color _BG_COLOR = Color.white;
	private static final Color _JUNCTION_COLOR = Color.blue;
	private static final Color _JUNCTION_LABEL_COLOR = new Color(200, 100, 0);
	private static final Color _GREEN_LIGHT_COLOR = Color.green;
	private static final Color _RED_LIGHT_COLOR = Color.red;
	private static final int _JRADIUS = 10;

	private int x1, x2;

	public MapByRoadComponent(Controller _ctrl) {
		initGUI();
		_ctrl.addObserver(this);
	}

	private void initGUI() {
		_car = loadImage("car_front.png");
		setPreferredSize(new Dimension(300, 200));
	}

	private Image loadImage(String img) {
		Image i = null;
		try {
			return ImageIO.read(new File("resources/icons/" + img));
		} catch (IOException e) {
		}
		return i;
	}

	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// clear with a background color
		g.setColor(_BG_COLOR);
		g.clearRect(0, 0, getWidth(), getHeight());

		if (_map == null || _map.getJunctions().size() == 0) {
			g.setColor(Color.red);
			g.drawString("No map yet!", getWidth() / 2 - 50, getHeight() / 2);
		} else {
			updatePrefferedSize();
			drawMap(g);
		}
	}

	private void drawMap(Graphics g) {
		this.x1 = 20;
		this.x2 = getWidth() - 105;

		drawRoadsID(g);

		this.x1 = 45;

		drawRoads(g);
		drawJunctions(g);
		drawVehicles(g);
		drawWeather(g);
		drawCo2(g);
	}

	private void drawRoads(Graphics g) {
		for (int j = 0; j < _map.getRoads().size(); j++) {
			int y = (j + 1) * 50;

			g.setColor(Color.black);
			g.drawLine(this.x1, y, this.x2, y);
		}
	}

	private void drawJunctions(Graphics g) {
		for (int j = 0; j < _map.getRoads().size(); j++) {
			int y = (j + 1) * 50;
			Road r = _map.getRoads().get(j);

			// Source
			g.setColor(_JUNCTION_COLOR);
			g.fillOval(x1 - _JRADIUS / 2, y - _JRADIUS / 2, _JRADIUS, _JRADIUS);

			g.setColor(_JUNCTION_LABEL_COLOR);
			g.drawString(r.getSrc().getId(), x1, y - 10);

			// Destination
			int idx = r.getDest().getGreenLightIndex();
			if (idx != -1 && r.equals(r.getDest().getInRoads().get(idx)))
				g.setColor(_GREEN_LIGHT_COLOR);
			else
				g.setColor(_RED_LIGHT_COLOR);
			g.fillOval(x2 - _JRADIUS / 2, y - _JRADIUS / 2, _JRADIUS, _JRADIUS);

			g.setColor(_JUNCTION_LABEL_COLOR);
			g.drawString(r.getDest().getId(), x2, y - 10);
		}
	}

	private void drawVehicles(Graphics g) {
		for (int j = 0; j < _map.getRoads().size(); j++) {
			int y = (j + 1) * 50;
			Road r = _map.getRoads().get(j);

			for (Vehicle v : _map.getVehilces()) {
				if (r.equals(v.getRoad()) && v.getStatus() != VehicleStatus.ARRIVED) {
					double f = ((float) v.getLocation()) / r.getLength();
					int vX = (int) (x1 + (x2 - x1) * f);

					int vLabelColor = (int) (25.0 * (10.0 - (double) v.getContClass()));
					g.setColor(new Color(0, vLabelColor, 0));

					g.drawImage(_car, vX, y - 6, 12, 12, this);
					g.drawString(v.getId(), vX, y - 6);
				}
			}
		}
	}

	private void drawRoadsID(Graphics g) {
		g.setColor(Color.BLACK);
		int i = 0;
		for (Road r : _map.getRoads()) {
			g.drawString(r.getId(), x1, ((i + 1) * 50) + 4);
			i++;
		}
	}

	private void drawWeather(Graphics g) {
		for (int j = 0; j < _map.getRoads().size(); j++) {
			int y = (j + 1) * 50;
			Road r = _map.getRoads().get(j);

			switch (r.getWeather()) {
			case SUNNY:
				g.drawImage(loadImage("sun.png"), x2 + 10, y - 32 / 2, 32, 32, this);
				break;
			case CLOUDY:
				g.drawImage(loadImage("cloud.png"), x2 + 10, y - 32 / 2, 32, 32, this);
				break;
			case WINDY:
				g.drawImage(loadImage("wind.png"), x2 + 10, y - 32 / 2, 32, 32, this);
				break;
			case RAINY:
				g.drawImage(loadImage("rain.png"), x2 + 10, y - 32 / 2, 32, 32, this);
				break;
			case STORM:
				g.drawImage(loadImage("storm.png"), x2 + 10, y - 32 / 2, 32, 32, this);
				break;
			}
		}
	}

	private void drawCo2(Graphics g) {
		for (int j = 0; j < _map.getRoads().size(); j++) {
			int y = (j + 1) * 50;
			Road r = _map.getRoads().get(j);

			int c = (int) Math.floor(Math.min((double) r.getTotalCO2() / (1.0 + (double) r.getContLimit()), 1.0) / 0.19);
			switch (c) {
			case 0:
				g.drawImage(loadImage("cont_0.png"), x2 + 50, y - 32 / 2, 32, 32, this);
				break;
			case 1:
				g.drawImage(loadImage("cont_1.png"), x2 + 50, y - 32 / 2, 32, 32, this);
				break;
			case 2:
				g.drawImage(loadImage("cont_2.png"), x2 + 50, y - 32 / 2, 32, 32, this);
				break;
			case 3:
				g.drawImage(loadImage("cont_3.png"), x2 + 50, y - 32 / 2, 32, 32, this);
				break;
			case 4:
				g.drawImage(loadImage("cont_4.png"), x2 + 50, y - 32 / 2, 32, 32, this);
				break;
			default:
				g.drawImage(loadImage("cont_5.png"), x2 + 50, y - 32 / 2, 32, 32, this);
				break;
			}
		}
	}

	private void updatePrefferedSize() {
		int maxH = 200;

		for (int i = 0; i < _map.getRoads().size(); i++)
			maxH += 37;

		if (maxH > getHeight()) {
			setPreferredSize(new Dimension(getWidth(), maxH));
			setSize(new Dimension(getWidth(), maxH));
		}
	}

	@Override
	public void onAdvanceStart(RoadMap map, List<Event> events, int time) {
	}

	@Override
	public void onAdvanceEnd(RoadMap map, List<Event> events, int time) {
		this._map = map;
		repaint();
	}

	@Override
	public void onEventAdded(RoadMap map, List<Event> events, Event e, int time) {
		this._map = map;
		repaint();
	}

	@Override
	public void onReset(RoadMap map, List<Event> events, int time) {
		this._map = map;
		setPreferredSize(new Dimension(getWidth(), 200));
		setSize(new Dimension(getWidth(), 200));
		repaint();
	}

	@Override
	public void onRegister(RoadMap map, List<Event> events, int time) {
		this._map = map;
		repaint();
	}

	@Override
	public void onError(String err) {
		// TODO Auto-generated method stub

	}

}

package simulator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import exceptionsUtils.ExceptionsUtils;

public class Junction extends SimulatedObject {
	private List<Road> inRoads; // lista de carreteras entrantes
	private Map<Junction, Road> outRoads; // lista de carreteras salientes
	private Map<Road, List<Vehicle>> inRoadToQueueList; //
	private List<List<Vehicle>> queueList; // la cola i-ésima corresponde a la i-ésima carretera en la lista de
											// carreteras entrantes
	private int currGreen; // índice del semáforo en verde
	private int lastSwitchingTime; // el paso en el cual el índice del semáforo en verde ha cambiado de valor
	private LightSwitchingStrategy lightStrategy; // Estrategia cambio semaforo
	private DequeuingStrategy dqStrategy; // Estrategia extraccion elementos de cola
	private int xCoor;
	private int yCoor;

	protected Junction(String id, LightSwitchingStrategy lsStrategy, DequeuingStrategy dqStrategy, int xCoor,
			int yCoor) {
		super(id);

		if (lsStrategy == null)
			throw new IllegalArgumentException(
					String.format("[ERROR]: %s %s", "Light Switching strategy", ExceptionsUtils.NULL_PARAMETER));
		else if (dqStrategy == null)
			throw new IllegalArgumentException(
					String.format("[ERROR]: %s %s", "Dequeuing strategy", ExceptionsUtils.NULL_PARAMETER));
		else if (xCoor < 0)
			throw new IllegalArgumentException(
					String.format("[ERROR]: %s %s", "Coordinate x", ExceptionsUtils.INVALID_NEGATIVE));
		else if (yCoor < 0)
			throw new IllegalArgumentException(
					String.format("[ERROR]: %s %s", "Coordinate y", ExceptionsUtils.INVALID_NEGATIVE));
		else {
			this.inRoads = new ArrayList<>();
			this.outRoads = new HashMap<>();

			this.inRoadToQueueList = new HashMap<>();
			this.queueList = new ArrayList<>();

			this.currGreen = -1;
			this.lastSwitchingTime = -1;

			this.lightStrategy = lsStrategy;
			this.dqStrategy = dqStrategy;

			this.xCoor = xCoor;
			this.yCoor = yCoor;
		}
	}

	public void addIncomingRoad(Road r) {
		if (!r.getDest().equals(this))
			throw new IllegalArgumentException("[Error]: Road destiniation does not match junction");
		else {
			this.inRoads.add(r);
			List<Vehicle> list = new LinkedList<Vehicle>();
			this.queueList.add(list);
			this.inRoadToQueueList.put(r, list);
		}
	}

	public void addOutGoingRoad(Road r) {
		if (this.outRoads.putIfAbsent(r.getDest(), r) != null)
			throw new IllegalArgumentException("[Error]: This road can not be added");
	}

	public void enter(Vehicle v) {
		this.inRoadToQueueList.get(v.getRoad()).add(v);
	}

	public Road roadTo(Junction j) {
		return this.outRoads.get(j);
	}

	@Override
	public void advance(int time) {
		// lista de vehiculos de current green que deben avanzar
		if(currGreen != -1) {
			List<Vehicle> advancingVehicles = this.dqStrategy.dequeue(this.queueList.get(currGreen));
			
			for (Vehicle ve : advancingVehicles) {
				try {
					ve.moveToNextRoad();
					this.queueList.get(currGreen).remove(ve);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}

		int nextGreen = lightStrategy.chooseNextGreen(this.inRoads, this.queueList, currGreen, this.lastSwitchingTime,
				time);
		if (nextGreen != this.currGreen) {
			this.currGreen = nextGreen;
			this.lastSwitchingTime = time;
		}
	}

	@Override
	public JSONObject report() {
		JSONObject jsonO = new JSONObject();

		jsonO.put("id", this.getId());
		if(this.currGreen == -1) jsonO.put("green","none");
		else jsonO.put("green", inRoads.get(this.currGreen).getId());
		jsonO.put("queues", this.queuesToJSON());

		return jsonO;
	}

	@Override
	public void updateSpeedLimit() {}

	@Override
	public int calculateVehicleSpeed(Vehicle v) { return 0;}

	@Override
	public void reduceTotalContamination() {}

	private JSONArray queuesToJSON() {
		JSONArray allQueues = new JSONArray();
		
		for(int i = 0; i < this.queueList.size(); i++) {
			JSONObject queue = new JSONObject();
			
			queue.put("road", this.inRoads.get(i).getId());
			
			JSONArray vehicleList = new JSONArray();
			for(int v = 0; v < this.queueList.get(i).size(); v++) {
				vehicleList.put(this.queueList.get(i).get(v).getId());
			}
			queue.put("vehicles", vehicleList);
			
			allQueues.put(queue);
		}
		
		return allQueues;
	}
}

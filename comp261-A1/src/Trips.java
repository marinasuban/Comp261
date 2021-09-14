import java.util.ArrayList;
import java.util.List;

public class Trips {
	private String tripID;
	public List<Stops> tripSequence = new ArrayList<Stops>(); // List of stop in order within trip
	public List<Connections> connectionList = new ArrayList<Connections>(); // List of all connections within trip

	public Trips(String tripID) {
		this.tripID = tripID;

	}

	public void addStop(Stops stop) {
		tripSequence.add(stop); //add each stop to list of stops for each trip

	}

	/** getters AND setters */
	
	public String getTripID() {
		return tripID;
	}

	public List<Stops> getTripSequence() {
		return tripSequence;
	}

	public List<Connections> getConnectionList() {
		return connectionList;
	}

	public void setConnections(ArrayList<Connections> newConnections) {
		connectionList = newConnections;
	}

}

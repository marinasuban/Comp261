
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.awt.Point;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Maps extends GUI {
	/** Variable list */
	public HashMap<String, Stops> allStops = new HashMap<String, Stops>();
	public Map<String, Trips> allTrips = new HashMap<String, Trips>();
	private QuadTreeNew tree;
	public static int SCALE = 10;
	public Location origin = new Location(-24, 19);
	public Trie trie = new Trie();
	private Point mousePressPosition;
	double ZOOM_FACTOR = 1.3;
	static boolean quadTreeOn = false;

	@Override
	protected void redraw(Graphics g) {
		Dimension d = getDrawingAreaDimension();
		tree = new QuadTreeNew(new Point(0, 0), d.width, d.height);

		// draw all stops
		for (Stops s : allStops.values()) {
			s.drawStop(g, origin, SCALE);

			// QUADTREE
			tree.insertStop(s);

		}
		if (quadTreeOn == true) {
			// QUADTREE
			tree.draw(g);
		}

		// draw all connections (from trips)
		for (Trips t : allTrips.values()) {
			for (Connections c : t.connectionList) {
				c.drawConnection(g, origin, SCALE);
			}
		}
	}

	@Override
	protected void onClick(MouseEvent e) {
		resetHighlights();
		Point p = e.getPoint();
		boolean highlight = true;

		// for each stop turn off highlight
		// if within region and first to be clicked highlight and print
		for (Stops s : allStops.values()) {
			if (s.getIsHigh()) {
				s.setHighlighted(false);
			}

			if (s.within(p) && highlight) {
				s.setHighlighted(true);
				printStop(s);
				highlight = false;
			}

		}

		// quad tree
		if (quadTreeOn == true) {
			Stops st = tree.findStop(p);

			if (st == null) {
				getTextOutputArea().setText("Cannot be found");
			}
			if (st != null) {
				for (Stops s : allStops.values()) {
					if (s.getID().equals(st.getID())) {
						s.setHighlighted(true);
						printStop(s);
						highlight = false;
					}
				}
			}
		}
	}

	private void printStop(Stops stop) {
		String stopName = stop.getName();
		HashMap<String, Connections> connections = stop.getConnection();
		HashMap<String, Integer> tripIDs = new HashMap<>();

		String stopSelected = "Name of stop selected:" + "\n" + stopName + "\n\n";
		stopSelected = stopSelected + "Trips connected to stop:" + "\n";

		// for each connection get trip id and add to print
		for (Connections connection : connections.values()) {
			String connectTrip = connection.getTrip().getTripID();

			if (tripIDs.get(connectTrip) == null) {
				stopSelected = stopSelected + connectTrip + " - ";
				tripIDs.put(connectTrip, 1);
				for (Connections c : allTrips.get(connectTrip).getConnectionList()) {
					c.setHighlighted(true);
				}

			}

		}

		getTextOutputArea().setText(stopSelected);

	}

	@Override
	protected void onSearch() {
		resetHighlights();
		if (allStops.isEmpty()) {
			return;
		}

		// find the stop from the search string
		String textOutput = "Search result:" + "\n";
		String searchText = getSearchBox().getText().toUpperCase();
		Stops searchResult = null;
		for (Stops s : allStops.values()) {

			if (s.getName().toUpperCase().equals(searchText)) {
				searchResult = s;
				s.setHighlighted(true);
				textOutput = textOutput + s.getName() + "\n";
			}
		}

		if (searchResult == null) {
			// break;
			textOutput = "no stops found";
		} else {
			textOutput += "trips found:\n";
			// look for trips that has this stop
			for (Trips t : allTrips.values()) {
				if (t.tripSequence.contains(searchResult)) {
					// highlight every connection
					textOutput += t.getTripID() + "\n";
					for (Connections c : t.getConnectionList()) {
						c.setHighlighted(true);
					}

				}
			}
		}

		getTextOutputArea().setText(textOutput);
	}

	@Override
	protected void onSearchTrie() {
		resetHighlights();
		if (allStops.isEmpty()) {
			return;
		}

		// find the stop from the search string
		String textOutput = "----Stops:----\n";
		String searchText = getSearchBox().getText().toUpperCase();

		if (searchText.isBlank())
			return;

		HashSet<Stops> stopsBySearch = trie.searchStopsByName(searchText);
		super.updateStopsInComboBox(stopsBySearch);

		if (stopsBySearch.isEmpty()) {
			getTextOutputArea().setText("");
			return;
		}

		for (Stops s : allStops.values()) {
			if (stopsBySearch.contains(s)) {
				s.setHighlighted(true);

				textOutput += s.getName() + " - ";
			}
		}

		textOutput += "\n----trips:-----\n";
		for (Trips t : allTrips.values()) {
			for (Stops s : stopsBySearch) {
				if (t.tripSequence.contains(s)) {
					// highlight every connection
					textOutput += t.getTripID() + " - ";
					for (Connections c : t.getConnectionList()) {
						c.setHighlighted(true);
					}
				}
			}
		}
		getTextOutputArea().setText(textOutput);
	}

	private void resetHighlights() {
		for (Stops s : allStops.values()) {
			s.setHighlighted(false);
		}

		for (Trips t : allTrips.values()) {
			for (Connections c : t.getConnectionList()) {
				c.setHighlighted(false); // reset
			}
		}
	}

	@Override
	protected void onMove(GUI.Move m) {
		double screenWidth = getDrawingAreaDimension().getWidth();
		double screenHeight = getDrawingAreaDimension().getHeight();

		Point tL = Location.newFromLatLon(0, 0).asPoint(origin, SCALE);
		Point tR = Location.newFromLatLon(screenWidth, 0).asPoint(origin, SCALE);
		Point bL = Location.newFromLatLon(0, screenHeight).asPoint(origin, SCALE);
		Point bR = Location.newFromLatLon(screenWidth, screenHeight).asPoint(origin, SCALE);

		tL.setLocation(0, 0);
		tR.setLocation(screenWidth, 0);
		bL.setLocation(0, screenHeight);
		bR.setLocation(screenWidth, screenHeight);

		double width = Location.newFromLatLon(screenWidth, 0).newFromPoint(tR, origin, SCALE).x
				- Location.newFromLatLon(0, 0).newFromPoint(tL, origin, SCALE).x;
		double height = Location.newFromLatLon(0, screenHeight).newFromPoint(bL, origin, SCALE).y
				- Location.newFromLatLon(0, 0).newFromPoint(tL, origin, SCALE).y;

		// TODO Auto-generated method stub
		switch (m) {
		case ZOOM_IN:
			SCALE *= ZOOM_FACTOR;
			width /= ZOOM_FACTOR;
			height /= ZOOM_FACTOR;
			Stops.STOP_SIZE+=1;
			origin = origin.moveBy((width - (width / ZOOM_FACTOR)) / 2, (height - (height / ZOOM_FACTOR)) / 2);
			break;
			
		case ZOOM_OUT:
			origin = origin.moveBy(-(width - (width / ZOOM_FACTOR)) / 2, -(height - (height / ZOOM_FACTOR)) / 2);
			SCALE /= ZOOM_FACTOR;
			width *= ZOOM_FACTOR;
			height *= ZOOM_FACTOR;
			Stops.STOP_SIZE-=1;
			break;

		case NORTH:
			origin = origin.moveBy(0, 1);
			break;
		case SOUTH:
			origin = origin.moveBy(0, -1);
			break;
		case EAST:
			origin = origin.moveBy(1, 0);
			break;
		case WEST:
			origin = origin.moveBy(-1, 0);
			break;
		}

	}

	// distance mouse wheel moved
	/**CANT MAKE IT WORK WITH ZOOM AFTER CENTRE INSTRUCTION*/
	protected void onMouseWheelMoved(MouseWheelEvent e) {
		//SCALE += -e.getWheelRotation();
			//Stops.STOP_SIZE += e.getWheelRotation()/2;
	}
	

	// position on click
	protected void onMousePressed(MouseEvent e) {
		mousePressPosition = e.getPoint();
	}

	protected void onMouseDragged(MouseEvent e) {
		Point newPosition = e.getPoint();

		// distance from original mouse pressed to current position
		double xDistance = mousePressPosition.x - newPosition.x;
		double yDistance = mousePressPosition.y - newPosition.y;
		origin = origin.moveBy(xDistance * 0.02, yDistance * -0.02);
		mousePressPosition = newPosition;

	}

	@Override
	protected void onLoad(File stopFile, File tripFile) {
		loadStopFile(stopFile);
		loadTripFile(tripFile);

	}

	@Override
	protected void onComboBoxSelect(String item) {
		if (item == null)
			return;

		resetHighlights();
		String textOutput = "Stop chosen from dropdown: ";

		Stops result = null;
		for (Stops s : allStops.values()) {
			if (item.equals(s.getName())) {
				s.setHighlighted(true);
				result = s;
			}
		}

		if (result != null) {
			textOutput += result.getName() + "\n\n";
			textOutput += "Trips through this stop: \n";
			for (Trips t : allTrips.values()) {

				if (t.getTripSequence().contains(result)) {
					textOutput += t.getTripID() + " - ";

					for (Connections c : t.getConnectionList()) {
						c.setHighlighted(true);
					}
				}
			}
		}

		getTextOutputArea().setText(textOutput);
	}

	/** Reads stop file, create stops, store in list */
	public void loadStopFile(File stopFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(stopFile));
			String[] currentLine;
			String temporary = null;

			String ID;
			String name;
			Double latitude;
			Double longitude;

			br.readLine(); // skip first line

			while ((temporary = br.readLine()) != null) {
				// split line by tab and process
				currentLine = temporary.split("\\t");
				ID = currentLine[0];
				name = currentLine[1];
				latitude = Double.parseDouble(currentLine[2]);
				longitude = Double.parseDouble(currentLine[3]);

				Stops stop = new Stops(ID, name, latitude, longitude);
				allStops.put(ID, stop);

				// add stop to trie via name
				trie.addStop(stop, name.toUpperCase());
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Reads trip file, create trip, create connections, store in list */
	public void loadTripFile(File tripFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(tripFile));
			String[] currentLine;
			String temporary = null;

			br.readLine(); // skip first line

			while ((temporary = br.readLine()) != null) {
				String tripID;
				ArrayList<Connections> connectList = new ArrayList<>();

				// break line by tab
				currentLine = temporary.split("\\t");

				// new trip object
				tripID = currentLine[0];
				Trips trip = new Trips(tripID);

				// add stop to trip, origin stop
				for (int i = 1; i < currentLine.length; i++) {
					trip.addStop(allStops.get(currentLine[i]));
					Stops stop2 = allStops.get(currentLine[i]);

					// connection created (2to3)
					// connection added to connectList
					// connection 2to3 added to origin stop
					if (i < currentLine.length - 1) {
						Stops stop3 = allStops.get(currentLine[i + 1]);
						Connections connect = new Connections(stop2, stop3, trip);
						connectList.add(connect);
						stop2.addconnect2to3(connect);
					}

					// connection added to connectList
					// connection 1to2 added to origin stop
					if (i > 1) {
						Stops stop1 = allStops.get(currentLine[i - 1]);

						Connections connection = new Connections(stop2, stop1, trip);
						connectList.add(connection);

						stop2.addconnect2to1(connection);

					}

					// after line processed trip added to allTrips
					// connectList added to trip
					allTrips.put(tripID, trip);
					trip.setConnections(connectList);

				}
			}
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Maps();
	}

}

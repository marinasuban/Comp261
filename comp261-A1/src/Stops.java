import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;

public class Stops {
	/** VARIABLES LIST*/
	private HashMap<String, Connections> connection2to1 = new HashMap<String, Connections>();
    private HashMap<String, Connections> connection2to3 = new HashMap<String, Connections>();
	private String ID;
	public String name;
	private double latitude;
	private double longitude;
	private Location stopLocation;
	private boolean isHighlighted = false;
	private Point posit;
	static int STOP_SIZE = 4;

	public Stops() {} //for QUADTREE
	
	//STOP object
	public Stops(String ID, String name, double latitude, double longitude) {
		this.ID = ID;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.stopLocation = Location.newFromLatLon(latitude,longitude);
		posit = new Point(0,0);
	}
	
	//Draw method
	public void drawStop(Graphics g, Location origin, double scale) {
		posit = stopLocation.asPoint(origin, scale);
		int xCoord = posit.x;
		int yCoord = posit.y;
		
		if (isHighlighted) g.setColor(Color.GREEN);
		else g.setColor(Color.black);
		
		g.fillOval(xCoord,yCoord,STOP_SIZE,STOP_SIZE);
	}
	
	//Method for QUADTREE
	public boolean within(Point p) {
		boolean withinX = p.x >= posit.x && p.x <= (posit.x + STOP_SIZE);
		boolean withinY = p.y >= posit.y && p.y <= (posit.y + STOP_SIZE);

		return withinX && withinY;
	}
    //origin to next destination
    public void addconnect2to3(Connections connect2to3) {
		String connectID = connect2to3.getTrip().getTripID() + "_" + connect2to3.getDepart().getID();
		connection2to3.put(connectID, connect2to3);
		}
    //origin to previous destination
    public void addconnect2to1(Connections connect2to1) {
		String connectID = connect2to1.getTrip().getTripID() + "_" + connect2to1.getArrive().getID();
		connection2to1.put(connectID, connect2to1);
		}
    
    /** GETTER AND SETTERS*/
    public String getID() {
		return ID;
	}

	public String getName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public Location getstopLocation() {
		return stopLocation;
	}

    public HashMap<String, Connections> getConnection() {
        HashMap<String, Connections> connectionList = new HashMap<String, Connections>();
        connectionList.putAll(connection2to3);
        connectionList.putAll(connection2to1);
        return connectionList;
    }
    public HashMap<String, Connections> getconnect2to3() {
		return connection2to3;
	}

	public HashMap<String, Connections> getconnect2to1() {
		return connection2to1;
	}
    
	public void setHighlighted(boolean high) {
		isHighlighted = high;
	}
    
    
    public boolean getIsHigh() {
    	return isHighlighted;
    }
  
    public Point getPosition() {
		return posit;
	}
  
    public int getSize() {
		return STOP_SIZE;
	}
  
}

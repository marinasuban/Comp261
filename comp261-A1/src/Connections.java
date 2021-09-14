import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Connections {
	/**Variable list*/
	private final Stops arrive;
	private final Stops depart;
	public Trips tripConnect;
	public boolean isHighlighted;

	//OBJECT
	public Connections(Stops arrive, Stops depart, Trips tripConnect) {
		this.arrive = arrive;
		this.depart = depart;
		this.tripConnect = tripConnect;
		this.isHighlighted = false;
	}
	
	//DRAW
	public void drawConnection(Graphics g, Location origin, int scale) {
		Point startPos = arrive.getstopLocation().asPoint(origin, scale);
		Point endPos = depart.getstopLocation().asPoint(origin, scale);
		
		if (isHighlighted) {g.setColor(Color.BLUE);}
		else {g.setColor(Color.BLACK);}
		
		g.drawLine(startPos.x+3, startPos.y+3, endPos.x+3, endPos.y+3);
	}

	/**GETTER AND SETTERS*/
	public void setHighlighted (boolean value) {
		this.isHighlighted = value;
	}
	public Trips getTrip() {
		return this.tripConnect;
	}
	
	public Stops getArrive() {
		return arrive;
	}

	public Stops getDepart() {
		return depart;
	}

}

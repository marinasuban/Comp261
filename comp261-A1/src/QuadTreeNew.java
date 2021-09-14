
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

public class QuadTreeNew {
	//VARIABLES AND LIST
	private QuadTreeNew topLeft,topRight,bottomLeft,bottomRight;
	private Point position; //this is the coordinate of the top left of the quadtree node on the GUI
	private int width;
	private int height;
	private final int capacity = 1; //list of 1 item works better than nulling an object... kept getting problems
	private boolean isDivided = false;
	private ArrayList<Stops> stops = new ArrayList<>(); //list of 1 item works better than nulling an object... kept getting problems

	public QuadTreeNew(Point pos, int width, int height) {
		position = pos;
		this.width = width;
		this.height = height;
	}
	
	public void draw(Graphics g) {
		drawTree(g);
	}
	
	public void drawTree(Graphics g) {
		g.drawRect(position.x, position.y, width, height);
		
		if (isDivided) {
			topLeft.drawTree(g);
			topRight.drawTree(g);	
			bottomLeft.drawTree(g);
			bottomRight.drawTree(g);
		}
	}
	
	public Stops findStop(Point p) {
		Stops result = null;
		
		if (!withinBounds(p)) return null;
		
		//if not divided return only stop within quadrant
		if (this.stops != null && !isDivided &&!this.stops.isEmpty()) {
			result = this.stops.get(0);
		}
		
		//if divided search each quadrant for stop
		if (this.stops == null && isDivided) {
			Stops tl = topLeft.findStop(p);
			Stops tr = topRight.findStop(p);
			Stops bl = bottomLeft.findStop(p);
			Stops br = bottomRight.findStop(p);
	
			if (tl != null) result = tl;
			if (tr != null) result = tr ;
			if (bl != null) result = bl;
			if (br != null) result = br;
		}
		
		return result;
		}
	
	
	public void insertStop(Stops stop) {
		if (!withinBounds(stop.getPosition())) {return;}
		
		if (this.stops != null && this.stops.size() < this.capacity) {
			this.stops.add(stop);
			return;
		}
		
		//if adding stop to undivided divide
		if (!this.isDivided) {
			this.divide();
			
		}
		//add to quadrant within boundary of quadsection
		topLeft.insertStop(stop);
		topRight.insertStop(stop);
		bottomLeft.insertStop(stop);
		bottomRight.insertStop(stop);
	}

	private void moveParentStopsToChild() {
		for (Stops s : stops) {
			topLeft.insertStop(s);
			topRight.insertStop(s);
			bottomLeft.insertStop(s);
			bottomRight.insertStop(s);
		}
		
		stops = null;
	}
	
	//divide quad tree and moves stop
	private void divide() {
		Point p = this.position;
		int childWidth = width/2;
		int childHeight = height/2;
		
		Point TLpos = new Point (p.x, p.y);
		Point TRpos = new Point (p.x + childWidth, p.y);
		Point BLpos = new Point (p.x, p.y + childHeight);
		Point BRpos = new Point (p.x + childWidth, p.y + childHeight);
		
		topLeft = new QuadTreeNew(TLpos, childWidth, childHeight);
		topRight = new QuadTreeNew(TRpos, childWidth, childHeight);
		bottomLeft = new QuadTreeNew(BLpos, childWidth, childHeight);
		bottomRight = new QuadTreeNew(BRpos, childWidth, childHeight);
		this.isDivided = true;
		
		moveParentStopsToChild();
	}

	//checked is clicked within boundary of quadtree box
	private boolean withinBounds(Point p) {
		boolean within = false;
		
		int left = position.x;
		int right = position.x + width;
		int top = position.y;
		int bottom = position.y + height;
		
		
		if ((left < p.x) && (p.x <= right) && (top < p.y) && (p.y <= bottom)) within = true;
		return within;
	}
	
}



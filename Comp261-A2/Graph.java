import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This represents the data structure storing all the roads, nodes, and
 * segments, as well as some information on which nodes and segments should be
 * highlighted.
 * 
 * @author tony
 */
public class Graph {
	// map node IDs to Nodes.
	Map<Integer, Node> nodes = new HashMap<>();
	// map road IDs to Roads.
	Map<Integer, Road> roads;
	// just some collection of Segments.
	Collection<Segment> segments;

	//origin and destination
	Node originNode;
	Node destinationNode;

	//highlight
	Node highlightedNode;
	Collection<Road> highlightedRoads = new HashSet<>();
	ArrayList<Segment> highlightedSegments = new ArrayList<>();

	// ASTAR
	ASTAR aStar = new ASTAR();
	ASTAR.HEURESTIC_FUNCTION filter = ASTAR.HEURESTIC_FUNCTION.DISTANCE;
	ArrayList<ASTAR.RESTRICTIONS> restrictions = new ArrayList<>();

	// articulation points
	private final ArticulationPoint AP;
	private ArrayList<Node> APnodes = new ArrayList<>();

	// GUI button & filters
	public static Boolean articulationState = false;
	public static Boolean onewayState = false;
	public static Boolean noBike = false;
	public static Boolean noCar = false;
	public static Boolean noPedestrian = false;
	public static Boolean preferTime = false;

	public Graph(File nodes, File roads, File segments, File polygons) {
		this.nodes = Parser.parseNodes(nodes, this);
		this.roads = Parser.parseRoads(roads, this);
		this.segments = Parser.parseSegments(segments, this);
		this.AP = new ArticulationPoint();
	}

	//colors based on filters applied
	private Color getAppropriateColor(Segment s) {
		if (onewayState && s.road.isOnewayRoad())
			return Color.MAGENTA;
		else if (noBike && s.road.notForBicy)
			return Color.ORANGE;
		else if (noCar && s.road.notForCar)
			return Color.GREEN;
		else if (noPedestrian && s.road.notForPede)
			return Color.YELLOW;
		else
			return Mapper.SEGMENT_COLOUR;
	}

	public void draw(Graphics g, Dimension screen, Location origin, double scale) {
		// a compatibility wart on swing is that it has to give out Graphics
		// objects, but Graphics2D objects are nicer to work with. Luckily
		// they're a subclass, and swing always gives them out anyway, so we can
		// just do this.
		Graphics2D g2 = (Graphics2D) g;

		// draw all the segments.
		for (Segment s : segments) {
			g2.setColor(getAppropriateColor(s));
			s.draw(g2, origin, scale);
		}

		// draw the segments of all highlighted roads.
		g2.setColor(Mapper.HIGHLIGHT_COLOUR);
		g2.setStroke(new BasicStroke(3));

		for (Road road : highlightedRoads) {
			for (Segment seg : road.components) {
				seg.draw(g2, origin, scale);
			}
		}

		for (Segment segment : highlightedSegments) {
			segment.draw(g2, origin, scale);
		}

		// draw all the nodes.
		g2.setColor(Mapper.NODE_COLOUR);
		for (Node n : nodes.values())
			n.draw(g2, screen, origin, scale);

		if (articulationState) {
			if (APnodes != null || !APnodes.isEmpty()) {
				g2.setColor(Color.pink);
				this.getAPs();
				for (Node aPoint : APnodes) {
					aPoint.draw(g2, screen, origin, scale);
				}
			}
		}
		
		// draw the highlighted node, if it exists.
		if (originNode != null) {
			g2.setColor(Color.GREEN);
			originNode.draw(g2, screen, origin, scale);
		}
		if (destinationNode != null) {
			g2.setColor(Color.RED);
			destinationNode.draw(g2, screen, origin, scale);
		}
	}

	//set highlighted nodes
	public void setHighlight(Node node) {
		this.highlightedNode = node;
	}

	//set highlighted road
	public void setHighlight(Collection<Road> roads) {
		this.highlightedRoads = roads;
	}

	//set origin node
	public void setOrigin(Node node) {
		this.clearHighlighted();
		this.originNode = node;
		this.setHighlight(new ArrayList<>());
	}

	//set destination node
	public void setDestination(Node node) {
		if (originNode == null) return;
		this.destinationNode = node;
		this.search();
	}

	
	//ASTAR path finding
	private void search() {
		ArrayList<Node> nodes = aStar.search(this.segments, originNode, destinationNode, filter, restrictions);

		highlightedSegments.clear();

		for (int i = 0; i < nodes.size() - 1; i++) {
			highlightSegment(nodes.get(i), nodes.get(i + 1));
		}
	}

	//highlight segment
	private void highlightSegment(Node start, Node end) {
		for (Segment segment : segments) {
			if ((segment.start == start && segment.end == end) || (segment.start == end && segment.end == start)) {
				highlightedSegments.add(segment);
				return;
			}
		}
	}

	//reset articulation points
	private void resetAPs() {
		AP.reset();
		APnodes.clear();

	}
	
	//get articulation points
	public void getAPs() {
		this.resetAPs();
		ArrayList<Node> nodeList = new ArrayList<>();
		for (Node node : nodes.values()) {
			nodeList.add(node);
		}
		this.APnodes = AP.getPoints(nodeList);
	}

	//removes highlight
	private void clearHighlighted() {
		originNode = null;
		destinationNode = null;
		highlightedSegments.clear();
	}
	
	//set heuristic based on user selection
	public void setFilter(ASTAR.HEURESTIC_FUNCTION f) {
		filter = f;
	}

	//add based on filter button clicked
	public void addRestriction(ASTAR.RESTRICTIONS restriction) {
		restrictions.add(restriction);
	}

	//remove based on filter button clicked
	public void removeRestriction(ASTAR.RESTRICTIONS restriction) {
		restrictions.remove(restriction);
	}
}

// code for COMP261 assignments
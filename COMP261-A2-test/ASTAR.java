import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class ASTAR {

	/**
	 * Heuristics used to determine best path Distance means that search will return
	 * shortest route Time means that search will return route which can be
	 * traversed in the shortest amount of time
	 * 
	 * @author marina.s
	 *
	 */
	public enum HEURESTIC_FUNCTION {
		DISTANCE, TIME
	}

	/**
	 * Restrictions selected by user No car removes path inaccessible by cars No
	 * bike removes path inaccessible by bike No pedestrian removes path
	 * inaccessible by pedestrian
	 * 
	 * @author marina.s
	 *
	 */
	public enum RESTRICTIONS {
		NO_CAR, NO_PEDESTRIAN, NO_BICYCLE
	}

	// variables list
	public static final int ROAD_CLASS_WEIGHT = 10;
	public static final int BEST_CASE_SPEED = 130;
	public static final int BEST_CASE_ROADCLASS = 4;

	/**
	 * method to search for best path
	 * https://www.baeldung.com/java-a-star-pathfinding?fbclid=IwAR0YdRu-K4AsAhbOIP0deff228sH0nP9iA83TsfHZgC7T5zwFiEE8y00sUg
	 * 
	 * @param segments
	 * @param start
	 * @param end
	 * @param filter
	 * @param restrictions
	 * @return
	 */
	public ArrayList<Node> search(Collection<Segment> segments, Node start, Node end, HEURESTIC_FUNCTION filter,
			List<RESTRICTIONS> restrictions) {
		// initial datasets
		PriorityQueue<AStarNode> fringe = new PriorityQueue<>();
		HashSet<AStarNode> visited = new HashSet<>();

		// initialize start node and add to fringe
		double Hdistance = distance(start, end);
		AStarNode startNode = new AStarNode(start, null, 0, Hdistance);
		fringe.add(startNode);

		while (!fringe.isEmpty()) {
			// get node on top of queue
			AStarNode current = fringe.poll();
			// mark visited so no looping
			visited.add(current);

			// found the end node -> return
			if (current.node == end) {
				return getPathFromEndNode(current);
			}

			// get neighbours of node on top of queue
			HashSet<AStarNode> neighbours = current.getNeighbours(filter);

			// for each neighbour get segment, if segment and node is not visited, not null,
			// accessible and is not restricted
			for (AStarNode n : neighbours) {
				Segment s = getSegment(segments, current.node, n.node);

				if (!isVisited(visited, n) && s != null && accessible(s, current.node, n.node)
						&& !isRestricted(s, restrictions)) {
					double startToNeighbour = n.routeCost; // current accurate total cost
					double neighbourToEnd; // estimated cost to end

					// if heuristic cost is distance cost of neighbour to end is the difference
					// between neighbour nood and end
					// if heuristic cost is time difference is divided by best case for time
					// (highest speed * (road class +1 (so multiplier isnt 0) * weight)
					if (filter == HEURESTIC_FUNCTION.DISTANCE) {
						neighbourToEnd = distance(n.node, end);
					} else {
						neighbourToEnd = distance(n.node, end)
								/ (BEST_CASE_SPEED * ((BEST_CASE_ROADCLASS + 1) * ROAD_CLASS_WEIGHT));
					}
					// neighbour heuristic cost is set to calculated cost above
					// neighbour added to fringe
					n.heuristicCost = startToNeighbour + neighbourToEnd;
					fringe.add(n);
				}
			}
		}
		return new ArrayList<Node>();
	}

	/**
	 * determines if node has been visited
	 * 
	 * @param visited
	 * @param node
	 * @return
	 */
	private boolean isVisited(HashSet<AStarNode> visited, AStarNode node) {
		for (AStarNode asnode : visited) {
			if (asnode.node == node.node) {
				return true;
			}
		}
		return false;
	}

	/**
	 * return A* path
	 * 
	 * @param endNode
	 * @return
	 */
	private ArrayList<Node> getPathFromEndNode(AStarNode endNode) {
		ArrayList<Node> path = new ArrayList<>();

		while (endNode != null) {
			path.add(0, endNode.node); // add to front of array because it will have to be in order
			endNode = endNode.prevNode;
		}

		if (path.isEmpty()) {
			System.out.println("error: A* is empty with endnode..."); // debug
		}
		return path;
	}

	/**
	 * check if segment is accessible using one way limitation
	 * 
	 * @param segment
	 * @param start
	 * @param end
	 * @return
	 */
	private boolean accessible(Segment segment, Node start, Node end) {
		if (segment == null)
			return false;
		if (segment.road.isOnewayRoad() && segment.start != start)
			return false;
		return true;
	}

	/**
	 * get specific segment determined by start and end node
	 * 
	 * @param segments
	 * @param start
	 * @param end
	 * @return
	 */
	private Segment getSegment(Collection<Segment> segments, Node start, Node end) {
		for (Segment s : segments) {
			if ((s.start == start && s.end == end) || (s.start == end && s.end == start)) { // found our segment
				return s;
			}
		}
		return null;
	}

	/**
	 * check if segment is accessible using filters applied by users
	 * 
	 * @param segment
	 * @param restrictions
	 * @return
	 */
	private boolean isRestricted(Segment segment, List<RESTRICTIONS> restrictions) {
		if (restrictions.contains(RESTRICTIONS.NO_CAR)) {
			return segment.road.notForCar;
		}
		if (restrictions.contains(RESTRICTIONS.NO_PEDESTRIAN)) {
			return segment.road.notForPede;
		}
		if (restrictions.contains(RESTRICTIONS.NO_BICYCLE)) {
			return segment.road.notForBicy;
		}
		return false;
	}

	/**
	 * returns distance between 2 nodes
	 * 
	 * @param A
	 * @param B
	 * @return
	 */
	private double distance(Node A, Node B) {
		return A.location.distance(B.location);
	}

	/**
	 * ASTAR OBJECT
	 * 
	 * @author marina.s
	 *
	 */
	private class AStarNode implements Comparable<AStarNode> {
		Node node;
		AStarNode prevNode;
		double routeCost;
		double heuristicCost;

		// creates ASTARNODE
		public AStarNode(Node node, AStarNode prev, double routeCost, double heuristicCost) {
			this.node = node;
			this.prevNode = prev;
			this.routeCost = routeCost;
			this.heuristicCost = heuristicCost;
		}

		// get neighbour of ASTAR
		public HashSet<AStarNode> getNeighbours(ASTAR.HEURESTIC_FUNCTION filter) {
			HashSet<AStarNode> neighbours = new HashSet<>();

			HashSet<Segment> segments = new HashSet<>(node.segments);
			for (Segment s : segments) {
				double totalCost;

				// total cost determine by heuristic preference
				if (filter == HEURESTIC_FUNCTION.DISTANCE) {
					totalCost = this.routeCost + s.length;
				} else {
					Road r = s.road;
					totalCost = this.routeCost + (s.length / (r.speed + (r.roadClass + 1) * ROAD_CLASS_WEIGHT));
				}

				// create neighbourNode object
				AStarNode neighbourNode = null;

				// Set neighbour object based on direction
				if (this.node == s.start) {
					neighbourNode = new AStarNode(s.end, this, totalCost, Double.MAX_VALUE);
				} else if (this.node == s.end) {
					neighbourNode = new AStarNode(s.start, this, totalCost, Double.MAX_VALUE);
				}

				// if neighbourNode has been set add to Neighbours list
				if (neighbourNode != null) {
					neighbours.add(neighbourNode);
				}
			}

			return neighbours;
		}

		// Priority queue based on heuristic cost
		@Override
		public int compareTo(AStarNode other) {
			if (this.heuristicCost > other.heuristicCost) {
				return 1;
			} else if (this.heuristicCost < other.heuristicCost) {
				return -1;
			}
			return 0;
		}

	}

}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class ArticulationPoint {
	private HashMap<Integer, artPoint> visited = new HashMap<>();

	public ArrayList<Node> getPoints(ArrayList<Node> nodes) {
		HashSet<artPoint> AP = new HashSet<>();
		while (true) {
			artPoint root = null;
			// look for unvisited node
			for (Node node : nodes) {
				// if node not visited add to visited, create new artPoint and set as root
				if (!visited.containsKey(node.nodeID)) {
					visited.put(node.nodeID, (root = new artPoint(node, 0)));
					break;
				}
			}

			// if root not null -> unvisited call find AP method
			// add result of method to AP
			if (root != null) {
				AP.addAll(findAP(root));
			}

			// root is visited
			else {
				break;
			}
		}
		return setsToLists(AP);
	}

	/**
	 * finds AP from root
	 * 
	 * @param root
	 * @return
	 */
	private HashSet<artPoint> findAP(artPoint root) {
		HashSet<artPoint> allAPs = new HashSet<>();
		Stack<artPoint> children = root.getChild();
		int subTree = 0;

		for (artPoint child : children) {
			// if child hasn't been visited (depth = POSITIVE_INFINITY as stated in artpoint
			// method)
			// set child artpoint information
			// call iterative method to look for AP which can be reached by child
			if (child.depth == Double.POSITIVE_INFINITY) {
				child.parent = root;
				child.depth = 1;
				iterateFindAP(child, allAPs);
				subTree++;

			}
			// if subtree is greater than one add root to AP set
			if (subTree > 1) {
				allAPs.add(root);
			}
		}
		// add root to visited
		visited.put(root.node.nodeID, root);
		return allAPs;
	}

	/**
	 * iterative method to find AP
	 * 
	 * @param firstPoint
	 * @param AP
	 */
	private void iterateFindAP(artPoint start, HashSet<artPoint> AP) {
		Stack<artPoint> fringe = new Stack<>();
		fringe.push(start);
		double depth = start.depth;

		while (!fringe.isEmpty()) {
			artPoint currentPoint = fringe.peek();

			// if unvisited, set information to current DFS graph
			if (!currentPoint.isVisited()) {
				currentPoint.depth = depth;
				currentPoint.reachBack = depth;
			}

			//has children -> process child APs ( branch node )
			if (!currentPoint.getChild().isEmpty()) {
				artPoint child = currentPoint.children.pop();
				if (child.isVisited()) {
					currentPoint.reachBack = Math.min(child.depth, currentPoint.reachBack);
				} else {
					depth++;
					child.depth = depth;
					child.reachBack = depth;
					child.parent = currentPoint;
					fringe.push(child);
				}
			}			
			//has no children -> is a leaf node, set parent as an AP
			else {
				if (currentPoint != start) {
					currentPoint.parent.reachBack = Math.min(currentPoint.parent.reachBack, currentPoint.reachBack);
					if (currentPoint.reachBack >= currentPoint.parent.depth) {
						AP.add(currentPoint.parent);
					}
				}
				fringe.pop();
			}
			visited.put(currentPoint.node.nodeID, currentPoint);
		}

	}

	// reset visited
	public void reset() {
		visited.clear();
	}

	// converts set of artPoints to arraylist of nodes
	private ArrayList<Node> setsToLists(HashSet<artPoint> set) {
		ArrayList<Node> nodes = new ArrayList<>();
		for (artPoint a : set) {
			nodes.add(a.node);
		}
		return nodes;
	}

	/**
	 * ArtPoint object
	 * 
	 * @author marina.s
	 *
	 */
	private class artPoint {
		final Node node;
		double depth;
		double reachBack;
		artPoint parent;
		private Stack<artPoint> children;

		// creates artpoint object
		public artPoint(Node node, double depth) {
			this.node = node;
			this.depth = depth;
			this.reachBack = depth;
		}

		// get child
		public Stack<artPoint> getChild() {
			// if children have been set
			if (this.children != null)
				return this.children;

			// otherwise for each segment in the artpoint node
			this.children = new Stack<artPoint>();
			for (Segment segment : node.segments) {
				Node childNode;
				artPoint point;

				// if the start of the segment is not the same as it artpoint node childnode is
				// set to start of segment else it is set to end of segment
				if (segment.start != this.node) {
					childNode = segment.start;
				} else {
					childNode = segment.end;
				}

				// if child has been visited point is set to child ID otherwise a new artpoint
				// is created with a depth of POSITIVE_INFINITY)
				if (visited.containsKey(childNode.nodeID)) {
					point = visited.get(childNode.nodeID);
				} else {
					point = new artPoint(childNode, Double.POSITIVE_INFINITY);
					visited.put(childNode.nodeID, point);
				}

				// if the child artpoint is not the same as the parent add child artpoint to
				// children list
				if (point != parent) {
					children.add(point);
				}
			}
			return children;
		}

		public boolean isVisited() {
			return this.depth < Double.POSITIVE_INFINITY;
		}

	}

}
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Trie {
	TrieNode root = new TrieNode(); // root

	private class TrieNode {
		char character;
		HashMap<Character, TrieNode> children = new HashMap<>();
		boolean isEndOfWord = false;
		List<Stops> stops = new ArrayList<>();

		public TrieNode() { // created for root to be initiated
		}

		public TrieNode(char character) { // create node with character
			this.character = character;
		}
	}
	
/** ADD STOP TO TRIE*/
	public void addStop(Stops stop, String stopName) {
		TrieNode node = this.root;
		// for each char in stop name
		for (int i = 0; i < stopName.length(); i++) {
			char c = stopName.charAt(i);

			// if there isn't a char that matches children add
			if (!node.children.containsKey(c)) {
				node.children.put(c, new TrieNode(c));
			}

			// move to the child to progress to next char
			node = node.children.get(c);
		}
//add stop to list of stop store in node and change end of word boolean to true for last node added
		node.stops.add(stop);
		node.isEndOfWord = true;
	}

/**SEARCH TRIE FOR STOP*/
	public HashSet<Stops> searchStopsByName(String searchText) {
		TrieNode node = this.root;
		HashSet<Stops> result = new HashSet<>();

//for each char in searchText
		for (int i = 0; i < searchText.length(); i++) {
			char c = searchText.charAt(i);

			// if node doesn't contain char it returns an empty set
			if (!node.children.containsKey(c)) {
				return new HashSet<Stops>();
			}
			// otherwise node equals child containing char
			node = node.children.get(c);

		}
//if node is end of word add all the stops to result
		if (node.isEndOfWord)
			result.addAll(node.stops);
//if child of node is not empty call method
		if (!node.children.isEmpty()) {
			searchChildrenForStops(node, result);
		}
//return result
		return result;
	}

/**SEARCH TRIE FOR STOP SUPPORTING METHOD*/
	private void searchChildrenForStops(TrieNode node, HashSet<Stops> result) {
		//if node is end of word add all the stops to result
		if (node.isEndOfWord)
			result.addAll(node.stops);
		//if empty return nothing
		if (node.children.isEmpty())
			return;
		//recursive method called until end of word found where results return when end of word found
		for (TrieNode child : node.children.values()) {
			searchChildrenForStops(child, result);
		}
	}
}

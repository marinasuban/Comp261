import java.util.*;
import java.io.*;

/**
 * A new instance of HuffmanCoding is created for every run. The constructor is
 * passed the full text to be encoded or decoded, so this is a good place to
 * construct the tree. You should store this tree in a field and then use it in
 * the encode and decode methods.
 */
public class HuffmanCoding {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Please call this program with two arguments, which are "
					+ "the input file name and either 0 for constructing tree and printing it, or "
					+ "1 for constructing the tree and encoding the file and printing it, or "
					+ "2 for constructing the tree, encoding the file, and then decoding it and "
					+ "printing the result which should be the same as the input file.");
		} else {
			try {
				Scanner s = new Scanner(new File(args[0]));

				// Read the entire file into one String.
				StringBuilder fileText = new StringBuilder();
				while (s.hasNextLine()) {
					fileText.append(s.nextLine() + "\n");
				}

				if (args[1].equals("0")) {
					System.out.println(constructTree(fileText.toString()));
				} else if (args[1].equals("1")) {
					constructTree(fileText.toString()); // initialises the tree field.
					System.out.println(encode(fileText.toString()));
				} else if (args[1].equals("2")) {
					constructTree(fileText.toString()); // initialises the tree field.
					String codedText = encode(fileText.toString());
					// DO NOT just change this code to simply print fileText.toString() back. ;-)
					System.out.println(decode(codedText));
				} else {
					System.out.println("Unknown second argument: should be 0 or 1 or 2");
				}
			} catch (FileNotFoundException e) {
				System.out.println("Unable to find file called " + args[0]);
			}
		}
	}
	// Change type from Object to HuffmanTree or appropriate type you design
	// TODO add a field with your ACTUAL HuffmanTree implementation.

	private static HuffmanNode tree; // rootNode Change type from Object to HuffmanTree or appropriate type you
										// design
	private static Map<HuffmanNode, String> nodeToStringMap = new HashMap<>(); // map of node and string of 0s and 1s
	private static Map<Character, String> dictionary = new HashMap<>(); // map of characters and binary string

	/**
	 * This would be a good place to compute and store the tree.
	 */
	public static Map<Character, String> constructTree(String text) {

		PriorityQueue<HuffmanNode> queueNode = constructingFrequencyQueue(text); // priority queue of node and its
																					// frequency

		while (queueNode.size() > 1) {
			HuffmanNode leftNode = queueNode.poll(); // first node in the queue
			HuffmanNode rightNode = queueNode.poll(); // second node in the queue

			int sumFreq = leftNode.getFreq() + rightNode.getFreq();
			char rightChar = rightNode.getCharacter().charAt(0);
			char leftChar = leftNode.getCharacter().charAt(0);
			HuffmanNode combinedNode = null;

			// while queue size is greater than one create new combinded node
			if (Character.compare(rightChar, leftChar) < 0) {
				combinedNode = new HuffmanNode(rightNode.getCharacter(), sumFreq, leftNode, rightNode, true);
			} else {
				combinedNode = new HuffmanNode(leftNode.getCharacter(), sumFreq, leftNode, rightNode, true);
			}
			// add combined node to queue
			queueNode.add(combinedNode);
		}
		// the last node in the queue will be the rootNode

		HuffmanNode rootNode = queueNode.poll();
		tree = rootNode;
		nodeToStringMap.put(rootNode, "");
		traverseTree(rootNode);
		dictionary = extractNode();
		return dictionary;
	}

	/**
	 * Take an input string, text, and encode it with the tree computed from the
	 * text. Should return the encoded text as a binary string, that is, a string
	 * containing only 1 and 0.
	 */
	public static String encode(String text) {
		// TODO fill this in.
		String encodedString = "";
		// System.out.println("length is: "+ text.length());
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			String binaryString = dictionary.get(c);
			encodedString = encodedString + binaryString;
		}
		// System.out.println("length encoded is: " + encodedString.length());
		return encodedString;
	}

	/**
	 * Take encoded input as a binary string, decode it using the stored tree, and
	 * return the decoded text as a text string.
	 */
	public static String decode(String encoded) {
		// TODO fill this in.
		String decodedString = "";
		HuffmanNode startNode = tree;
		for (int i = 0; i < encoded.length(); i++) {
			char binaryVal = encoded.charAt(i);

			if (binaryVal == '0') {
				HuffmanNode leftNode = startNode.leftChild();
				if (!leftNode.combined()) { // node is a node containing a char not a combined node
					startNode = tree; // reset back to rootNode
					decodedString = decodedString + leftNode.getCharacter();
				}

				else {
					startNode = leftNode; // setting startNode to leftNode for further traverse
				}
			}

			if (binaryVal == '1') {
				HuffmanNode rightNode = startNode.rightChild();
				if (!rightNode.combined()) { // node's character not null but a char
					startNode = tree; // reset back to rootNode
					decodedString = decodedString + rightNode.getCharacter();
				}
				else {
					startNode = rightNode; // setting startNode to rightNode for further traverse
				}
			}
		}
		return decodedString;
	}

	/**
	 * This method is to convert the text into a map of character and its frequency
	 * And then convert the map into a priorityQueue of HuffmanNodes.
	 */
	public static PriorityQueue<HuffmanNode> constructingFrequencyQueue(String text) {
		Map<Character, Integer> frequencyTable = new HashMap<>();

		// for each character in text add if not in table else increase frequency
		for (int i = 0; i < text.length(); i++) {
			Character c = text.charAt(i);
			if (!frequencyTable.keySet().contains(c)) {
				frequencyTable.put(c, 1);
			} else {
				int freq = frequencyTable.get(c);
				frequencyTable.put(c, freq + 1);
			}
		}

		PriorityQueue<HuffmanNode> queueNode = new PriorityQueue<>();
		// for each character in frequency table create node and add it to queue
		for (Character c : frequencyTable.keySet()) {
			String charac = "" + c;
			int freq = frequencyTable.get(c);
			HuffmanNode node = new HuffmanNode(charac, freq, null, null, false);
			queueNode.add(node);
		}

// TEST OUTPUT //    	
//    	//duplicate queue
//    	PriorityQueue<HuffmanNode> queueNode2 = new PriorityQueue<>();
//    	for(HuffmanNode node: queueNode) {
//    		queueNode2.add(node);
//    	}
//    	
//    	//for each node in queue print     	
//    	for(int i=0; i<frequencyTable.size(); i++) {
//    		HuffmanNode node = queueNode2.poll();
//    		System.out.println(node);
//   	}    	
		return queueNode;
	}

	public static void traverseTree(HuffmanNode node) {
		String text = nodeToStringMap.get(node);
		HuffmanNode leftChild = node.leftChild();
		HuffmanNode rightChild = node.rightChild();

		if (leftChild != null) {
			nodeToStringMap.put(leftChild, text + "0");
			traverseTree(leftChild);
		}

		if (rightChild != null) {
			nodeToStringMap.put(rightChild, text + "1");
			traverseTree(rightChild);
		}
	}

	public static HashMap<Character, String> extractNode() {
		HashMap<Character, String> charToStringMap = new HashMap<>();
		for (HuffmanNode node : nodeToStringMap.keySet()) {

			if (!node.combined()) {
				// if the character in the node not null then add char and the string to the map
				charToStringMap.put(node.getCharacter().charAt(0), nodeToStringMap.get(node));
			}

		}
		return charToStringMap;
	}
}

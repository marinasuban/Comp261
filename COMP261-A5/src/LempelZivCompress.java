import java.util.*;
import java.io.*;

public class LempelZivCompress {
	public static final int DEFAULT_WINDOW_SIZE = 100;

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Please call this program with one argument which is the input file name.");
		} else {
			try {
				Scanner s = new Scanner(new File(args[0]));

				// Read the entire file into one String.
				StringBuilder fileText = new StringBuilder();
				while (s.hasNextLine()) {
					fileText.append(s.nextLine() + "\n");
				}

				System.out.println(compress(fileText.toString()));
			} catch (FileNotFoundException e) {
				System.out.println("Unable to find file called " + args[0]);
			}
		}
	}

	/**
	 * Take uncompressed input as a text string, compress it, and return it as a
	 * text string.
	 */
	public static String compress(String input) {
		StringBuilder compressed = new StringBuilder();

		int cursor = 0;
		int windowSize = 100; // some suitable size
		int lab = 0; // look ahead buffer

		ArrayList<TupleNode> textCompressed = new ArrayList<>(); // we will compress the text into this array of tuple
																	// nodes

		while (cursor < input.length()) {
			lab = (cursor + lab >= input.length()) ? input.length() : cursor + lab;
			int sbStart = (cursor - windowSize < 0) ? 0 : cursor - windowSize;
			int matchLength = 1;
			int matchLocation = 0;
			String sb = (cursor == 0) ? "" : input.substring(sbStart, cursor);
			String next = input.substring(cursor, cursor + matchLength);
			char nextC = input.charAt(cursor);
			int offset = 0;
			if (sb.contains(next)) {
				for (int i = cursor - 1; i >= sbStart; i--) { // find the position of the very last occurance of this
					if (input.charAt(i) == nextC) {
						matchLocation = i;
						offset = cursor - matchLocation;
						break;
					}
				}
				int length = 1; // the length of the match we have found so far is 1, now we look further on
				for (int i = matchLocation + 1; i < cursor; i++) {
					if (cursor + length >= input.length()) {
						break;
					}
					char peek = input.charAt(cursor + length);
					// input char at i is the search buffer, peek is the look ahead
					if (input.charAt(i) == peek) { // if the next c at the search buffer is equal to the next c at the
													// look ahead buffer
						length++;
					} else {
						break; // if the pattern does not match anymore then we break out
					}
				}
				if (cursor + length < input.length()) {
					cursor = cursor + length;
				}
				textCompressed.add(new TupleNode(offset, length, input.charAt(cursor)));
			} else {
				char character = input.charAt(cursor);
				textCompressed.add(new TupleNode(0, 0, character)); // add an empty tuple as this was the first
																	// occurance of the word
			}
			cursor++;
		}

		// compressed.append("[");
		for (TupleNode tn : textCompressed) {
			compressed.append(tn.toString());
		}

		return compressed.toString();
	}
}


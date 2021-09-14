import java.util.*;
import java.io.*;

public class KMP {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Please call this program with " + "two arguments which is the input file name "
					+ "and the string to search.");
		} else {
			try {
				Scanner s = new Scanner(new File(args[0]));

				// Read the entire file into one String.
				StringBuilder fileText = new StringBuilder();
				while (s.hasNextLine()) {
					fileText.append(s.nextLine() + "\n");
				}

				System.out.println(search(fileText.toString(), args[1]));
			} catch (FileNotFoundException e) {
				System.out.println("Unable to find file called " + args[0]);
			}
		}
	}
	
	// testing
//	public static void main(String args[]) {
//		String txt = "ABABDABACDABABCABABABABDABACDABABCABABABABDABACDABABCABAB";
//		String pat = "ABABCABAB";
//		new KMP().search(txt, pat);
//	}

	/**
	 * Perform KMP substring search on the given text with the given pattern.
	 * 
	 * This should return the starting index of the first substring match if it
	 * exists, or -1 if it doesn't.
	 * @return 
	 */
	public static int search(String text, String pattern) {
		// TODO
		int patternLength = pattern.length();
		int textLength = text.length();
		int table[] = new int[patternLength]; // create table to hold longest pattern
		int indexP = 0; // index for pattern
		computeLPSArray(pattern, patternLength, table); // Preprocess the pattern
		int indexT = 0; // index for text
		while (indexT < textLength) {
			if (pattern.charAt(indexP) == text.charAt(indexT)) {
				indexT = indexT + 1;
				indexP = indexP + 1;
			}
			if (indexP == patternLength) {
				return (indexT - indexP);
//				System.out.println("Found pattern " + "at index " + (indexT - indexP));
//				indexP = table[indexP - 1];
			} else if (indexT < textLength && pattern.charAt(indexP) != text.charAt(indexT)) {
				// Do not match lps[0..lps[j-1]] characters,
				// they will match anyway
				if (indexP != 0){indexP = table[indexP - 1];}
				else { indexT = indexT + 1;}
			}
		}
		return -1;
	}

	public static void computeLPSArray(String pattern, int patternLength, int table[]) {
		// length of the previous longest prefix suffix
		int lengthPrev = 0; // length of the previous longest prefix suffix
		int i = 1;
		table[0] = 0;

		while (i < patternLength) {
			if (pattern.charAt(i) == pattern.charAt(lengthPrev)) {
				lengthPrev++;
				table[i] = lengthPrev;
				i=i+1;
			} else { // (pat[i] != pat[len])

				// This is tricky. Consider the example.
				// AAACAAAA and i = 7. The idea is similar
				// to search step.
				if (lengthPrev != 0) {
					lengthPrev = table[lengthPrev - 1];

					// Also, note that we do not increment
					// i here
				} else { //if (len == 0)

					table[i] = lengthPrev;
					i=i+1;
				}
			}
		}
	}
}



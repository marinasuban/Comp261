import java.util.*;
import java.io.*;

public class LempelZivDecompress {

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

                System.out.println(decompress(fileText.toString()));
            } catch (FileNotFoundException e) {
                System.out.println("Unable to find file called " + args[0]);
            }
        }
    }
    
    /**
     * Take compressed input as a text string, decompress it, and return it as a
     * text string.
     */
    public static String decompress(String compressed) {
		char compressedChar[] = compressed.toCharArray(); //an array of the compressed file which is seperated by [ ] 
		ArrayList<TupleNode> formatted = new ArrayList<TupleNode>(); //makng a list of tuple nodes we recieve from the compressed file				
		StringBuilder offsetHold = new StringBuilder(); //we convert to an int later
		StringBuilder lenHold = new StringBuilder(); //we convert to an int later
		//char charHold;
		int state = 0; //1 = we are looking at the offset, 2 we are looking at the length, 3 we are looking at the char
		for(char c : compressedChar) {
			if(c != '[' && c != ']' && c != '|') {
				if(state == 1) {
					offsetHold.append(c);
				}
				else if(state == 2) {					
					lenHold.append(c);
				}
				else if(state == 3) {					
					int off = Integer.parseInt(offsetHold.toString());
					int len = Integer.parseInt(lenHold.toString());
					
					TupleNode toAdd = new TupleNode(off, len, c);	
					formatted.add(toAdd);
					offsetHold = new StringBuilder(); //reset string builder
					lenHold = new StringBuilder(); //reset string builder					
					state = -1; //restart the states but before we see the ending brackert ]
				}
			}
			else {
				state++;
			}
		}
		
		StringBuilder sb = new StringBuilder();		

		ArrayList<Character> output = new ArrayList<Character>();		
		int cursor = 0;
		int formatNumber = 0;
		for(TupleNode tn : formatted) {
			if(tn.length == 0) {
				output.add(cursor++, tn.character);
			} //the length is only 0 if it is the first occurance of the word, so just print the letter
			else { //if ([offset, length, ch ])
				for (int j = 0; j< tn.length; j++) {
					output.add(output.get(cursor-tn.offset));
					cursor++;
				}
				if(formatNumber<formatted.size()-1) { //for some reason mine adds the last character iff the final character(s) are a patter.
					output.add(cursor++, tn.character);
				}				
			}
			formatNumber++;
		}	
		for(char c : output) {
			sb.append(c);
		}
		return sb.toString();	
    }
}

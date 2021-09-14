import java.util.*;
import java.io.*;

public class BM {
    private static final int MAXCHAR = 256;
    private static int[] badtable1;
    private static int[] goodtable1;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Please call this program with " +
                               "two arguments which is the input file name " +
                               "and the string to search.");
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

    /**
     * Perform BM substring search on the given text with the given pattern.
     * 
     * This should return the starting index of the first substring match if it
     * exists, or -1 if it doesn't.
     */
    public static int search(String text, String pattern) {
    	  if(pattern.length()==0 || text.length()==0) return -1;//empty nothing found
          char[] patternArray = pattern.toCharArray();
          char[] textArray = text.toCharArray();
          BadMatchTable(patternArray);
          goodSuffixes(patternArray);
          for (int i = patternArray.length - 1, j = i; i < textArray.length;  i += Math.max(goodtable1[patternArray.length - 1 - j], badtable1[textArray[i]])){//max is the maximum index to shift
              for (j = patternArray.length - 1; patternArray[j] == textArray[i]; i--, j--) {
                  //found
                  if (j == 0) {
                      return i;
                  }
              }
          }
          return -1;
      }
    
      public static int[] BadMatchTable(char[] pattern){// fill in the array of bad characters
          int[] badtable = new int[MAXCHAR];//set the array size one greater than 255
         for(int i=0; i < pattern.length;i++){
             badtable [pattern[i]] = pattern.length-1-i;
         }
         badtable1=badtable;
         return badtable;
      }
      public static int[] goodSuffixes(char[] pattern){
          int[] goodtable = new int[pattern.length];//
          // tracking the last index of prefix pattern
          int lastPrefix = pattern.length;

          for(int index = pattern.length; index>0 ; index--){//loops from the back
              if(checkPrefix(pattern,index)){
                  lastPrefix = index;//update the last prefix index
              }
             goodtable[pattern.length - index] = lastPrefix + pattern.length - index;
          }
          for (int i=0; i <pattern.length-1; i++){
              int suffixLength = 0;
              while(pattern[i-suffixLength] == pattern[pattern.length-1-suffixLength]){ suffixLength++;}//the maximum length of the substring ends at index and is a suffix.
              goodtable[suffixLength] = pattern.length-1 + i +suffixLength;
          }
          goodtable1=goodtable;
          return goodtable;
      }

      private static boolean checkPrefix(char[] pattern, int index) {
          for(int i=0;i<pattern.length-index;i++){
              if(pattern[i]!= pattern[i=index]) return false;//not match then return false
          }
          return true;
      }

}

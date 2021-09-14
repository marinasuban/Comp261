
public class HuffmanNode implements Comparable<HuffmanNode>{
	private String c;
	private int freq;
	private HuffmanNode child1;
	private HuffmanNode child2;
	private boolean combined;
	
	public HuffmanNode(String c, int freq, HuffmanNode child1, HuffmanNode child2, boolean combined) {
		this.c = c;
		this.freq = freq;
		this.child1 = child1;
		this.child2 = child2;
		this.combined = combined;
	}

	@Override
	public int compareTo(HuffmanNode other) {
		
		if(this.freq < other.freq) { // smaller frequence = higher priority
			return -1;
		}
		
		else if(this.freq > other.freq) { // higher frequence = lower priority
			return 1;
		}
		
		else {
			if(Character.compare(this.c.charAt(0), other.c.charAt(0))<0) {
				return -1;
			}
			
			else {
				return 1;
			}
			//return Character.compare(this.c.charAt(0), other.c.charAt(0));			
		}		
	}
	
	public String getCharacter() {
		return c;
	}
	
	public int getFreq() {
		return freq;
	}
	
	public HuffmanNode leftChild() {
		return child1;
	}
	
	public HuffmanNode rightChild() {
		return child2;
	}
	
	public boolean combined() {
		return combined;
	}
	
	public String toString() {
		return "character: " + c + " appears: " + freq + " times.";
	}
	
	
}



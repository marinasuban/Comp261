
public class TupleNode {
	int offset;
	int length;
	char character;
	public TupleNode(int off, int len, char c) {
		this.offset = off;
		this.length = len;
		this.character = c;
	}


@Override
public String toString() {
	return "[" + offset + "|" + length + "|" + character + "]";		
}
}
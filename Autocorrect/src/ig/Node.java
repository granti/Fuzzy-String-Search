package ig;

public class Node {
	protected char letter;
	protected boolean wordEnd;
	protected Node child;
	protected Node sibling;
	public Node(char letter){
		this.letter = letter;
		wordEnd = false;
	}
}
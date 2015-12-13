package ig;

public class Node {
	char letter;
	boolean wordEnd;
	Node child;
	Node sibling;
	public Node(char letter){
		this.letter = letter;
		wordEnd = false;
	}
}
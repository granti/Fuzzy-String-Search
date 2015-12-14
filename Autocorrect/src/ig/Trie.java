package ig;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Trie to store and search a dictionary.
 * Testing some thing.
 * @author Ian Grant
 *
 */
public class Trie {
	private Node root;
	private ArrayList<String> fuzzyResults;
	private int resultCount;
	public static void main(String[] args){
		Trie dict = new Trie("/usr/share/dict/words");
		Scanner scan = new Scanner(System.in);
		String term = scan.nextLine();
		while(term!="q"){
			System.out.println(dict.inDict(term));
			if(!dict.inDict(term)){
				int j = 1;
				int words =0;
				String[] res = new String[32];
				while(words<1){
					words = dict.fuzzySearch(term, j, res);
					j++;
				}
				for(int i=0; i<words; i++)
					System.out.println(res[i]);
			}
			term = scan.nextLine();
		}
		scan.close();
	}
	/**
	 * Constructor for Trie.
	 * @param file Path of dictionary text file.
	 */
	public Trie(String file){
		fuzzyResults = new ArrayList<String>();
		resultCount = 0;
		root = new Node('A');
		Node current = root;
		File source = new File(file);
		try {
			Scanner scan = new Scanner(source);
			while(scan.hasNextLine()){
				String line = scan.nextLine();
				current = root;
				for(int i=0; i<line.length(); i++){				//For each letter in the word
					while(current.letter != line.charAt(i)){	//As long as the letter doesn't match check the next node.
						if(current.sibling == null){					//If there is no next node, make one with the desired letter.
							current.sibling = new Node(line.charAt(i));
						}
						current = current.sibling;						//Check the next node.
					}
					if(current.child != null){							//By now the node and letter match so move to the next generation
						current = current.child;
					}
					while(current.child == null && i<line.length()-1){
						i++;
						current.child = new Node(line.charAt(i));
						current = current.child;						//Once the current child is null add the rest of the word quickly.
					}
				}
				current.wordEnd = true;
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Checks if a string is in the Trie.
	 * @param term String to search for.
	 * @return String is in the dictionary.
	 */
	public boolean inDict(String term){
		Node current = root;
		Node previous = root.child;
		for(int i=0;i<term.length();i++){
			while(term.charAt(i)!=current.letter){
				if(current.sibling == null){
					return false;
				}	
				current = current.sibling;
			}
			if(current.child == null && i<term.length()-1){
				return false;
			}
			previous = current;
			current = current.child;
		}
		return previous.wordEnd;
	}
	/**
	 * Searches for strings within a maximum Levenshtein Distance of the search term.
	 * @param term String to search for close matches in the dictionary.
	 * @param maxDist Maximum Levenshtein Distance to return results within.
	 * @param result Array to store results in.
	 * @return Number of results returned.
	 */
	public int fuzzySearch(String term, int maxDist, String[] result){
		fuzzyResults.clear();
		resultCount = 0;
		int[] init = new int[term.length()+1];
		for(int i=0; i<init.length; i++){
			init[i] = i;
		}
		recursiveFuzz(term, "", root,init, maxDist);
		result = fuzzyResults.toArray(result);
		fuzzyResults.clear();
		return resultCount;
	}
	private void recursiveFuzz(String term, String word, Node current, int[] lastRow, int maxDist){
		int[] thisRow = new int[lastRow.length];
		thisRow[0] = lastRow[0] + 1;
		for(int i=1; i<thisRow.length; i++){
			if(current.letter==term.charAt(i-1))
				thisRow[i] = lastRow[i-1];
			else
				thisRow[i] = Math.min(lastRow[i-1], Math.min(lastRow[i], thisRow[i-1]))+1;
		}
		if(current.sibling!=null)
			recursiveFuzz(term, word,current.sibling,lastRow, maxDist);
		word = word+current.letter;
		if(current.child!=null)
			recursiveFuzz(term, word,current.child,thisRow, maxDist);
		if(current.wordEnd && thisRow[thisRow.length-1]<=maxDist){
			resultCount++;
			fuzzyResults.add(word);
		}
	}
}

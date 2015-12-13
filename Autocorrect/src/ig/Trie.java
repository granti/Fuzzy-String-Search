package ig;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Trie {
	Node root;
	ArrayList<String> fuzzyResults;
	int resultCount;
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
				for(int i=0; i<line.length(); i++){
					while(current.letter != line.charAt(i)){
						if(current.sibling == null){
							current.sibling = new Node(line.charAt(i));
						}else{
							current = current.sibling;
						}
					}
					if(current.child != null){
						current = current.child;
					}
					while(current.child == null && i<line.length()-1){
						i++;
						current.child = new Node(line.charAt(i));
						current = current.child;
					}
					current.wordEnd = true;
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Checks if a word is in the dictionary
	 * 
	 **/
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
package ig;

import java.util.Scanner;

public class Levenshtein {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter two strings:");
		String a = scan.nextLine();
		String b = scan.nextLine();
		scan.close();
		System.out.println(distance(a,b));
	}
	
	public static int distance(String one, String two){
		one = one.toLowerCase();
		two = two.toLowerCase();
		int len1=one.length(), len2=two.length();
		int[][] dists = new int[len1+1][len2+1]; 
		for(int i=0; i<=len2; i++)
			dists[0][i] = i;
		for(int j=0; j<=len2; j++)
			dists[j][0] = j;
		for(int i=1; i<=len2; i++){
			for(int j=1; j<=len2; j++){
				if(one.charAt(j-1)==two.charAt(i-1))
					dists[i][j] = dists[i-1][j-1];
				else{
					dists[i][j] = Math.min(dists[i-1][j], Math.min(dists[i][j-1], dists[i-1][j-1]))+1;
				}
			}
		}
		return dists[len1][len2];
	}

}

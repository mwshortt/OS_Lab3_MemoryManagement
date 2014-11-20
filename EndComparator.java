import java.util.Comparator;

/**
 * Custom comparator to sort based on the end of the hole (the first entry plus second entry of the array)
 * @author mshortt
 *
 */
public class EndComparator implements Comparator<int[]>{

	/**
	 * Compares the second entry in the array (the size entry)
	 */
	public int compare(int[] first, int[] second) {
		int fEnd = first[0] + first[1];
		int sEnd = second[0] + second[1];
		if(fEnd > sEnd)
			return 1;
		else if(fEnd < sEnd)
			return -1;
		else //(first[1] == second[1])
			return 0;
	}

}


import java.util.Comparator;

/**
 * Custom comparator to sort based on the size of the hole (the second entry of the array)
 * @author mshortt
 *
 */
public class SizeComparator implements Comparator<int[]>{

	/**
	 * Compares the second entry in the array (the size entry)
	 */
	public int compare(int[] first, int[] second) {
		if(first[1] > second[1])
			return 1;
		else if(first[1] < second[1])
			return -1;
		else //(first[1] == second[1])
			return 0;
	}

}

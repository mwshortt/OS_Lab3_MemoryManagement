import java.util.Comparator;

/**
 * Custom comparator to sort based on the start of the hole (the first entry of the array)
 * @author mshortt
 *
 */
public class StartComparator implements Comparator<int[]>{

	/**
	 * Compares the first entry in the array (the start entry)
	 */
	public int compare(int[] first, int[] second) {
		if(first[0] > second[0])
			return 1;
		else if(first[0] < second[0])
			return -1;
		else //(first[1] == second[1])
			return 0;
	}

}


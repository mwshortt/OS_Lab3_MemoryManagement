import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class is the main class for the Memory Management Lab
 * This class takes in the input file and reads the first line, 
 * it then starts the appropriate memory management strategy 
 * (either segmentation or paging) passing the file to that 
 * appropriate class 
 * 
 * @author mshortt and ssethi
 *
 */
public class MemoryManagement {

	/**
	 * The main method for the Memory Management class
	 * This method takes in the input file and passes it to the 
	 * appropriate memory management strategy class
	 * @param args - the filename 
	 */
	public static void main(String[] args) {
		//check to make sure that the appropriate number of parameters were passed in
		if(args.length != 1)
		{
			System.out.println("Please pass the filename as a parameter.");
			System.exit(0);
		}
		//variable to hold the name of the file that was passed in
		String filename = args[0];
		//variable to hold the Java scanner for the text file  
		Scanner input = null;
		
		//try to create a scanner for the text file that was passed in 
		try
	    {
	      input = new Scanner(new File(filename));
	    }
		//if there was no such file found throw an exception
	    catch(FileNotFoundException s)
	    {
	      System.out.println("The file you passed does not exist.");
	      System.exit(0);
	    }
		
		//now read the file: 
		//the first line should have the memory size and the policy 
		//if the policy is 0, we will use segmentation
		//if the policy is 1, we will use paging
		
		//the memory size of the system from the input file
		int memorySize = input.nextInt();
		//the variable that tells us which policy that the system will use
		int whichPolicy = input.nextInt();
		
		System.out.println("memorySize: " + memorySize + " whichPolicy: " + whichPolicy);
		
		//the variable that holds which memory management policy the system is using
		MemoryPolicy policy = null;
		
		//if the policy number is 0, choose segmentation for the memory management policy
		if(whichPolicy == 0)
		{
			policy = new Segmentation(memorySize);
		}
		//if the policy number is 1, choose paging for the memory management policy
		else if(whichPolicy == 1)
		{
			policy = new Paging(memorySize);
		}
		//if the policy number is not 0 or 1, quit the program 
		else
		{
			System.out.println("The file does not contain a valid policy.");
		    System.exit(0);
		}
		
		String firstChar;
		//start reading the file! 
		//System.out.println("Starting to read file. What is its next? " + input.next());
		while(input.hasNext())
		{
			firstChar = input.next();
			if(firstChar.equals("A"))
			{
				policy.allocate(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt());
			}
			else if(firstChar.equals("D"))
			{
				policy.deallocate(input.nextInt());
			}
			else if(firstChar.equals("P"))
			{
				policy.printMemoryState();
			}
			else
			{
				System.out.println("File contains an invalid start of line character.");
				System.exit(0);
			}
			
		}
		
	}

}

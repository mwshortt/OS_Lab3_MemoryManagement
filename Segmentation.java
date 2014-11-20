import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

/**
 * This class implements the Segmentation memory policy
 * We assume that processes do not grow or shrink, no compaction is performed by the 
 * memory manager, and the paging scheme assumes that all of process’s pages are resident in the main 
 * memory.
 * 
 * Write a segmentation based allocator which allocates three segments for each 
process: text, stack, and heap. The memory region within each segment must be contiguous, but the 
three segments do not need to be placed contiguously. 
Instead, you should use either a best fit, first fit, 
or worst fit memory allocation policy to find a free region for each segment. The policy choice is yours, 
but you must explain why you picked that policy in your report. 
When a segment is allocated within a 
hole, if the remaining space is less than 16 bytes then the segment should be allocated the full hole. This 
will cause some internal fragmentation but prevents the memory allocator from having to track very 
small holes

 * @author mshortt and ssethi
 *
 */
public class Segmentation implements MemoryPolicy{
	//The amount of memory in the current system
	private int mSize;
	
	//The variable that keeps track of the current processes in the system
	//the key is the process id
	//the value is the int[]:
	//[0] = start of the text segment
	//[1] = size of the text segment
	//[2] = start of the data segment
	//[3] = size of the data segment
	//[4] = start of the heap segment
	//[5] = size of the heap segment
	//[6] = amount of internal fragmentation in the process (across all three segments)
	//[7] = size of the process
	private Map<Integer, int[]> processes = new HashMap<Integer, int[]>();
	
	//The helper variable for creating the value array for processes
	private int[] pHelper;
	
	//The variable that holds the free memory holes
	//ordered by size of the blocks
	//the array int[]:
	//[0] = start of the hole
	//[1] = size of the hole
	private TreeSet<int[]> freeBlocks;
	
	//The variable that holds the free memory holes, used while determining if there is enough memory for a process
	private TreeSet<int[]> freeBlocksTEST;
	
	//The current array, helper for adding holes to freeBlocks
	private int[] hole = new int[2];
	
	//The number of process that fail due to external fragmentation
	private int failExtF;
	
	//The number of process that fail because there's not enough storage
	private int fail;
	
	//The overall space in the system
	private int freeSpace;
	
	//The overall space in the system, used while determining if there is enough memory for a process
	private int freeSpaceTEST;
	
	/**
	 * Constructor for Segmentation
	 * Initializes the memory in the system, the free space, and the free blocks
	 * @param memSize - the size of the memory in the system
	 */
	public Segmentation(int memSize)
	{
		//initialize memory size
		mSize = memSize;
		
		//initialize free space
		freeSpace = memSize;
		
		//initialize freeBlocks, the free memory holes in the system
		freeBlocks = new TreeSet<int[]>(new SizeComparator());
		
		//create a hole for the initial memory size
		//the start of the hole is 0
		hole[0] = 0;
		//the size of the whole is the size of the memory
		hole[1] = mSize;
		
		//add the hole to the data structure that holds all the free memory blocks
		freeBlocks.add(hole);
	}

	 // allocate this many bytes to the process with this id
	 // assume that each pid is unique to a process
	 // text_size, data_size, and heap_size
	 // are the size of each segment. Verify that:
	 // text_size + data_size + heap_size = bytes
	// return 1 if successful
	 // return -1 if unsuccessful; print an error indicating
	 // whether there wasn't sufficient memory or whether 
	 // you ran into external fragmentation

	/**
	 * Allocates memory to a process
	 * First checking to make sure that there is enough memory for all the segments in the process
	 */
	public int allocate(int bytes, int pid, int text_size, int data_size,
			int heap_size) {
		//make sure that text_size + data_size + heap_size = bytes, quit if not
		if(text_size + data_size + heap_size != bytes)
		{
			System.out.print("Something wrong with the input file: text_size + data_size + heap_size does not equal bytes!");
			return -1;
		}
		
		//I AM USING THE FIRST FIT ALGORITHM TO FIND A FREE HOLE
		
		//initialize the helper array
		pHelper = new int[8];
		//clone freeBlocks to freeBlocksTEST so that we aren't actually allocating memory yet
		freeBlocksTEST = cloneSet(freeBlocks);
		//update the free space TEST variable to reflect the current free space in the system
		freeSpaceTEST = freeSpace;
		
		//check if the text segment can be allocated
		//we are just TESTING we aren't actually allocating memory yet (so we can back out these changes if there
		//isn't actually enough memory to allocate to all three segments)
		//so we are passing in our CLONES free blocks arraylist (freeBlocksTest) that we just initialized to be 
		//the same as our real freeBlocks and the free space test that we just initialized to be the same as our real
		//free space
		boolean textSegAlloc = allocMem(pid, text_size, 0, freeBlocksTEST, freeSpaceTEST, false);
		if(!textSegAlloc){
			//if the text segment couldn't be allocated then the whole process can't be allocated
			//the error message was already printed from allocMem so all we need to do is return -1
			return -1;
		}
		
		//check if the data segment can be allocated
		boolean dataSegAlloc = allocMem(pid, data_size, 1, freeBlocksTEST, freeSpaceTEST, false);
		if(!dataSegAlloc){
			return -1;
		}
		
		//check if the heap segment can be allocated
		boolean heapSegAlloc = allocMem(pid, heap_size, 2, freeBlocksTEST, freeSpaceTEST, false);
		if(!heapSegAlloc){
			return -1;
		}
		
		//if we made it this far it means that none of our segment allocations failed! YEY!
		//so now we can do it for real:
		//reinitialize pHelper so it's fresh
		pHelper = new int[8];
		//initialize the size of the process to pHelper
		pHelper[7] = bytes;
		
		//I will now pass in the real freeSpace and the real freeBlocks for each of the segments: text, data and heap
		//to actually allocate memory to this process 
		allocMem(pid, text_size, 0, freeBlocks, freeSpace, true);
		allocMem(pid, data_size, 1, freeBlocks, freeSpace, true);
		allocMem(pid, heap_size, 2, freeBlocks, freeSpace, true);
		
		//Now the pHelper is updated will all the right values
		//I will add this new process to the hashmap
		processes.put(pid, pHelper);
		
		//return 1 because allocation was successful! 
		return 1;
	}
	
	/**
	 * Helper method that 
	 * @param pid - process id
	 * @param segSize - size of the segment
	 * @param segNumber - number of the segment text:0, data:1, heap:2
	 * @param list - the free blocks list (either the real one or a clone)
	 * @param fSpace - the free space in the system (either the real one or a clone)
	 * @param real - if this is a test or if we are actually allocating memory
	 * @return
	 */
	private boolean allocMem(int pid, int segSize, int segNumber, TreeSet<int[]> set, int fSpace, boolean real){
		//need to figure out which segment this is, text, data or heap, so we can put the information into the correct 
		//index in the array
		//the start index, in which position of the values array the start of the segment will go 
		int startIndex = 0;
		//the size index, in which position of the values array the size of the segment will go 
		int sizeIndex = 0;
		//if it's the text segment
		if(segNumber == 0){
			startIndex = 0;
			sizeIndex = 1;
		}
		//if it's the data segment
		else if(segNumber == 1){
			startIndex = 2;
			sizeIndex = 3;
		}
		//if it's the heap segment
		else if(segNumber == 2){
			startIndex = 4;
			sizeIndex = 5;
		}
		
		//I am using the BEST FIT algorithm
		//so I am trying to find the BEST hole that will fit this segment
		//I am doing this by calling ceiling on my TreeSet which returns:
		// the least element in this set greater than or equal to the given element, or null if there is no such element.
		//this method is O(logN) so it is more efficient than a brute force linear iteration search
		int[] bestHole = (int[]) set.ceiling(new int[]{0,segSize});
		//if the best hole is null that means that there is no such element 
		//which means that there isn't enough free space available for the segment 
		if(bestHole == null)
		{
			//if the free space available is less than this segment size
			if(fSpace < segSize)
			{
				//increase the number of pure fails
				fail++;
				System.out.println("Unable to allocate memory to process "+pid+" due to insufficient free space");
			}
			else
			{
				//otherwise it must have been due to external fragmentation
				failExtF++;
				System.out.println("Unable to allocate memory to process "+pid+" due to external fragmentation");
			}
			return false;
		}
		//otherwise it means that there IS a hole large enough for the segment 

		//remove the hole from the set
		set.remove(bestHole);
		//enter the start segment section into the helper array to start building it 
		pHelper[startIndex] = bestHole[0];
		//check if the hole is only <=16 bytes larger than the hole
		if((bestHole[1] - segSize) <= 16)
		{
			//change the free space TEST to reflect losing this hole
			fSpace = fSpace - bestHole[1];
			//allocate the WHOLE hole to the this segment
			pHelper[sizeIndex] = bestHole[1];
			//add the difference to the internal fragmentation 
			pHelper[6] += (bestHole[1] - segSize);
			//delete the WHOLE hole
			//we've already done this
		}
		else
		{
			//change the free space TEST to reflect adding this segment
			fSpace = fSpace - segSize;
			//allocate the amount of the segment size to the segment process
			pHelper[sizeIndex] = segSize;
			//nothing to add to internal fragmentation in this case
			//edit the hole to reflect the amount remaining
			//new starting place (end of this segment)
			int[] newHole = new int[2];
			newHole[0] = pHelper[startIndex] + segSize;
			//new size (minus the text_size)
			newHole[1] = bestHole[1] - segSize;
			//add the hole BACK to the set (because we previously removed the whole thing)
			set.add(newHole);
		}

		//if it was "real" (if it wasn't a test and memory was actually allocated)
		//update freeSpace to properly show the changes
		if(real){
			freeSpace = fSpace;
		}
		//otherwise update the test variable (so that the next segments i.e. data and heap 
		//have accurate free space calculations)
		else{
			freeSpaceTEST = fSpace;
		}
		return true;
}
	
	/**
	 * Helper method for cloning the free blocks sorted set to use for testing
	 * @param freeBlocks2 - the sorted set to be cloned
	 * @return - a new, cloned sorted set
	 */
	private TreeSet<int[]> cloneSet(TreeSet<int[]> freeBlocks2){
		TreeSet<int[]> clone = new TreeSet<int[]>(new SizeComparator());
		int[] helper;
		int[] current;
		Iterator<int[]> itr=freeBlocks2.iterator();
		while(itr.hasNext()){
		   current = itr.next();
		   helper = new int[2];
		   helper[0] = current[0];
		   helper[1] = current[1];
		   clone.add(helper);
		}
	    return clone;
	}
	
	
	/**
	 * Deallocate memory allocated to this process
	 * return 1 if successful, -1 otherwise with a console message
	 */
	public int deallocate(int pid) {
		//get the value related to this process id key in the processes map
		int[] currentP = processes.remove(pid);
		//if currentP is null this means that the key wasn't found so we have an error
		if(currentP == null){
			System.out.println("Process "+pid+" could not be found");
			return -1;
		}
		//create holes for each of the segments and populate them with the correct starting point and size
		int[] textHole = new int[2];
		textHole[0] = currentP[0];
		textHole[1] = currentP[1];
		int[] dataHole = new int[2];
		dataHole[0] = currentP[2];
		dataHole[1] = currentP[3];
		int[] heapHole = new int[2];
		heapHole[0] = currentP[4];
		heapHole[1] = currentP[5];
		
		//update the free space to now include these holes
		freeSpace+=textHole[1];
		freeSpace+=dataHole[1];
		freeSpace+=heapHole[1];
		
		//call combineBlocks for each new hole 
		//to see if there is any way to combine these holes with previous holes
		combineBlocks(textHole);
		combineBlocks(dataHole);
		combineBlocks(heapHole);
		
		return 1;
	}

	/**
	 * Private helper method that works to combine any holes that are adjacent
	 * @param s - new hole that will be added
	 */
	private void combineBlocks(int[] s){
		//add all the entries from freeBlocks to freeBlocksStart so they will be ordered by start position
		TreeSet<int[]>freeBlocksStart = new TreeSet<int[]>(new StartComparator());
		freeBlocksStart.addAll(freeBlocks);
		//check if there are any holes with a starting position equal to the hole's end position
		int[] checkBlock = freeBlocksStart.ceiling(new int[]{(s[0]+s[1]), 0});
		//if the return wasn't null (meaning something was actually found)
		//AND the thing that was found has a starting point equal to the current block's starting point plus size (meaning they're adjacent)
		if((checkBlock != null)&&(checkBlock[0] == s[0]+s[1])){
			//remove the found, adjacent block from freeBlocks (the REAL data structure for the free blocks)
			freeBlocks.remove(checkBlock);
			//add a new block with now has starting point of the current block and size of the two blocks combined
			int[] newB = new int[]{s[0], (s[1]+checkBlock[1])};
			freeBlocks.add(newB);
			//now return because the hole has been added through newB
			return;
		}
		
		//add all the entries from freeBlocks to freeBlocksEnd so they will be ordered by end position
		TreeSet<int[]>freeBlocksEnd = new TreeSet<int[]>(new EndComparator());
		freeBlocksEnd.addAll(freeBlocks);
		//check if there are any holes with an end position that's equal to the start of the current block
		checkBlock = freeBlocksEnd.ceiling(new int[]{s[0], 0});
		//if the return wasn't null (meaning something was actually found)
		//AND the thing that was found has an ending point equal to the current block's starting point (meaning they're adjacent)
		if((checkBlock != null)&&((checkBlock[0] + checkBlock[1]) == s[0])){
			//remove the found, adjacent block from freeBlocks (the REAL data structure for the free blocks)
			freeBlocks.remove(checkBlock);
			//add a new block with now has starting point of the previous block and size of the two blocks combined
			int[] newB = new int[]{checkBlock[0], (s[1]+checkBlock[1])};
			freeBlocks.add(newB);
			//now return because the hole has been added through newB
			return;
		}
		//simply add this holes into the free holes list because we weren't able to find an adjacent hole for it
		freeBlocks.add(s);
		return;
	}
	
	
	// print out current state of memory
	 // the output will depend on the memory allocator being used.
	 // SEGMENTATION Example: 
	 // Memory size = 1024 bytes, allocated bytes = 179, free = 845
	// There are currently 10 holes and 3 active process
	 // Hole list:
	 // hole 1: start location = 0, size = 202
	 // ...
	 // Process list:
	 // process id=34, size=95 allocation=95
	 // text start=202, size=25
	 // data start=356, size=16
	 // heap start=587, size=54
	 // process id=39, size=55 allocation=65
	 // ...
	 // Total Internal Fragmentation = 10 bytes
	 // Failed allocations (No memory) = 2
	 // Failed allocations (External Fragmentation) = 7
	 // 
	public void printMemoryState() {
		//print out memory policy
		System.out.println("SEGMENTATION: ");
		//print out memory size
		System.out.println("Memory size = "+mSize+" bytes, allocated bytes = "+(mSize-freeSpace)+", free = "+freeSpace);
		//print out number of holes and processes
		System.out.println("There are currently "+freeBlocks.size()+" holes and "+processes.size()+" active processes");
		//print out the hole list
		System.out.println("Hole list: ");
		Iterator<int[]> itr=freeBlocks.iterator();
		int[] current;
		int count = 1;
		while(itr.hasNext()){
		   current = itr.next();
		   System.out.println("hole "+count+": start location = "+current[0]+", size = "+current[1]);
		   count++;
		}
		//variable to keep track of internal fragmentation
		int inFrag = 0;
		//print out the process list
		System.out.println("Process list:");
		for(Entry<Integer, int[]> entry : processes.entrySet()){
			inFrag += entry.getValue()[6];
		    System.out.println("process id = "+entry.getKey()+", size = "+entry.getValue()[7]+", allocation = "+(entry.getValue()[7]+entry.getValue()[6]));
		    System.out.println("text start = "+entry.getValue()[0]+", size = "+entry.getValue()[1]);
		    System.out.println("data start = "+entry.getValue()[2]+", size = "+entry.getValue()[3]);
		    System.out.println("text start = "+entry.getValue()[4]+", size = "+entry.getValue()[5]);
		}
		//print out internal fragmentation
		System.out.println("Total internal fragmentation = "+inFrag+" bytes");
		//print out failed allocation due to no memory
		System.out.println("Failed allocations due to no memory = "+fail);
		//print out failed allocations due to external fragmentation 
		System.out.println("Failed allocations due to external fragmentation = "+failExtF);
		
	}

}

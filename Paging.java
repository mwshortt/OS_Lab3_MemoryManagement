import java.awt.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * 
 * @author mshortt and ssethi
 *
 */
public class Paging implements MemoryPolicy {

	 int PAGE_SIZE = 32;
	int pages;
	int memSize;
	LinkedList<Integer> freePages;
	int external_frag = 0;
	ArrayList<Process> processList = new ArrayList<Process>();
	//Hashset
	 // Set<Process> processList;
	int noMemCount = 0;
	

	public Paging(int memSize) {
		this.memSize = memSize;
		//System.out.println("Paging has been created with memory size "
				//+ memSize);
		freePages = new LinkedList<Integer>();
		pages = splitMem(memSize, PAGE_SIZE);
		for (int i = 0; i < pages; i++) {
			freePages.add(i);
		}
		
	}

	public int allocate(int bytes, int pid, int text_size, int data_size,
			int heap_size) {
		//System.out.format("allocating process" + pid);

		if (bytes != text_size + data_size + heap_size) {
			return -1;
		}
		// allocate this many bytes to the process with this id
		// assume that each pid is unique to a process
		Process process = new Process(pid, bytes);
		// return 1 if successful
		// return -1 if unsuccessful; print an error indicating
		// whether there wasn't sufficient memory or whether
		// you ran into external fragmentation
		int length = process.pages.length;
		if (length > freePages.size()) {
			System.out.print("unsuccessful; insufficient memory");
			noMemCount++;
			external_frag++;
			return -1;
		}

		for (int i = 0; i < length; i++) {
			process.pages[i] = freePages.poll();

		}
		//add new process to process list
		processList.add(process);


		return 1;
	}

	@Override
	public int deallocate(int pid) {
		//System.out.format("Deallocating process" + pid);
		// deallocate memory allocated to this process
		// return 1 if successful, -1 otherwise with an error message
		// MY PSEUDOCODE
		// get process id
		// if process id is null, return -1 (unsuccessful)
		// walk through number of pages and add the free pages to the page at i

		Process process2 = processList.get(pid);
		if (process2 == null) {
			System.out.println("Unuccessful; process does not exist");

			return -1;
		}

		int pages[] = process2.pages;
		for (int i = 0; i < pages.length; i++) {
			freePages.add(pages[i]);
		}

		processList.remove(pid);
		return 1;
	}

	@Override
	public void printMemoryState() {
		System.out.println("PAGING EXAMPLE");

		System.out.println("From printMemoryState" + memSize + "Total pages: "
				+ PAGE_SIZE);
		System.out.println("Allocated pages:" + pages + "Free pages:"
				+ freePages.size());
		System.out.println("There are currently" + processList.size()
				+ "number of active processes");
		System.out.println("Free page list:");
		Iterator<Integer> iter = freePages.iterator();
		if (iter.hasNext()) {
			System.out.println(iter.next());

		}
		for(int i = 0; i<processList.size(); i++){
			processList.get(i).print();
		}

	}

	public int splitMem(int a, int b) {

		if (a % b == 0) {
			return a / b;
		} else {
			return a / b + 1;
		}

	}
	
	  private int computeInternalFrag(){
			int internal_frag = 0;
			for (int i =0; i<processList.size();i++){
			    internal_frag += processList.get(i).internalFrag();
			}	
			    
			return internal_frag;
		    }
	
	}

	/**
	 * Maintains information about a single process
	 * 
	 * @author sethi22s
	 *
	 */
 class Process  {
		int process_id;
		int process_size;
		int[] pages;
		Paging paging = new Paging(1024);

		public Process(int process_id, int process_size) {
			this.process_id = process_id;
			this.process_size = process_size;
			// split the system memory into a set of fixed size 32 byte pages,
			// and then allocate pages to each process based on the amount of
			// memory it needs.
			int allocate_pages = paging.splitMem(process_size, paging.PAGE_SIZE);
			pages = new int[allocate_pages];

		}

		public void print() {
			paging.printMemoryState();
			System.out.println("Process List:");
			System.out.println("Process id:" + process_id + "size:"
					+ process_size + "numbere of pages:" + pages.length);
			int used = paging.PAGE_SIZE;
			for (int i = 0; i < pages.length; i++) {
				if (i == pages.length - 1) {
					used = process_size % paging.PAGE_SIZE;
					if (used == 0) {
						used = paging.PAGE_SIZE;
					}
				}
				System.out.println("Virtual Page ->" + i + "Phys Page"
						+ pages[i] + "used" + used);
			}
			
			
			
		}

		/**
		 * Helper method for splitting the system memory
		 * 
		 * @param a
		 * @param b
		 * @return
		 */

//		public int splitMem(int a, int b) {
//
//			if (a % b == 0) {
//				return a / b;
//			}
//
//			else {
//				return a / b + 1;
//			}
//
//		}
		
		public int internalFrag(){
			int internal_frag = 0;

			internal_frag = process_size % paging.PAGE_SIZE;
		
			if (internal_frag!=0){ 
			   internal_frag = paging.PAGE_SIZE-internal_frag;	}
			return internal_frag;

		}
	}

 

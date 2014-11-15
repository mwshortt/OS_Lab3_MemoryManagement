/**
 * 
 * @author mshortt and ssethi
 *
 */
public class Paging implements MemoryPolicy {
	
	public Paging(int memSize) {
		System.out.println("Paging has been created with memory size " + memSize);
	}

	public int allocate(int bytes, int pid, int text_size, int data_size,
			int heap_size) 
	{
		System.out.println("From allocate - bytes: " + bytes + " pid: " + pid + " text_size: " + text_size + " data_size: "+ data_size + " heap_size: " + heap_size);
		return 0;
	}

	@Override
	public int deallocate(int pid) {
		System.out.println("From dealocate - pid: " + pid);
		return 0;
	}

	@Override
	public void printMemoryState() {
		System.out.println("From printMemoryState");
	}

}

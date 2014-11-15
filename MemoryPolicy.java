/**
 * This is the interface that will be used by Segmentation and Paging 
 * It allows us to use the Strategy Pattern in MemoryManagement
 * @author mshortt and ssethi
 *
 */
public interface MemoryPolicy {
	
	public int allocate(int bytes, int pid, int text_size, int data_size, int heap_size);
	
	public int deallocate(int pid);
	
	public void printMemoryState();

}

/**
 * 
 * @author mshortt and ssethi
 *
 */
public class Segmentation implements MemoryPolicy{
	
	public Segmentation(int memSize)
	{
		
	}

	@Override
	public int allocate(int bytes, int pid, int text_size, int data_size,
			int heap_size) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deallocate(int pid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void printMemoryState() {
		// TODO Auto-generated method stub
		
	}

}

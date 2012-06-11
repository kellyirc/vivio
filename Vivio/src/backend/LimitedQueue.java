import java.util.ArrayList;

public class LimitedQueue<E> extends ArrayList<E> {
	
	private int limit;
	
	public LimitedQueue(int limit) {
		this.limit = limit;
	}
	
	@Override
	public boolean add(E e) {
		if(size()>limit) {
			remove(0);
		}
		System.out.println(this);
		return super.add(e);
	}
}
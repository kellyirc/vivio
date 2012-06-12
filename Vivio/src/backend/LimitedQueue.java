/*
 * @author Kyle Kemp
 */
package backend;

import java.util.ArrayList;

public class LimitedQueue<E> extends ArrayList<E> {
	
	private static final long serialVersionUID = 1L;
	
	private int limit;
	
	public LimitedQueue(int limit) {
		this.limit = limit;
	}
	
	@Override
	public boolean add(E e) {
		if(size()>limit) {
			remove(0);
		}
		return super.add(e);
	}
}
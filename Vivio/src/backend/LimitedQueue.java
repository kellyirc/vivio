/*
 * @author Kyle Kemp
 */
package backend;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class LimitedQueue.
 * 
 * @param <E>
 *            the element type
 */
public class LimitedQueue<E> extends ArrayList<E> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The limit. */
	private int limit;

	/**
	 * Instantiates a new limited queue.
	 * 
	 * @param limit
	 *            the limit
	 */
	public LimitedQueue(int limit) {
		this.limit = limit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	@Override
	public boolean add(E e) {
		if (size() > limit) {
			remove(0);
		}
		return super.add(e);
	}
}
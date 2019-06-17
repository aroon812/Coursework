/**
 * This is an interface for a Queue. An object which implements this interface
 * is thus a "first in, first out", or "FIFO" structure. It can enqueue to the
 * end, dequeue from the front, or peek at the front.
 *
 * @author Adam Smith
 * @version 1.0
 */

public interface Queue<E> {
	/**
	 * Removes an item from the front of the queue.
	 *
	 * @return the value removed
	 */

	public E dequeue();

	/**
	 * Adds an item to the end of the queue.
	 *
	 * @param value  the value to be enqueued
	 */

	public void enqueue(E value);

	/**
	 * Checks item at the front of the queue, without altering the queue.
	 *
	 * @return the value checked
	 */

	public E peek();
}

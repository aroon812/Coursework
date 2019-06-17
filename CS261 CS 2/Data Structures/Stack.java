/**
 * This is an interface for a Stack. An object which implements this interface
 * is thus a "first in, last out", or "FILO" structure. It can push to the top,
 * pop from the top, or peek at the top.
 *
 * @author Adam Smith
 * @version 1.0
 */

public interface Stack<E> {
	/**
	 * Checks item at the top of the stack, without altering the stack.
	 *
	 * @return the value checked
	 */

	public E peek();

	/**
	 * Removes an item from the top of the stack.
	 *
	 * @return the value removed
	 */

	public E pop();

	/**
	 * Adds an item to the top of the stack.
	 *
	 * @param value  the value to be enqueued
	 */

	public void push(E value);
}
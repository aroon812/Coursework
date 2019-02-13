import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.ParallelTransitionBuilder;

/**
 * A priority queue class implemented using a max heap. Priorities cannot be
 * negative.
 * 
 * @author Braden Ash & Aaron Thompson
 * @version 9/19/18
 *
 */
public class PriorityQueue {

	private Map<Integer, Integer> location;
	private List<Pair<Integer, Integer>> heap;

	/**
	 * Constructs an empty priority queue
	 */
	public PriorityQueue() {
		location = new HashMap<Integer, Integer>();
		heap = new ArrayList<Pair<Integer, Integer>>();
	}

	/**
	 * Insert a new element into the queue with the given priority.
	 *
	 * @param priority priority of element to be inserted
	 * @param element  element to be inserted
	 *
	 *                 <dt><b>Preconditions:</b>
	 *                 <dd>
	 *                 <ul>
	 *                 <li>The element does not already appear in the priority
	 *                 queue.</li>
	 *                 <li>The priority is non-negative.</li>
	 *                 </ul>
	 * 
	 */
	public void push(int priority, int element) {
		Pair object = new Pair(priority, element);

		if (location.containsKey(element)) {
			throw new AssertionError("Value already in Priority Queue", null);
		} else if (priority < 0) {
			throw new AssertionError("Priority must be non-negative", null);
		}

		heap.add(object);
		location.put(element, heap.size() - 1);
		percolateUp(heap.size() - 1);

	}

	/**
	 * Remove the highest priority element
	 * 
	 * <dt><b>Preconditions:</b>
	 * <dd>
	 * <ul>
	 * <li>The priority queue is non-empty.</li>
	 * </ul>
	 * 
	 */
	public void pop() {
		if (heap.size() == 0) {
			throw new AssertionError("Priority Queue must be non empty.", null);
		}
		swap(0, heap.size() - 1);
		location.remove(heap.get(heap.size() - 1).element);
		heap.remove(heap.size() - 1);
		pushDown(0);

	}

	/**
	 * Returns the highest priority in the queue
	 * 
	 * @return highest priority value
	 * 
	 *         <dt><b>Preconditions:</b>
	 *         <dd>
	 *         <ul>
	 *         <li>The priority queue is non-empty.</li>
	 *         </ul>
	 */
	public int topPriority() {
		if (heap.size() == 0) {
			throw new AssertionError("Priority Queue must be non empty.", null);
		}
		return heap.get(0).priority;
	}

	/**
	 * Returns the element with the highest priority
	 * 
	 * @return element with highest priority
	 *
	 *         <dt><b>Preconditions:</b>
	 *         <dd>
	 *         <ul>
	 *         <li>The priority queue is non-empty.</li>
	 *         </ul>
	 */
	public int topElement() {
		if (heap.size() == 0) {
			throw new AssertionError("Priority Queue must be non empty.", null);
		}
		return heap.get(0).element;
	}

	/**
	 * Change the priority of an element already in the priority queue.
	 * 
	 * @param element     element whose priority is to be changed
	 * @param newpriority the new priority
	 * 
	 *                    <dt><b>Preconditions:</b>
	 *                    <dd>
	 *                    <ul>
	 *                    <li>The element exists in the priority queue</li>
	 *                    </ul>
	 */
	public void changePriority(int element, int newpriority) {
		if (!location.containsKey(element)) {
			throw new AssertionError("Element must be in the Priority Queue.", null);
		}

		int i = location.get(element);
		if (heap.get(i).priority > newpriority) {
			heap.get(i).priority = newpriority;
			pushDown(i);
		} else if (heap.get(i).priority < newpriority) {
			heap.get(i).priority = newpriority;
			percolateUp(i);
		}
	}

	/**
	 * Gets the priority of the element
	 * 
	 * @param element the element whose priority is returned
	 * @return the priority value
	 *
	 *         <dt><b>Preconditions:</b>
	 *         <dd>
	 *         <ul>
	 *         <li>The element exists in the priority queue</li>
	 *         </ul>
	 */
	public int getPriority(int element) {
		if (!isPresent(element)) {
			throw new AssertionError("Element must exist in the priority queue", null);
		}
		return heap.get(location.get(element)).priority;
	}

	/**
	 * Returns true if the priority queue contains no elements
	 * 
	 * @return true if the queue contains no elements, false otherwise
	 */
	public boolean isEmpty() {
		if (heap.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the element exists in the priority queue.
	 * 
	 * @return true if the element exists, false otherwise
	 */
	public boolean isPresent(int element) {
		if (location.containsKey(element)) {
			return true;
		}
		return false;
	}

	/**
	 * Removes all elements from the priority queue
	 */
	public void clear() {
		heap.clear();
		location.clear();
	}

	/**
	 * Returns the number of elements in the priority queue
	 * 
	 * @return number of elements in the priority queue
	 */
	public int size() {
		return heap.size();
	}

	/*********************************************************
	 * Private helper methods
	 *********************************************************/

	/**
	 * Push down a given element
	 * 
	 * @param start_index the index of the element to be pushed down
	 * @return the index in the list where the element is finally stored
	 */
	private int pushDown(int start_index) {
		while (left(start_index) < heap.size()) {
			// has a right child
			if (hasTwoChildren(start_index)) {
				// left greater than right
				if (heap.get(left(start_index)).priority > heap.get(right(start_index)).priority) {
					if (heap.get(start_index).priority < heap.get(left(start_index)).priority) {
						swap(start_index, left(start_index));
						start_index = left(start_index);
					} else {
						return start_index;
					}
				}
				// right greater than left
				else {
					if (heap.get(start_index).priority < heap.get(right(start_index)).priority) {
						swap(start_index, right(start_index));
						start_index = right(start_index);
					} else {
						return start_index;
					}
				}
			}
			// does not have a right child
			else {
				if (heap.get(start_index).priority < heap.get(left(start_index)).priority) {
					swap(start_index, left(start_index));
					start_index = left(start_index);
				} else {
					return start_index;
				}
			}
		}
		return start_index;
	}

	/**
	 * Percolate up a given element
	 * 
	 * @param start_index the element to be percolated up
	 * @return the index in the list where the element is finally stored
	 */
	private int percolateUp(int start_index) {
		int parentIndex = parent(start_index);
		while (heap.get(start_index).priority > heap.get(parentIndex).priority) {
			swap(start_index, parentIndex);
			start_index = parentIndex;
			parentIndex = parent(start_index);
		}
		return start_index;
	}

	/**
	 * Swaps two elements in the priority queue by updating BOTH the list
	 * representing the heap AND the map
	 * 
	 * @param i element to be swapped
	 * @param j element to be swapped
	 */
	private void swap(int i, int j) {

		// Swap for HashMap
		location.put(heap.get(j).element, i);
		location.put(heap.get(i).element, j);
		// Swap for ArrayList
		Pair temp = heap.get(i);
		heap.set(i, heap.get(j));
		heap.set(j, temp);
	}

	/**
	 * Computes the index of the element's left child
	 * 
	 * @param parent index of element in list
	 * @return index of element's left child in list
	 */
	private int left(int parent) {
		return (2 * parent) + 1;
	}

	/**
	 * Computes the index of the element's right child
	 * 
	 * @param parent index of element in list
	 * @return index of element's right child in list
	 */
	private int right(int parent) {
		return (2 * parent) + 2;
	}

	/**
	 * Computes the index of the element's parent
	 * 
	 * @param child index of element in list
	 * @return index of element's parent in list
	 */

	private int parent(int child) {
		return (child - 1) / 2;
	}

	/*********************************************************
	 * These are optional private methods that may be useful
	 *********************************************************/

	/**
	 * Returns true if element has two children in the heap
	 * 
	 * @param i index of element in the heap
	 * @return true if element in heap has two children
	 */
	private boolean hasTwoChildren(int i) {
		if (right(i) >= heap.size() || left(i) >= heap.size()) {
			return false;
		}
		return true;
	}

	/**
	 * Print the underlying list representation
	 */
	private void printHeap() {
		String s = "";
		for (int i = 0; i < heap.size(); i++) {
			s += "(" + heap.get(i).priority + " ," + heap.get(i).element + ")";
		}
		System.out.println(s);
	}

	/**
	 * Print the entries in the location map
	 */
	private void printMap() {
		System.out.println(location.entrySet());
	}

}

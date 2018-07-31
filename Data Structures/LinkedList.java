/**
 * An implementation of a linked list
 * @author Aaron Thompson
 * @version 4/4/2017
 * @param <E>
 */
class LinkedList<E> implements Stack<E>, Queue<E>
{
	private Node head, tail; //the start and end of the LinkedList
	private int size; //the size of the linked list
	
	/**
	 * Adds to the start of the LinkedList 
	 * @param value the object to be added
	 */
	public void addToHead(E value)
	{
		head = new Node(value, head);
		//if the linked list is empty, make the tail and the head the first object
		if (head.next == null) tail = head;
		size++;
	}
	
	/**
	 * Adds to the end of the LinkedList
	 * @param value the object to be added
	 */
	public void addToTail(E value)
	{
		Node newNode = new Node(value, null);
		if (size == 0)
		{
			head = newNode;
			tail = newNode;
		}
		else{
			tail.next = newNode; //link the tail to the new node
			tail = newNode; //make the new node the new tail
		}
		
		
		size++;
	}
	
	/**
	 * Returns the head of the LinkedList
	 * @return the head
	 */
	public E getHead()
	{
		return head.value;
	}
	
	/**
	 * Returns the size of the linked list
	 * @return the size of the linked list
	 */
	public int getSize()
	{
		return size;
	}
	
	/**
	 * Removes the head element from the list and makes the next element the new head
	 * @return the removed head, or null if the list is empty
	 */
	public E removeHead()
	{
		if (size == 0) return null; //if the list is empty
		E removedHead = head.value;
		if (head == tail) tail = head.next; //if list has only one element, also update the tail
		head = head.next;
		
		size--;
		return removedHead;
	}
	
	/**
	 * checks to see if the LinkedList contains a value
	 * @param value the value which is being searched for
	 * @return whether the value is found in the LinkedList
	 */
	public boolean contains(E value)
	{
		//loop through entire linked list in search of the value
		for (Node current = head; current != null; current = current.next)
		{
			if(current.value.equals(value))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes an object from the queue
	 * @return the object that was removed
	 */
	@Override
	public E dequeue() 
	{
		return removeHead();
	}
	
	/**
	 * Adds an object to the queue
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void enqueue(Object value) 
	{
		addToTail((E) value);
		
	}
	
	/**
	 * Returns the next object on the queue
	 * @return the next object in the queue
	 */
	@Override
	public E peek() 
	{
		return getHead();
	}

	/**
	 * takes from the stack
	 */
	@Override
	public E pop() 
	{
		return removeHead();
	}

	/**
	 * Puts an object on the stack
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void push(Object value) 
	{
		addToHead((E) value);
	}
	
	private class Node
	{
		public E value;
		public Node next;
		
		public Node(E value, Node next)
		{
			this.value = value;
			this.next = next;
		}
	}
	
	/**
	 * Returns a string representation of the LinkedList
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		for (Node current = head; current != null; current = current.next)
		{
			builder.append("(");
			builder.append(current.value);
			builder.append(")");
			if (current != tail) builder.append(" -> ");
		}
		
		return builder.toString();
	}
	
	public static void main(String[] args)
	{
		LinkedList<Integer> linkedList = new LinkedList<Integer>();
		Queue<Character> queue = new LinkedList<Character>();
		Stack<String> stack = new LinkedList<String>();
		
		System.out.println("Adding elements to the stack...");
		stack.push("cat");
		stack.push("dog");
		stack.push("fox");
		System.out.println("Printing the stack...");
		System.out.println(stack.toString());
		System.out.println("Taking from the stack...");
		stack.pop();
		System.out.println(stack.toString());
		
		System.out.println("Adding elements to the queue...");
		queue.enqueue('a');
		queue.enqueue('b');
		queue.enqueue('c');
		queue.enqueue('d');
		System.out.println("Printing the queue...");
		System.out.println(queue.toString());
		System.out.println("Removing an object from the queue...");
		queue.dequeue();
		System.out.println(queue.toString());
		
		System.out.println("Adding elements to the linked list...");
		linkedList.addToHead(1);
		linkedList.addToTail(2);
		linkedList.addToTail(3);
		System.out.println("Printing the linked list...");
		System.out.println(linkedList.toString());
		System.out.println("Linked list contains 2? " + linkedList.contains(2));
		System.out.println("Removing from head...");
		linkedList.removeHead();
		System.out.println(linkedList.toString());
		
		
		
		
	}

}

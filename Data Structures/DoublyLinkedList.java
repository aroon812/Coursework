
/**
 * Implementation of a doubly linked list
 * @author Aaron Thompson
 * @version 4/6/2017
 * @param <E>
 */

class DoublyLinkedList<E> 
{
	private Node head, tail; //the head and tail of the DoublyLinkedList
	private int size; //the size of the doubly linked list
	
	/**
	 * Adds a value to a location in the list
	 * @param value the value to be added
	 * @param location the location where the value will be added
	 */
	public void add(E value, int location)
	{
		if (location < 0 || location >= size) throw new IndexOutOfBoundsException("Bad location: " + location);
		
		if (location == 0)
		{
			addToHead(value);
			return;
		}
		
		Node insertionPoint;
		Node newNode = new Node(value, null, null);
			
		//find the insertionPoint
		if (location <= size/2)
		{
			insertionPoint = head;
			for (int i = 0; i != location-1; i++) insertionPoint = insertionPoint.next; 	
		}
		
		else{
			insertionPoint = tail;
			for (int i = size-1; i != location-1; i--) insertionPoint = insertionPoint.prev;
		}
		
		//place the new node right after the insertion point
		newNode.next = insertionPoint.next;
		newNode.prev = insertionPoint;
		insertionPoint.next = newNode;
		size++;
	}
	
	/**
	 * Removes the object at the specified location
	 * @param location the location of the object to be removed
	 * @return the object that was removed
	 */
	public E remove(int location)
	{
		if (location < 0 || location >= size) throw new IndexOutOfBoundsException("Bad Location: " + location);
		
		if (location == 0) return removeHead(); //if the location is at the beginning of the list
		if (location == size-1) return removeTail(); //if the location is at the end of the list
		
		Node current;
		
		//find the location of the node to be removed
		if (location <= size/2)
		{
			current = head;
			for (int i = 0; i < location; i++) current = current.next;
		}
		
		else{ 
			current = tail;
			for (int i = size-1; i > location; i--) current = current.prev;
		}
		
		//remove the current node
		current.prev.next = current.next;
		current.next.prev = current.prev;
		size--;
		return current.value;
	}
	
	/**
	 * Removes the head of the linked list
	 * @return the head that was removed, or null if the list is empty
	 */
	public E removeHead()
	{
		if (size == 0) return null; //if the list is empty, return null
		E removedHead = head.value;
		if (head == tail) tail = head.next; //if list has only one element, also update the tail
		
		head = head.next;
		
		size--;
		return removedHead;
	}
	
	/**
	 * Removes the tail of the linked list
	 * @return the tail that was removed, or null if the list is empty
	 */
	public E removeTail()
	{
		if (size == 0) return null; //if the list is empty, return null
		E removedTail = tail.value;
		if (head == tail) head = tail.prev; //if the list only has one element, also update the head
		
		tail = tail.prev;
		tail.next = null;
		
		size--;
		return removedTail;
	}
	
	/**
	 * Adds to the beginning of the DoublyLinkedList 
	 * @param value the added object
	 */
	public void addToHead(E value)
	{
		head = new Node(value, head, null);
		
		//if this object is the first in the list, make both the head and the tail aliases of it
		if (head.next == null) tail = head;
		else head.next.prev = head; //for the next element, make the previous element the new head
			
		size++;
	}
	
	/**
	 * Adds to the end of the DoublyLinkedList
	 * @param value the added object
	 */
	public void addToTail(E value)
	{
		Node newNode = new Node(value, null, null);
		if (size == 0)
		{
			head = newNode;
		}
		else{
			//link the previous tail to the new node
			tail.next = newNode; 
			newNode.prev = tail;
		}
		
		tail = newNode; //make the tail the new node
		size++;
	}
	
	/**
	 * Returns a boolean stating whether the list contains a specified value
	 * @param value the value that is being searched for
	 * @return if the value was found
	 */
	public boolean contains(E value)
	{
		if (find(value) != -1) return true;
		return false;
	}
	
	/**
	 * Returns the index of a specified value
	 * @param value that is being searched for
	 * @return the index of the value in the list, or -1 if the value is not found
	 */
	public int find(E value)
	{
		int currentIndex = 0;
		//loop through entire list in search of the value
		for (Node current = head; current != null; current = current.next, currentIndex++)
		{
			if (current.value.equals(value)) return currentIndex;
		}
		
		return -1;
	}
	
	/**
	 * Returns an object at the given location
	 * @param index the location of the object
	 * @return the object
	 */
	public E get(int index)
	{
		if (index >= size) throw new IndexOutOfBoundsException("Bad location: " + index); //throw an exception if the index is out of bounds
		
		Node current;
		if (index <= size/2) //if the index is in the first half of the list
		{
			current = head;
			for (int i = 0; i < index; i++) current = current.next;
		}
		
		//if the index is in the second half of the list
		else{ 
			current = tail;
			for (int i = size-1; i > index; i--) current = current.prev;
		}
		
		return current.value; //return the value of the current node	
	}
	
	/**
	 * Returns the head of the linked list
	 * @return the head of the linked list
	 */
	public E getHead()
	{
		return head.value;
	}
	
	/**
	 * Returns the tail of the linked list
	 * @return the tail of the linked list
	 */
	public E getTail()
	{
		return tail.value;
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
	 * Returns a string representation of the DoublyLinkedList
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		//iterate through entire list, adding everything to the stringbuilder
		for (Node current = head; current != null; current = current.next)
		{
			builder.append("(");
			builder.append(current.value);
			builder.append(")");
			if (current != tail) builder.append(" -> ");
		}
		
		return builder.toString();
	}
	
	private class Node
	{
		public E value;
		public Node next;
		public Node prev;
		
		public Node(E value, Node next, Node prev)
		{
			this.value = value;
			this.next = next;
			this.prev = prev;
		}
	}
	
	public static void main(String[] args)
	{
		DoublyLinkedList<Integer> list = new DoublyLinkedList<Integer>();
		list.addToHead(12);
		list.addToHead(18);
		list.addToHead(22);
		list.addToHead(34);
		System.out.println("12, 18, 22, and 34 added to the head. List is now " + list.toString());
		list.addToHead(50);
		System.out.println("50 added to the head. List is now " + list.toString());
		list.addToTail(3);
		System.out.println("3 added to the tail. List is now " + list.toString());
		list.addToTail(5);
		System.out.println("5 added to the tail. List is now " + list.toString());
		list.removeHead();
		System.out.println("Removed head of the list, List is now " + list.toString());
		list.removeTail();
		System.out.println("Removed tail of the list, List is now " + list.toString());
		list.add(100, 1);
		System.out.println("100 added to the list at index 1. List is now " + list.toString());
		System.out.println("The index of object containing 22 is " + list.find(22));
		System.out.println("The list contains the value of 18? " + list.contains(18));
		list.remove(1);
		System.out.println("Removed object at index 1. List is now " + list.toString());
		
	}
}

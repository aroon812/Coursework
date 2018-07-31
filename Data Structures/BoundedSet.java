
/**
 * Holds a set of objects
 * @author Aaron Thompson
 * @version 1/31/2017
 */
class BoundedSet<E> {
	private int capacity;
	private int numElements;
	private E[] array;
	
	/**
	 * The constructor to create a new BoundedSet
	 * @param capacity the size the user wants the BoundedSet to be
	 */
	@SuppressWarnings("unchecked")
	public BoundedSet(int capacity)
	{
		numElements = 0;
		array = (E[])(new Object [capacity]);
		this.capacity = capacity;
	}
	
	/**
	 * Adds an element to the BoundedSet
	 * @param element the element to be added
	 * @return whether the element was sucessfully added
	 */
	boolean add(E element)
	{
		if(numElements == capacity || element == null || contains(element))
		{
			return false;
		}
		
		array[numElements] = element;
		numElements++;
		return true;
	}
	
	/**
	 * Checks to see if the BoundedSet contains an element
	 * @param element the element the user is searching for
	 * @return whether the element was found
	 */
	boolean contains(E element)
	{
		if(findIndex(element) == -1)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * returns the size of the BoundedSet
	 * @return the size of the BoundedSet
	 */
	int getCapacity()
	{
		return capacity;
	}
	
	/**
	 * returns how many elements are in the BoundedSet
	 * @return the number of elements in the BoundedSet
	 */
	int getSize()
	{
		return numElements;
	}
	
	/**
	 * removes an element in the array
	 * @param element the element to be removed
	 * @return whether the remove was successful
	 */
	boolean remove(E element)
	{
		int index = findIndex(element);
		if(index == -1)
		{
			return false;
		}
		
		array[index] = null;
		
		for (int i = index; i < getSize(); i++) 
		{
			array[index] = array[index+1];
		}
		array[index+1] = null;
		numElements--;
		return true;
	}
	
	/**
	 * Returns a String representation of the BoundedSet
	 * @return a String representation of the BoundedSet
	 */
	@Override
	public String toString()
	{
		String stringRep = "";
		for (int i = 0; i < getSize(); i++) 
		{
			stringRep += "[" + array[i].toString() + "],";
		}
		return stringRep;
	}
	
	private int findIndex(E element)
	{
		for (int i = 0; i < getSize(); i++) 
		{
			if(element.equals(array[i]))
			{
				return i;
			}
		}
		return -1;
	}
	
	
	
	public static void main(String[] args)
	{
		BoundedSet<String> stringSet = new BoundedSet<String>(10);
		stringSet.add("cat");
		stringSet.add("dog");
		stringSet.add("frog");
		System.out.println(stringSet.toString());
		System.out.println("stringSet contains \"cat\": " + stringSet.contains("cat"));
		System.out.println("stringSet contains \"yolo\": " + stringSet.contains("yolo"));
		
		stringSet.remove("dog");
		System.out.println("Removing \"dog\" " + stringSet.toString());
		System.out.println("Capacity of stringSet: " + stringSet.getCapacity());
		System.out.println("Number of elements in stringSet: " + stringSet.getSize());
		
		BoundedSet<Integer> intSet = new BoundedSet<Integer>(10);
		intSet.add(1);
		intSet.add(10);
		intSet.add(7);
		intSet.add(20);
		System.out.println();
		System.out.println(intSet.toString());
		System.out.println("intSet contains \"10\": " + intSet.contains(10));
		System.out.println("intSet contains \"70\": " + intSet.contains(70));
		
		intSet.remove(7);
		System.out.println("Removing \"7\": " + intSet.toString());
		System.out.println("Capacity of intSet: " + intSet.getCapacity());
		System.out.println("Number of elements in intSet: " + intSet.getSize());
		
		
		
	}
	
}

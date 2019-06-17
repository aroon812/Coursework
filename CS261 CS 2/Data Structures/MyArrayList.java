/**
 * A list of objects
 * @author Aaron Thompson
 * @version 2/5/17
 */
public class MyArrayList<E>
{
	private int size;
	private E[] array;
	private final static int DEFAULT_CAPACITY = 10;
	
	/**
	 * Creates a new MyArrayList
	 * @param capacity the initial size of the MyArrayList
	 */
	@SuppressWarnings("unchecked")
	public MyArrayList(int capacity)
	{
		size = 0;
		array = (E[])(new Object [capacity]);
	}
	
	public MyArrayList()
	{
		this(DEFAULT_CAPACITY);
	}
	
	/**
	 * Adds an element to the MyArrayList
	 * @param element the element the user wishes to add
	 */
	void add(E element)
	{	
		//If the array isn't big enough, double the size
		if(size == array.length) reallocateArray(array.length*2);
		array[size] = element;
		size++;
	}
	
	/**
	 * Adds an element to the MyArrayList at the specified index
	 * @param index the index where the element will be added
	 * @param element the element to be added
	 */
	void add(int index, E element)
	{
		ensureCapacity(index);

		//move everything above the newly added element to the right
		for(int i = index; i < array.length; i++)
		{
			if(size == array.length) reallocateArray(array.length*2);
			array[index+1] = array[index];
		}
		
		array[index] = element;
		if(index > size)
		{
			size = index;
		}
		else{
			size++;
		}
	}
	
	/**
	 * clears the MyArrayList
	 */
	void clear()
	{
		//setting all elements of the array to null
		for(int i = 0; i < array.length; i++)
		{
			array[i] = null;
		}
		size = 0;
		
		reallocateArray(DEFAULT_CAPACITY);
	}
	
	/**
	 * returns whether the MyArrayList contains a specified element
	 * @param element the element that is being searched for
	 * @return whether the MyArrayList contains a specified element
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
	 * Makes sure that the MyArrayList is as big or bigger than the capacity
	 * @param capacity The capacity to expand the MyArrayList
	 */
	void ensureCapacity(int capacity)
	{
		//double in size until the array can hold the capacity
		while(capacity > array.length-1)
		{
			reallocateArray(array.length*2);
		}
	}
	
	/**
	 * Finds the index of a specified element
	 * @param element the element that will be searched for
	 * @return the index of the element, or -1 if the element is not found
	 */
	int findIndex(E element)
	{
		//loop iterates through the array searching for the element
		for (int i = 0; i <= size; i++) 
		{
			if(array[i] != null)
			{
				if(array[i].equals(element))
				{
					return i;
				}
			}
		}
		return -1;		
	}
	
	/**
	 * Returns the element at the specified index
	 * @param index the index from where the element will be returned
	 * @return the element located at the index
	 */
	E get(int index)
	{
		if(index >= array.length)throw new IndexOutOfBoundsException("Invalid Index");
		return array[index];
	}
	
	/**
	 * returns the capacity of the MyArrayList
	 * @return the capacity of the MyArrayList
	 */
	int getCapacity()
	{
		return array.length;
	}
	
	/**
	 * Returns the number of elements in the MyArrayList
	 * @return the number of elements in the MyArrayList
	 */
	int getSize()
	{
		return size;
	}
	
	/**
	 * Returns a boolean stating whether the MyArrayList is empty
	 * @return a boolean stating whether the MyArrayList is empty
	 */
	boolean isEmpty()
	{
		if(size == 0) return true;
		return false;
	}
	
	/**
	 * Removes an element at a given index
	 * @param index the index where an element will be removed
	 */
	void remove(int index)
	{
		if(index >= array.length || index < 0) throw new IndexOutOfBoundsException("Invalid index");
		
		else
		{
			//Shifts all of the other elements down
			for (int i = index; i <= size; i++) 
			{
				array[i] = array[i+1];
			}
			size--;
		}
		
		//If the number of elements is below or equal to a quarter of the actual array capacity, cut the array capacity in half
		if(size <= array.length/4) reallocateArray(array.length/2);
	}
	
	void set(int index, E element)
	{
		if(index >= array.length || index < 0) throw new IndexOutOfBoundsException("Invalid index");
		
		else{
			array[index] = element;
		}
	}
	
	/**
	 * Returns a comma separated list of all the elements in the MyArrayList
	 * @return a string representation of all the elements in the MyArrayList
	 */
	@Override
	public String toString()
	{
		String stringRep = "";
		//Loops through the array and adds every object to stringRep
		for(int i = 0; i <= size; i++)
		{
			stringRep += "[" + array[i] + "], ";
		}
		return stringRep;
	}
	
	@SuppressWarnings("unchecked")
	private void reallocateArray(int newCapacity)
	{
		if(newCapacity == array.length) return;
		E[] newArray = (E[]) new Object[newCapacity];
		
		if(newCapacity > array.length) System.arraycopy(array, 0, newArray, 0, array.length);
		else System.arraycopy(array, 0, newArray, 0, newCapacity);
		
		array = newArray;
	}
	
	public static void main(String[] args)
	{
		MyArrayList<Integer> intList = new MyArrayList<Integer>(5);
		MyArrayList<String> stringList = new MyArrayList<String>(5);
		intList.add(1);
		intList.add(6, 15);
		intList.add(8, 70);
		System.out.println(intList.toString());	
		System.out.println(intList.contains(15));
		System.out.println(intList.contains(80));
		System.out.println(intList.findIndex(15));
		System.out.println(intList.findIndex(80));
		System.out.println(intList.get(0));
		System.out.println(intList.getCapacity());
		System.out.println(intList.getSize());
		intList.remove(6);
		System.out.println(intList.toString());
		intList.set(2, 3);
		System.out.println(intList.toString());
		intList.ensureCapacity(15);
		intList.clear();
		System.out.println(intList.toString());
		
		
		
		stringList.add("A");
		stringList.add(5, "B");
		stringList.add(8, "C");
		System.out.println(stringList.toString());
		System.out.println(stringList.contains("A"));
		System.out.println(stringList.contains("D"));
		System.out.println(stringList.findIndex("C"));
		System.out.println(stringList.findIndex("E"));
		System.out.println(stringList.get(5));
		System.out.println(stringList.getCapacity());
		System.out.println(stringList.getSize());
		stringList.remove(0);
		System.out.println(stringList.toString());
		stringList.set(3, "F");
		System.out.println(stringList.toString());
		stringList.ensureCapacity(15);
		stringList.clear();
		System.out.println(stringList.toString());
		
		
		
	}
	
}

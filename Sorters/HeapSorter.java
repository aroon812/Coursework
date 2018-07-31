import java.util.Arrays;
import java.util.Scanner;

/**
 * Use heapsort to sort a provided array
 * @author Aaron Thompson
 * 2/23/17
 */

class HeapSorter extends Sorter
{
	/**
	 * Uses heapsort to sort the provided array
	 */
	@Override
	public <E extends Comparable<E>> void sort(E[] array) 
	{
		heapify(array);
		int range = array.length-1;
		for(int i = 0; i < array.length; i++, range--)
		{
			swap(array,0, range);
			sink(array, 0, range);
		}
		//swap(array ,0 , 1);
	}
	
	//turns an array of any order into a heap
	private <E extends Comparable<E>> void heapify(E[] array)
	{
		int lastParent = getLastParent(array.length);
		
		
		for(int i = lastParent; i >= 0; i--)
		{
			sink(array, i, array.length);
		}
	}
	
	//Sinks the element until it is bigger or equal to it's children
	private static <E extends Comparable<E>> void sink(E[] array, int index, int range)
	{
		boolean hasRightChild = false;
		boolean hasLeftChild = false;
		int biggerChild = index;
		int newIndex = index;
		
		//infinite loop until the parent has no children smaller than it
		while(true)
		{
			//check for any possible children
			if(getLeftChild(index) > 0 && getLeftChild(index) < array.length)
			{
				hasLeftChild = true;
			}
			if(getRightChild(index) > 0 && getRightChild(index) < array.length)
			{
				hasRightChild = true;
			}
			
			//has no children
			if(!hasRightChild && !hasLeftChild)
			{
				return;
			}
			
			//has both children
			else if(hasRightChild && hasLeftChild)
			{
				//find the bigger child
				if(array[getRightChild(index)].compareTo(array[getLeftChild(index)]) > 0)
				{
					biggerChild = getRightChild(index);
				}
				else if(array[getLeftChild(index)].compareTo(array[getRightChild(index)]) > 0)
				{
					biggerChild = getLeftChild(index);
				}
				else{
					biggerChild = getLeftChild(index);
				}
			}
			
			//only has right child
			else if(hasRightChild)
			{
				biggerChild = getRightChild(index);
			}
			
			//only has left child
			else if(hasLeftChild)
			{
				biggerChild = getLeftChild(index);
			}
			
			if(array[biggerChild].compareTo(array[index]) > 0)
			{
				//only swap the bigger child if it is within the range
				if(biggerChild < range)
				{
					newIndex = biggerChild;
					swap(array, index, biggerChild);
				}
				
				//return before the sort touches any elements that have already been sorted
				else{
					return;
				}
			}
			
			//if the child is not bigger, exit the method
			else{
				return;
			}
			
			index = newIndex;
			hasRightChild = false;
			hasLeftChild = false;
		}
	}
	
	//swaps two elements within the array
	private static <E extends Comparable<E>>void swap(E[] array, int index1, int index2)
	{
		E temp = array[index1];
		array[index1] = array[index2];
		array[index2] = temp;
	}
	
	//returns the left child of an element
	private static int getLeftChild(int index)
	{
		return (index*2)+1;
	}
	
	//returns the right child of an element
	private static int getRightChild(int index)
	{
		return (index*2)+2;
	}
	
	//returns the last parent in a heap
	private static int getLastParent(int range)
	{
		return (range-2)/2;
	}
	
	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("Testing heapsort.");
		System.out.print("Please enter the array size: ");
		String input = scan.nextLine();
		int size = 0;
		
		try{
			size = Integer.parseInt(input);
		}
		catch(NumberFormatException e){
			System.out.println("Bad number. Exiting the program...");
			System.exit(1);
		}
		
		HeapSorter sorter = new HeapSorter();
		int sort = sorter.timeSort(size);
		System.out.println("Array of size " + size + " took " + sort + " ms to sort.");
	}
	
}

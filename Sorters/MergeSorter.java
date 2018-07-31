import java.util.Scanner;

/**
 * An implementation of mergesort
 * @author Aaron Thompson
 * @version 3/6/2017
 */
class MergeSorter extends Sorter
{

	/**
	 * Uses mergesort to sort the provided array
	 * @param array the array that will be sorted
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <E extends Comparable<E>> void sort(E[] array) 
	{
		E[] buffer = (E[]) new Comparable[array.length/2];
		sort(array, 0 , array.length, buffer);
		
	}
	
	private static <E extends Comparable<E>> void sort(E[] array, int start, int end, E[] buffer)
	{
		int midpoint = (start + end)/2;
		
		//if each side of the active part of the array is more than one element, recurse
		if( (midpoint - start) > 1) sort(array, start, midpoint, buffer);
		if ((end-midpoint) > 1) sort(array, midpoint, end, buffer);
		
		int bufferRange = midpoint-start; //buffer will make up the first half of the active part of the array
		System.arraycopy(array, start, buffer, 0, bufferRange); 
		
		int l = 0, r = midpoint; 
		
		//iterate through the entire active section of the array, merging the two halves
		for(int i = start; i < end; i++)
		{
			if(l == bufferRange) return;
			
			//if the current element of the array is smaller than the current element of the buffer, put it first
			if(r < end && array[r].compareTo(buffer[l]) < 0)
			{
				array[i] = array[r];
				r++;
			}
			//if the current element in the buffer is bigger, put that one first
			else{
				array[i] = buffer[l];
				l++;
			}
		}
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) 
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("Testing mergesort.");
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
		
		MergeSorter sorter = new MergeSorter();
		int sort = sorter.timeSort(size);
		System.out.println("Array of size " + size + " took " + sort + " ms to sort.");

	}
	

}
import java.util.Scanner;
class QuickSorter extends Sorter
{
	/**
	 * Sorts the provided array
	 * @param the array to be sorted
	 */
	@Override
	public <E extends Comparable<E>> void sort(E[] array) 
	{
		sort(array, 0, array.length-1);
	}
	
	//private sort method which takes more parameters
	private <E extends Comparable<E>> void sort(E[] array, int start, int end)
	{
		//pivot is chosen in the middle of the current section of the array
		E pivot = array[start + (end-start)/2];
		int left = start, right = end; //the left and right sides of the chosen pivot
		
		while(left <= right)
		{
			//if the element to the left of the pivot is smaller, move on to the next
			while(array[left].compareTo(pivot) < 0) left++;
			
			//if the element to the right of the pivot is bigger, move on to the next
			while(array[right].compareTo(pivot) > 0) right--;
			
			/* when the two out of order right and left elements are identified, 
			 * make sure the left is still to the left and the right is still to the right
			 * if everything is still in order, swap them and move on to the next element on each side.
			 */	
			if(left <= right)
			{	
				swap(array, left, right);
				left++;
				right--;
			}
		}
			//if the right and left sides didn't go out of bounds for the array, recurse
			if (start < right) sort(array, start, right);
			if (left < end) sort(array, left, end);
	}
	
	//swaps two elements
	private <E extends Comparable<E>> void swap(E[] array, int index1, int index2)
	{
		E temp = array[index1];
		array[index1] = array[index2];
		array[index2] = temp;
	}
	
	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("Testing quicksort.");
		System.out.print("Please enter the array size: ");
		String input = scan.nextLine();
		scan.close();
		int size = 0;
		
		try{
			size = Integer.parseInt(input);
		}
		catch(NumberFormatException e){
			System.out.println("Bad number. Exiting the program...");
			System.exit(1);
		}
		
		QuickSorter sorter = new QuickSorter();
		int sort = sorter.timeSort(size);
		System.out.println("Array of size " + size + " took " + sort + " ms to sort.");
	}

}

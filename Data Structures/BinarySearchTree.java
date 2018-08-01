import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
/**
 * An implementation of a binary search tree
 * @author Aaron Thompson
 * @param <E>
 */
class BinarySearchTree <E extends Comparable<E>> 
{
	Node root;
	int size;
	
	public BinarySearchTree()
	{
		root = new Node(null, null, null);
	}
	
	/**
	 * Inserts a value into the BinarySearchTree
	 * @param value The value that will be inserted
	 */
	public void insert(E value)
	{
		size++;
		
		if (root.key == null)
		{
			root.key = value;
			return;
		}
		
		Node currentNode = root;
		
		while (true)
		{
			//if the value is less or equal than the current node key, go left
			if (value.compareTo(currentNode.key) <= 0)
			{
				//when reaching the end of the tree, attach a new node with the value in question as the key
				if (currentNode.left == null)
				{
					currentNode.left = new Node(value, null, null);
					return;
				}
				
				//otherwise, step to the next left node
				else{
					currentNode = currentNode.left;
				}
			}
			
			//if the value is greater than the current node key, go right
			else{
				//when reaching the end of the tree, attach a new node
				if (currentNode.right == null)
				{
					currentNode.right = new Node(value, null, null);
					return;
				}
				
				//otherwise, step to the next right node
				else{
					currentNode = currentNode.right;
				}
			}
		}
	}
	
	/**
	 * Searches for a value in the BinarySearchTree
	 * @param value The value that is being searched for
	 * @return true if the value is found, false if not
	 */
	public boolean has(E value)
	{
		Node nextNode = root;
		
		while (nextNode != null)
		{
			//if the value has been found
			if (nextNode.key.compareTo(value) == 0) return true;
			
			//if the value is less than the nodes key, move on to the next left node
			else if (value.compareTo(nextNode.key) < 0)
			{
				nextNode = nextNode.left;
			}
			
			//if the value is greater than the nodes key, move on to the next right node
			else if (value.compareTo(nextNode.key) > 0)
			{
				nextNode = nextNode.right;
			}
		}
		return false;
	}
	
	/**
	 * Returns the minimum value in the BinarySearchTree
	 * @return the minimum value in the BinarySearchTree
	 */
	public E findMin()
	{
		Node currentNode = root;
		
		//go left until not possible
		while (currentNode.left != null)
		{
			currentNode = currentNode.left;
		}
		
		return currentNode.key;
	}
	
	/**
	 * Returns the maximum value in the BinarySearchTree
	 * @return the maximum value in the BinarySearchTree
	 */
	public E findMax()
	{
		Node currentNode = root;
		
		//go right until not possible
		while (currentNode.right != null)
		{
			currentNode = currentNode.right;
		}
		
		return currentNode.key;
	}
	
	/**
	 * Returns the size of the BinarySearchTree
	 * @return The size of the BinarySearchTree
	 */
	public int getSize()
	{
		return size;
	}

	private class Node
	{
		public E key;
		public Node left;
		public Node right;
		
		public Node(E key, Node left, Node right)
		{
			this.key = key;
			this.left = left;
			this.right = right;
		}
	}
	@SuppressWarnings({ "unchecked", "resource", "rawtypes" })
	public static void main(String[] args) 
	{
		String fileName = "english.lex";
		File file = null;
		Scanner scanner = null;
		BinarySearchTree tree = new BinarySearchTree();
		
		try{
			 file = new File(fileName);
			 throw new IOException();
		}
		catch (IOException e)
		{
			System.out.print("");
		}
		
		try{
			scanner = new Scanner(file);
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found.");
		}
		
		ArrayList<String> tempList = new ArrayList<String>();
		while (scanner.hasNextLine())
		{
			tempList.add(scanner.nextLine());
		}
		scanner.close();
		
		Collections.shuffle(tempList);
		String[] array = tempList.toArray(new String[0]);
		
		for (int i = 0; i < array.length; i++)
		{
			tree.insert(array[i]);
		}
		
		System.out.println("Testing BinarySearchTree");
		System.out.println("Loaded \"" + fileName + "\" (" + tree.getSize() + " words from \"" + tree.findMin() + "\" to \"" + tree.findMax() + "\")");
		System.out.println("Please enter a word, or hit enter to quit:");
		
		Scanner inputScanner = new Scanner(System.in);
		String word;
		
		while (true)
		{
			System.out.print("> ");
			word = inputScanner.nextLine();
			
			if (word.equals("")) 
			{
				System.out.println("Goodbye!");
				System.exit(0);
			}
			else if (tree.has(word))
			{
				System.out.println("\"" + word + "\" is a valid word." );
			}
			else if (!tree.has(word))
			{
				System.out.println("\"" + word + "\" is NOT a valid word." );
			}	
		}	
	}
}

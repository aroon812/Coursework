package trie;
import java.io.Serializable;

/**
 * An implementation of a Trie
 * @author Aaron Thompson
 * @version 4/23/2017
 */
class Trie implements Serializable{
	
	private static final long serialVersionUID = 5999250038291475527L;
	Node root;
	int size;
	
	public Trie()
	{
		root = new Node();
		size = 0;
	}
	
	/**
	 * Inserts a String into the Trie
	 * @param word the String to be inserted
	 */
	public void insert(String word)
	{
		Node currentNode = root;
		
		for (int i = 0; i < word.length(); i++)
		{
			//if the node has not yot been created for that specific letter, create it
			if (currentNode.array[word.charAt(i) - 'a'] == null) currentNode.array[word.charAt(i) - 'a'] = new Node();
			
			//if not at the ond of the word, move on to the next node, otherwise set the boolean isWord to true
			if (i < word.length()-1) currentNode = currentNode.array[word.charAt(i) - 'a'];
			else{
				currentNode.isWord = true;
			}
		}
		
		size++;
	}
	
	/**
	 * Checks to see if the Trie has a specific String
	 * @param word the String that is being looked for
	 * @return true if the Trie contains the String, false if it doesn't
	 */
	public boolean has(String word)
	{
		Node currentNode = root;
		for (int i = 0; i < word.length(); i++)
		{
			//if at the end of the string, if isWord is true, the word exists, so return true
			if (i == word.length()-1)
			{
				if (currentNode.isWord == true) return true;
			}
			
			//if the next letter in the word is null, return false
			if (currentNode.array[word.charAt(i) - 'a'] == null) return false;
			currentNode = currentNode.array[word.charAt(i) - 'a'];
		}
		return false;
	}
	
	/**
	 * Get the size of the Trie
	 * @return the size of the Trie
	 */
	public int getSize()
	{
		return size;
	}
	
	private class Node
	{
		@SuppressWarnings("unused")
		private static final long serialVersionUID = 4872789038291475527L;
		public boolean isWord;
		public Node[] array;
		
		public Node()
		{
			isWord = false;
			array = new Node[26];
		}
	}
	
	public static void main(String[] args)
	{
		Trie trie = new Trie();
		trie.insert("romulan");
		//trie.insert("romulan");
		System.out.println(trie.has("romulan"));
				
	}
}

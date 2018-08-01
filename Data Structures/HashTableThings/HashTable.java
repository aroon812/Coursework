import java.util.ArrayList;

/**
 * An implementation of a hash table
 * @author Aaron Thompson
 * @version 4/30/2017
 * @param <K>
 * @param <V>
 */
public class HashTable<K, V> {

	@SuppressWarnings("rawtypes")
	ArrayList<Node> nodeList;
	int size;
	
	 /**
	  * Makes a new HashTable
	  * @param tableSize the number of all of the nodes to be in the HashTable
	  */
	@SuppressWarnings("rawtypes")
	public HashTable(int tableSize)
	{
		nodeList = new ArrayList<Node>(tableSize);
		
		for (int i = 0; i < tableSize; i++)
		{
			nodeList.add(new Node());
		}
		
		size = 0;
	}
	
	/**
	 * Returns a key value pair from within the HashTable
	 * @param key the key for the key value pair
	 * @return the value for the key value pair
	 */
	@SuppressWarnings("unchecked")
	V get(K key)
	{
		return (V)nodeList.get(getIndex(key)).get(key);
	}
	
	/**
	 * Deletes a key value pair from the HashTable
	 * @param key the key tied to the key value pair
	 * @return if the deletion was successful or not
	 */
	@SuppressWarnings("unchecked")
	boolean delete(K key)
	{
		size--;
		return nodeList.get(getIndex(key)).delete(key);
	}
	
	/**
	 * Clears the HashTable
	 */
	public void clear()
	{
		//clear each node one at a time
		for (int i = 0; i < nodeList.size(); i++)
		{
			nodeList.get(i).clear();
		}
		
		size = 0;
	}
	
	/**
	 * Adds a key value pair to the HashTable
	 * @param key the key
	 * @param value the value
	 */
	@SuppressWarnings("unchecked")
	public void put(K key, V value)
	{
		//if the pair isn't already in the HashTable, add it 
		if (nodeList.get(getIndex(key)).get(key) == null)
		{
			nodeList.get(getIndex(key)).put(key, value);
		}
		//if the pair is in the HashTable, delete it and replace with a new one
		else{
			nodeList.get(getIndex(key)).delete(key);
			nodeList.get(getIndex(key)).put(key, value);
		}
		
		size++;
	}
	
	/**
	 * Returns the number of key value pairs in the HashTable
	 * @return the number of key value pairs in the HashTable
	 */
	public int getSize()
	{
		return size;
	}
	
	private int getIndex(K key)
	{
		int hash = key.hashCode() % nodeList.size();
		hash = Math.abs(hash);
		
		return hash;
	}
	
	@SuppressWarnings("hiding")
	private class Node<K, V> {
		
		ArrayList<K> kList;
		ArrayList<V> vList;
		
		Node()
		{
			kList = new ArrayList<K>();
			vList = new ArrayList<V>();	
		}
		
		public void put(K key, V value)
		{
			kList.add(key);
			vList.add(value);
		}
		
		public void clear()
		{
			kList.clear();
			vList.clear();
		}
		
		public boolean delete(K key)
		{
			for (int i = 0; i < kList.size(); i++)
			{
				if (kList.get(i).equals(key))
				{
					kList.remove(i);
					vList.remove(i);
					return true;
				}
			}
			return false;
		}
		
		public V get(K key)
		{
			for (int i = 0; i < kList.size(); i++)
			{
				if (kList.get(i).equals(key))
				{
					return vList.get(i);
				}
			}
			return null;
		}
		
	}
	
	public static void main(String[] args) 
	{
		HashTable<Integer, String> table = new HashTable<Integer, String>(7);
		System.out.println("Adding pair 1 and car");
		table.put(1, "car");
		System.out.println("The element with the key of 1 is " + table.get(1));
		System.out.println("Adding pair 2 and boat");
		table.put(2, "boat");
		System.out.println("The element with the key of 2 is " + table.get(2));
		System.out.println("Adding pair 3 and plane");
		table.put(3, "plane");
		System.out.println(table.getSize() + " key value pairs are currently in the HashTable");
		System.out.println("Deleting key value pair of 2 and boat");
		table.delete(2);
		System.out.println(table.getSize() + " key value pairs are currently in the HashTable");
		System.out.println("Clearing the HashTable");
		table.clear();
		System.out.println(table.getSize() + " key value pairs are currently in the HashTable");
		System.out.println("Attempting to return item with key 3");
		System.out.println("An item with the key of 3 is not in the HashTable, so the return value is " + table.get(3));
		System.out.println("Are we able to delete an item with the key of 5? (This should return false): " + table.delete(5));
	}

}

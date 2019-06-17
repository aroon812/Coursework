import java.util.ArrayList;

/**
 * An implementation of a Sequential Search Symbol Table
 * @author Aaron Thompson
 * @param <K>
 * @param <V>
 */
class SequentialSearchSymbolTable<K, V> {
	
	ArrayList<K> kList;
	ArrayList<V> vList;
	
	SequentialSearchSymbolTable()
	{
		kList = new ArrayList<K>();
		vList = new ArrayList<V>();	
	}
	
	/**
	 * Put a new key-value pair into the table
	 * @param key the key
	 * @param value the value
	 */
	public void put(K key, V value)
	{
		kList.add(key);
		vList.add(value);
	}
	
	/**
	 * Clears the table
	 */
	public void clear()
	{
		kList.clear();
		vList.clear();
	}
	
	/**
	 * Deletes a key-value pair from the table
	 * @param key the key with the pair to be deleted
	 * @return true if the pair was found and deleted, false if the pair was not in the table
	 */
	public boolean delete(K key)
	{
		//loop through kList in search of the key
		for(int i = 0; i < kList.size(); i++)
		{
			if(kList.get(i).equals(key))
			{
				kList.remove(i);
				vList.remove(i);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the value of a specified key
	 * @param key the key that corresponds with the desired value
	 * @return the value that is paired with the key
	 */
	public V get(K key)
	{
		//loop through kList in search of the key
		for(int i = 0; i < kList.size(); i++)
		{
			if(kList.get(i).equals(key))
			{
				return vList.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Returns the number of key-value pairs in the SSST
	 * @return the number of key-value pairs in the SSST
	 */
	public int getSize()
	{
		return kList.size();
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		@SuppressWarnings("rawtypes")
		SequentialSearchSymbolTable table = new SequentialSearchSymbolTable();
		System.out.println("placing pair \"1\" and \"apple\" into the SSST... ");
		table.put(1, "apple");
		System.out.println("placing pair \"2\" and \"orange\" into the SSST... ");
		table.put(2, "orange");
		System.out.println("The value for the key \"2\" is " + table.get(2));
		System.out.println("Clearing the SSST...");
		table.clear();
		System.out.println("placing pair \"3\" and \"avocado\" into the SSST... ");
		table.put(3, "avocado");
		System.out.println("The SSST currently has " + table.getSize() + " key-value pair(s).");
		System.out.println("Removing the key-value pair...");
		table.delete(3);
		System.out.println("The size if the SSST is now " + table.getSize());
	}
	
	
}

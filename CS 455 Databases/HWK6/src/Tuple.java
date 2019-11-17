import java.util.Hashtable;

/**
 * Creates A tuple that is stored in a relation based on a set of attributes.
 * @author Aaron Thompson
 *
 */
public class Tuple {
	Hashtable<String, String> values; 
	
	/**
	 * Create a new tuple
	 * @param attributes The list of attributes that the associated relation contains.
	 * @param attValues The list of values corresponding to the attributes.
	 */
	public Tuple(String[] attributes, String[] attValues) {
	
		values = new Hashtable<String, String>();
		for (int i = 0; i < attValues.length; i++) {
			values.put(attributes[i], attValues[i]);
		}
		
	}
	
	/**
	 * Returns the value of an attribute in the tuple
	 * @param attribute The attribute to get the value of.
	 * @return The value of the attribute.
	 */
	public String getAttributeValue(String attribute) {
		return values.get(attribute);
	}
}

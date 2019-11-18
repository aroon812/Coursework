import java.util.ArrayList;

/**
 * Creates A tuple that is stored in a relation based on a set of attributes.
 * @author Aaron Thompson
 *
 */
public class Tuple { 
	private ArrayList<Attribute> values; 
	
	/**
	 * Create a new tuple
	 * @param attributes The list of attributes that the associated relation contains.
	 * @param attValues The list of values corresponding to the attributes.
	 */
	public Tuple(String[] attributes, String[] attValues) {
		
		values = new ArrayList<Attribute>();
		for (int i = 0; i < attValues.length; i++) {
			values.add(new Attribute(attributes[i], attValues[i]));
		}
	}
	
	/**
	 * Join two tuples based on a common attribute
	 * @param t1 The first tuple
	 * @param t2 The second tuple
	 * @param commonAttribute The common attribute to join the tuples on.
	 */
	public Tuple(Tuple t1, Tuple t2, String commonAttribute) {
		t2.removeAttribute(commonAttribute); 
		values = new ArrayList<Attribute>();
		for (int i = 0; i < t1.numAttributes(); i++) {
			values.add(t1.getAttribute(i));
		}
		for (int j = 0; j < t2.numAttributes(); j++) {
			values.add(t2.getAttribute(j));
		}	
	}
	
	/**
	 * Returns the value of an attribute in the tuple
	 * @param i The index of the value associated with an attribute in the tuple.
	 * @return The value of the attribute.
	 */
	public String getAttributeValue(int i) {
		return values.get(i).getValue();
	}
	
	/**
	 * Returns the attribute at the ith location of the tuple.
	 * @param i the index specifying the attribute to be returned.
	 * @return The attribute from the specified index.
	 */
	public Attribute getAttribute(int i) {
		return values.get(i);
	}
	
	/**
	 * Returns the number of attributes in a tuple.
	 * @return the number of attributes in a tuple.
	 */
	public int numAttributes() {
		return values.size();
	}
	
	/**
	 * Add an attribute to a tuple
	 * @param name The name of the attribute.
	 * @param value The value of the attribute
	 */
	public void addAttribute(String name, String value) {
		values.add(new Attribute(name, name));
	}
	
	/**
	 * Remove an attribute from a tuple.
	 * @param attribute The attribute to be removed.
	 */
	public void removeAttribute(String attribute) {
		for (int i = 0; i < values.size(); i++) {
			if (values.get(i).getName().equals(attribute)) {
				values.remove(i);
			}
		}
	}
}

/**
 * Attribute class for fields in tuples.
 * @author Aaron Thompson
 *
 */
public class Attribute {
	String name;
	String value;
	
	/**
	 * Create new attribute
	 * @param name The name of the attribute
	 * @param value The value of the attribute in it's tuple.
	 */
	public Attribute(String name, String value) {
		this.name = name;
		this.value = value; 
	}
	
	/**
	 * Return the name of an attribute.
	 * @return The name of an attribute.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return the value of an attribute.
	 * @return The value of an attribute.
	 */
	public String getValue() {
		return value;
	}
}

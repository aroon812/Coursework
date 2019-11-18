import java.util.Comparator;

/**
 * Comparator class to establish a natural ordering of the tuples
 * @author Aaron Thompson
 *
 */
public class SortByAttribute implements Comparator<Tuple> {
	String attribute;
	Relation r;
	public SortByAttribute(Relation r ,String attribute) {
		this.r = r;
		this.attribute = attribute;
	}
	
	@Override
	/**
	 * Compares two tuples so a relation can be sorted.
	 */
	public int compare(Tuple arg0, Tuple arg1) {
		return arg0.getAttributeValue(r.getAttributeLocation(attribute)).compareToIgnoreCase(arg1.getAttributeValue(r.getAttributeLocation(attribute)));
	}
}

	
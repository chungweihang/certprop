package trust;

/**
 * This class provide a skeleton of a trust factory.
 * 
 * @author chang
 *
 */
public abstract class AbstractTrustFactory implements Propagatable {
	
	public AbstractTrustFactory() {}
	
	public abstract AbstractTrust parse(String source, String sink, String value);
	
	public String printTrustValue(AbstractTrust t) {
		return String.valueOf(t.value());
	}
	
}

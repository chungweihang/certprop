package trust;

public abstract class AbstractTrust implements Comparable<AbstractTrust> {

	private final String source;
	private final String sink;
	
	protected AbstractTrust(String source, String sink) {
		this.source = source;
		this.sink = sink;
	}
	
	public abstract double value(); 
	
	public String getSource() {
		return source;
	}

	public String getSink() {
		return sink;
	}
	
	@Override
	public String toString() {
		return String.format("%s to %s: %f", source, sink, value());
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AbstractTrust))
	         return false;
		AbstractTrust t = (AbstractTrust) o;
	      return t.source.equals(source) && t.sink.equals(sink);
	}
	
	@Override
	public int hashCode() {
		int result = 17;
        result = 31 * result + source.hashCode();
        result = 31 * result + sink.hashCode();
        
        return result;
	}

	@Override
	public int compareTo(AbstractTrust t) {
		return Double.compare(value(), t.value());
	}
}

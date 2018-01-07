package trust;

public class CertPropTrustFactory extends AbstractTrustFactory {

	// private final double max;
	// private final double min;
	
	public CertPropTrustFactory() {
		super();
	}
	
	/*
	public CertPropTrustFactory(double min, double max) {
		super();
		this.min = min;
		this.max = max;
	}
	
	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}
	*/

	/**
	 * Parse r, s
	 */
	@Override
	public CertPropTrust parse(String source, String sink, String value) {
		String[] tokens = value.split(",");
		if (tokens.length != 2) throw new IllegalArgumentException("wrong format \"r,s\" from " + source + " to " + sink + ": " + value);
		
		return new CertPropTrust(source, sink, Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
	}
	
	/**
	 * Print r, s
	 */
	@Override
	public String printTrustValue(AbstractTrust t) {
		if (t instanceof CertPropTrust) {
			CertPropTrust tt = (CertPropTrust) t;
			return String.format("%f,%f", tt.r, tt.s);
		}
		
		return null;
	}
	
	@Override
	public CertPropTrust concatenate(AbstractTrust t1, AbstractTrust t2) {
		if (t1 instanceof CertPropTrust && t2 instanceof CertPropTrust) {
			CertPropTrust tt2 = (CertPropTrust) t2;
			if (t1.getSink().equals(t2.getSource())) {
				double r = t1.value() * tt2.r;
				double s = t1.value() * tt2.s;
				// return new CertPropTrust(t1.getSource(), t2.getSink(), r, s, tt2.min, tt2.max);
				return new CertPropTrust(t1.getSource(), t2.getSink(), r, s);
			}
		}
		return null;
	}

	@Override
	public CertPropTrust aggregate(AbstractTrust t1, AbstractTrust t2) {
		if (t1 instanceof CertPropTrust && t2 instanceof CertPropTrust) {
			
			CertPropTrust tt1 = (CertPropTrust) t1;
			CertPropTrust tt2 = (CertPropTrust) t2;
			
			if (t1.getSource().equals(t2.getSource()) && t1.getSink().equals(t2.getSink())) {
				double newr = tt1.r + tt2.r;
				double news = tt1.s + tt2.s;
				//return new CertPropTrust(t1.getSource(), t1.getSink(), newr, news, tt1.min, tt1.max);
				return new CertPropTrust(t1.getSource(), t1.getSink(), newr, news);
			}
		}
		return null;
	}
	
	public static class CertPropTrust extends AbstractTrust {
		
		private final double r;
		private final double s;
		private final double c;
		
		/**
		 * 
		 * 
		 * @param source
		 * @param sink
		 * @param r
		 * @param s
		 */
		public CertPropTrust(String source, String sink, double r, double s) {
			super(source, sink);
			
			if ((r + s) > 1024d) {
				double p = probability(r, s);
				this.r = p * 1024d;
				this.s = p * 1024d;
			} else {
				this.r = r;
				this.s = s;
			}
			
			this.c = certainty(this.r, this.s);
		}
		
		@Override
		public double value() {
			// return c * probability(r, s);
			return probability(r, s);
			//return probability(r, s) * (max - min) + min;
		}
		
		@Override
		public String toString() {
			return String.format("%s to %s: <%f, %f> %f", getSource(), getSink(), r, s, value());
		}
		
		public double certainty() {
			return c;
		}

		private static double probability(double r, double s) {
			if ((r + s) == 0) return 0;
			return r/(r + s);
		}

		public static double certainty(double r, double s) {
			return certaintyIntegral(r, s, 10) / 2;
		}

		private static double certaintyIntegral(double r, double s, int steps) {
			double sum = 0;
			double p = probability(r, s);
			double q = certaintyBinomial(r, s, steps)/Math.pow(p,r)/Math.pow(1-p,s);
			double a1 = 0;
			double a2 = 0;
			double a3 = 0;
			double a4 = 0;
			double b0 = 0;
			double b1 = 0;
			double b2 = 0;
			double b3 = 0;
			int i =  steps;
			double a = 0;
			double b = 1;

			while (i > 0) {
				a4 = Math.pow(p,(double) i);
				double h = (a4 - a)/4;
				a1 = a + h;
				a2 = a + 2 * h;
				a3 = a4 - h;
				
				double f0 = Math.abs(Math.pow(a/p, r) * Math.pow((1-a)/(1-p), s) - q);
				double f1 = Math.abs(Math.pow(a1/p, r) * Math.pow((1-a1)/(1-p), s) - q);
				double f2 = Math.abs(Math.pow(a2/p, r) * Math.pow((1-a2)/(1-p), s) - q);
				double f3 = Math.abs(Math.pow(a3/p, r) * Math.pow((1-a3)/(1-p), s) - q);
				double f4 = Math.abs(Math.pow(a4/p, r) * Math.pow((1-a4)/(1-p), s) - q);
				
				sum += 2 * h * (7 * f0 + 32 * f1 + 12 * f2 + 32 * f3 + 7 * f4) / 45;
				b0 = 1 - Math.pow(1-p, (double) i);
				h = (b - b0)/4;
				b1 = b0 + h;
				b2 = b0 + 2 * h;
				b3 = b - h;
				
				f0 = Math.abs(Math.pow(b0/p, r) * Math.pow((1-b0)/(1-p), s) - q);
				f1 = Math.abs(Math.pow(b1/p, r) * Math.pow((1-b1)/(1-p), s) - q);
				f2 = Math.abs(Math.pow(b2/p, r) * Math.pow((1-b2)/(1-p), s) - q);
				f3 = Math.abs(Math.pow(b3/p, r) * Math.pow((1-b3)/(1-p), s) - q);
				f4 = Math.abs(Math.pow(b/p, r) * Math.pow((1-b)/(1-p), s) - q);
				
				sum += 2 * h * (7 * f0 + 32 * f1 + 12 * f2 + 32 * f3 + 7 * f4) / 45;
				a = a4;
				b = b0;
				i--; 

			}

			return Math.abs(sum / q);
		}

		private static double certaintyBinomial(double r, double s, int steps) {
			double sum = 0;
			double p = probability(r, s);
			double a1 = 0;
			double a2 = 0;
			double a3 = 0;
			double a4 = 0;
			double b0 = 0;
			double b1 = 0;
			double b2 = 0;
			double b3 = 0;
			int i = steps;
			double a = 0;
			double b = 1;

			while (i > 0) {
				a4 = Math.pow(p,(double) i);
				double h = (a4 - a)/4;
				a1 = a + h;
				a2 = a + 2 * h;
				a3 = a4 - h;
				
				double f0 = Math.pow(a/p, r) * Math.pow((1-a)/(1-p), s);
				double f1 = Math.pow(a1/p, r) * Math.pow((1-a1)/(1-p), s);
				double f2 = Math.pow(a2/p, r) * Math.pow((1-a2)/(1-p), s);
				double f3 = Math.pow(a3/p, r) * Math.pow((1-a3)/(1-p), s);
				double f4 = Math.pow(a4/p, r) * Math.pow((1-a4)/(1-p), s);
				
				sum += 2 * h * (7 * f0 + 32 * f1 + 12 * f2 + 32 * f3 + 7 * f4) / 45;
				b0 = 1 - Math.pow(1-p, (double) i);
				h = (b - b0)/4;
				b1 = b0 + h;
				b2 = b0 + 2 * h;
				b3 = b - h;
				
				f0 = Math.pow(b0/p, r) * Math.pow((1-b0)/(1-p), s);
				f1 = Math.pow(b1/p, r) * Math.pow((1-b1)/(1-p), s);
				f2 = Math.pow(b2/p, r) * Math.pow((1-b2)/(1-p), s);
				f3 = Math.pow(b3/p, r) * Math.pow((1-b3)/(1-p), s);
				f4 = Math.pow(b/p, r) * Math.pow((1-b)/(1-p), s);
				
				sum += 2 * h * (7 * f0 + 32 * f1 + 12 * f2 + 32 * f3 + 7 * f4) / 45;
				a = a4;
				b = b0;
				i--; 

			}

			return sum * Math.pow(p,r) * Math.pow(1-p,s);
		}
	}

	public static void main(String[] args) {
		AbstractTrustFactory factory = new CertPropTrustFactory();
		for (int i = 1000; i < 2000; i++) {
			System.out.println(i + " " + factory.parse("1", "2", String.format("%d,%d",i/2, i/2)).value());
		}
	}
}

package graph;

public class GraphObj {
	private String vOut;
	private String vIn;
	private String term;
	
	public GraphObj(String vOut, String vIn, String term) {
		super();
		this.vOut = vOut;
		this.vIn = vIn;
		this.term = term;
	}

	public String getvOut() {
		return vOut;
	}

	public void setvOut(String vOut) {
		this.vOut = vOut;
	}

	public String getvIn() {
		return vIn;
	}

	public void setvIn(String vIn) {
		this.vIn = vIn;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	@Override
	public String toString() {
		return  "(" + vOut + "," + vIn + "," + term
				+ ")";
	}
	
	@Override 
	public boolean equals(Object other) {
	    boolean result = false;
	    GraphObj comObj = (GraphObj) other;
	    result = this.getvOut().equals(comObj.getvOut()) &&
	    		this.getvIn().equals(comObj.getvIn()) &&
	    		this.getTerm().equals(comObj.getTerm());
	    return result;
	}
    @Override public int hashCode() {
        return 41;
    }
	
}

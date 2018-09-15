package nl.bve.rabobank.parser;

enum INVALID { DUPLICATE, WRONG_BALANCE, UNPARSEABLE }

final class FailedTransaction implements Comparable<FailedTransaction> {
	private String reference;
	private String description;
	private INVALID cause;
	
	FailedTransaction(String reference, String description, INVALID cause) {
		this.reference = reference;
		this.description = description;
		this.cause = cause;
	}
	
	@Override
	public int compareTo(FailedTransaction other) {
		int referenceCompare = reference.compareTo(other.reference);
		return (referenceCompare == 0 ? cause.compareTo(other.cause) : referenceCompare);
	}
	
	public String toString() {
		return cause+ ": " +reference+ "; " +description;
	}
}
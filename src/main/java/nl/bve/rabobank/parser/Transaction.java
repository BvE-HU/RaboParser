package nl.bve.rabobank.parser;

final class Transaction {
	final String reference;
	final String description;
	final String startBalance;
	final String mutation;
	final String endBalance;
	
	Transaction(String reference, String description, String startBalance, String mutation, String endBalance) {
		this.reference = reference;
		this.description = description;
		this.startBalance = startBalance;
		this.mutation = mutation;
		this.endBalance = endBalance;
	}
}

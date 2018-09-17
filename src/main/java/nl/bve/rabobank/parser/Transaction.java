package nl.bve.rabobank.parser;

final class Transaction {
	private String reference;
	private String description;
	private String startBalance;
	private String mutation;
	private String endBalance;
	
	Transaction() { }
	
	Transaction(String reference, String description, String startBalance, String mutation, String endBalance) {
		this.reference = reference;
		this.description = description;
		this.startBalance = startBalance;
		this.mutation = mutation;
		this.endBalance = endBalance;
	}
	
	String getReference() 		{ return reference; 	}
	String getDescription() 	{ return description; 	}
	String getStartBalance() 	{ return startBalance; 	}
	String getMutation() 		{ return mutation; 		}
	String getEndBalance() 		{ return endBalance; 	}
	
	void setReference(String reference) 		{ this.reference = reference; 		}
	void setDescription(String description) 	{ this.description = description; 	}
	void setStartBalance(String startBalance)	{ this.startBalance = startBalance; }
	void setMutation(String mutation) 			{ this.mutation = mutation; 		}
	void setEndBalance(String endBalance) 		{ this.endBalance = endBalance; 	}
}

package br.elibrary.model.enuns;

public enum LoanStatus {
	
	ACTIVE("Ativo"), 
	RETURNED("Devolvido"), 
	OVERDUE("Atrasado");
	
	private final String label;

	LoanStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public String toString() {
        return label;
    }
}

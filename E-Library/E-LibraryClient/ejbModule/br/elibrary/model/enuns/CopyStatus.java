package br.elibrary.model.enuns;

public enum CopyStatus {
	
	AVAILABLE("DISPONIVEL"), 
	BORROWED("EMPRESTADO"), 
	RESERVED("RESERVADO");

	private final String label;

	CopyStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public String toString() {
        return label;
    }
	
    public boolean isAvailable() {
        return this == AVAILABLE;
    }
}

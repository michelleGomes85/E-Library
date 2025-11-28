package br.elibrary.model.enuns;

public enum Rules {
	
	COMMON_USER("Usuário Comum"), 
	ADMIN("Administrador");
	
	private final String label;

	Rules(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public String toString() {
        return label;
    }
}

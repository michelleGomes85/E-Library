package br.elibrary.model.enuns;

public enum UserType {
	
	STUDENT("Estudante"), 
	TEACHER("Professor");
	
	private final String label;

	UserType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public String toString() {
        return label;
    }
}

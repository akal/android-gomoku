package org.team1337.checkers;

public enum FieldValue {
	EMPTY("_"),
	X("X"),
	O("O");
	
	String displayValue;
	FieldValue(String displayValue) {
		this.displayValue = displayValue;
	}
	public String getDisplayValue() {
		return displayValue;
	}
}

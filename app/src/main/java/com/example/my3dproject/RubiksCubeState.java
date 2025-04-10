package com.example.my3dproject;

public enum RubiksCubeState {

	IDLE(true),
	PAUSED(false),
	SOLVING(false),
	ROTATED_BY_PLAYER(false),
	SHUFFLE(false);

	private final boolean isAvailableForModifications;

	RubiksCubeState(boolean isAvailableForModifications) {
		this.isAvailableForModifications = isAvailableForModifications;
	}

	public boolean isAvailableForModifications() {
		return isAvailableForModifications;
	}

}

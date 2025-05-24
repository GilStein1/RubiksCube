package com.example.my3dproject;

/**
 * RubiksCubeState represents the various states that a Rubik's cube can be in during gameplay.
 * Each state determines whether the cube is available for user modifications (rotations by the player).
 */
public enum RubiksCubeState {

	/**
	 * IDLE - The cube is at rest and ready for user interaction.
	 * In this state, the user can freely rotate faces and manipulate the cube.
	 */
	IDLE(true),

	/**
	 * SOLVING - The cube is being automatically solved. (After hitting the reset button)
	 * User modifications are disabled to prevent interference with the solving process.
	 */
	SOLVING(false),

	/**
	 * ROTATED_BY_PLAYER - The cube is currently being rotated by the player.
	 * This state prevents additional modifications while an animation is in progress.
	 */
	ROTATED_BY_PLAYER(false),

	/**
	 * SHUFFLE - The cube is being shuffled automatically.
	 * User modifications are disabled during the shuffling process to ensure
	 * a proper random scramble without user interference.
	 */
	SHUFFLE(false);

	// Flag indicating whether user can modify the cube in this state
	private final boolean isAvailableForModifications;

	/**
	 * Constructor for each enum constant
	 * @param isAvailableForModifications true if user can interact with cube in this state
	 */
	RubiksCubeState(boolean isAvailableForModifications) {
		this.isAvailableForModifications = isAvailableForModifications;
	}

	/**
	 * Determines if the cube can be modified by the user in the current state
	 * @return true if user modifications are allowed, false otherwise
	 */
	public boolean isAvailableForModifications() {
		return isAvailableForModifications;
	}

}
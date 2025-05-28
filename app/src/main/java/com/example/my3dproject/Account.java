package com.example.my3dproject;

/**
 * Represents a user account that has a username, profile picture and information about his current game session
 * (rotations made by him, the timer and so on).
 */
public class Account {

	// The display name of the user
	public String name;

	// The profile picture of the account, encoded as a Base64 String
	public String profilePicture;

	// The user ID */
	public String userId;

	// The user's best completion time (lowest time achieved)
	public double bestTime;

	// Flag indicating whether the best score should be reset (for debugging purposes)
	public boolean resetBestScore;

	// Serialized string containing saved rotation data
	public String savedRotations;

	// Current timer value for the active session
	public double timer;

	// Timestamp when the game state was last saved
	public long timestampOfSave;

	/**
	 * Constructor to create a new account with user ID and name.
	 * Initializes all other fields to default values.
	 *
	 * @param userId ID of the user
	 * @param name Display name for the user
	 */
	public Account(String userId, String name) {
		this.userId = userId;
		this.name = name;
		this.profilePicture = "";
		this.bestTime = Double.MAX_VALUE; // Set to maximum value (no best time yet)
		this.resetBestScore = false;
		this.savedRotations = "";
		this.timer = 0;
		this.timestampOfSave = 0;
	}

	/**
	 * Default constructor that creates an empty account.
	 * All fields are initialized to default values.
	 */
	public Account() {
		this.name = "";
		this.profilePicture = "";
		this.userId = "";
		this.bestTime = Double.MAX_VALUE; // Set to maximum value (no best time yet)
		this.resetBestScore = false;
		this.savedRotations = "";
		this.timer = 0;
		this.timestampOfSave = 0;
	}

	/**
	 * @return The user's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the user's display name.
	 *
	 * @param name The new name for the user
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The profile picture
	 */
	public String getProfilePicture() {
		return profilePicture;
	}

	/**
	 * Sets the user's profile
	 *
	 * @param profilePicture The new profile picture
	 */
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	/**
	 * @return true if the best score should be reset, false otherwise
	 */
	public boolean isResetBestScore() {
		return resetBestScore;
	}

	/**
	 * @return The user ID
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the user's ID
	 *
	 * @param userId The user ID
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Gets the user's best completion time.
	 *
	 * @return The best completion time.
	 */
	public double getBestTime() {
		return bestTime;
	}

	/**
	 * Sets the user's best completion time.
	 *
	 * @param bestTime The new best time
	 */
	public void setBestTime(double bestTime) {
		this.bestTime = bestTime;
	}

	/**
	 * Checks if the best score should be reset.
	 *
	 * @return true if the best score should be reset, false otherwise
	 */
	public boolean shouldResetBestScore() {
		return resetBestScore;
	}

	/**
	 * Sets the flag for resetting the best score.
	 *
	 * @param shouldResetBestScore true to reset the best score, false otherwise
	 */
	public void setResetBestScore(boolean shouldResetBestScore) {
		this.resetBestScore = shouldResetBestScore;
	}

	/**
	 * Gets the saved rotation data.
	 *
	 * @return The saved rotations data
	 */
	public String getSavedRotations() {
		return savedRotations;
	}

	/**
	 * Sets the saved rotation data.
	 *
	 * @param savedRotations The rotation data to save
	 */
	public void setSavedRotations(String savedRotations) {
		this.savedRotations = savedRotations;
	}

	/**
	 * Gets the timestamp of when the game state was last saved.
	 *
	 * @return The timestamp of the last save
	 */
	public long getTimestampOfSave() {
		return timestampOfSave;
	}

	/**
	 * Sets the timestamp of when the game state was saved.
	 *
	 * @param timestampOfSave The timestamp of the save operation
	 */
	public void setTimestampOfSave(long timestampOfSave) {
		this.timestampOfSave = timestampOfSave;
	}

	/**
	 * Gets the current timer value.
	 *
	 * @return The current timer value
	 */
	public double getTimer() {
		return timer;
	}

	/**
	 * Sets the current timer value.
	 *
	 * @param timer The new timer value
	 */
	public void setTimer(double timer) {
		this.timer = timer;
	}

}
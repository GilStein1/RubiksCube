package com.example.my3dproject;

import java.util.ArrayList;
import java.util.List;

public class Account {

	public String name;
	public String userId;
	public List<RotationOperation> rotationOperations;

	public Account(String userId, String name) {
		this.userId = userId;
		this.name = name;
		this.rotationOperations = new ArrayList<>();
	}

	public Account() {
		this.name = "";
		this.userId = "";
		this.rotationOperations = new ArrayList<>();
	}

	public void setRotationOperationList(List<RotationOperation> rotationOperations) {
		this.rotationOperations = rotationOperations;
	}

	public List<RotationOperation> getRotationOperationList() {
		return rotationOperations;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}

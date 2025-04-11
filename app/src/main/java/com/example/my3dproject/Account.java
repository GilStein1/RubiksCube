package com.example.my3dproject;

import java.util.ArrayList;
import java.util.List;

public class Account {

	public String accountId;
	public String name;
	public String userId;
	public List<RotationOperation> rotationOperations;

	public Account(String userId, String name, String accountId) {
		this.userId = userId;
		this.name = name;
		this.accountId = accountId;
		this.rotationOperations = new ArrayList<>();
	}

	public Account() {
		this.accountId = "";
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

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
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

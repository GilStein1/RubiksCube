package com.example.my3dproject;

public class InputManager {

	private static InputManager instance;
	private final ScreenGeometryManager screenGeometryManager;
	private final ScreenTouchListener screenTouchListener;

	private InputManager() {
		this.screenGeometryManager = ScreenGeometryManager.getInstance();
		this.screenTouchListener = ScreenTouchListener.getInstance();
	}

	public static InputManager getInstance() {
		if(instance == null) {
			instance = new InputManager();
		}
		return instance;
	}

}

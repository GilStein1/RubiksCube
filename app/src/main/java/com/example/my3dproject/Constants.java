package com.example.my3dproject;

import com.example.my3dproject.math.Vec3D;
import com.example.my3dproject.math.geometry.Point3d;

/**
 * Constants class containing constants that are used in many of the classes.
 */
public class Constants {

	/**
	 * Environment-related constants that define the 3D world settings.
	 */
	public static class EnvironmentConstants {

		/**
		 * Direction vector for the primary light source in the 3D scene.
		 */
		public static final Vec3D LIGHT_DIRECTION = new Vec3D(-0.3, 0.1, -0.7).normalize();

		/**
		 * Initial position of the camera in 3D space.
		 */
		public static final Point3d PLAYER_POSITION = new Point3d(0, 0, -100);

	}

	/**
	 * The ideal screen width that I took from the emulator that I used while creating this app.
	 */
	public static final int IDEAL_SCREEN_WIDTH = 480;

	/**
	 * The camera's focal length.
	 */
	public static final double FOCAL_LENGTH = 450;

}
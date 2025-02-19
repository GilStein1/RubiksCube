package com.example.my3dproject;

import com.example.my3dproject.math.Vec3D;
import com.example.my3dproject.math.geometry.Point3d;

public class Constants {

	public static class EnvironmentConstants {

		public static final Vec3D LIGHT_DIRECTION = new Vec3D(-0.3, 0.1, -0.7).normalize();

		public static final Point3d PLAYER_POSITION = new Point3d(0, 0, -100);

	}

	public static final int IDEAL_SCREEN_WIDTH = 480;

	public static final double FOCAL_LENGTH = 450;

}

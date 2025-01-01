package com.example.my3dproject;

import com.example.my3dproject.math.Vec3D;
import com.example.my3dproject.math.geometry.Point3d;

public class Constants {

	public static class EnvironmentConstants {

		public static Vec3D LIGHT_DIRECTION = new Vec3D(1, -0.6, 0.7).normalize();

		public static Point3d PLAYER_POSITION = new Point3d(0, 0, 0);

	}

}

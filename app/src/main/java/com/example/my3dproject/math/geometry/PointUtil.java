package com.example.my3dproject.math.geometry;

import com.example.my3dproject.drawables.Point;

import java.util.ArrayList;
import java.util.List;

public class PointUtil {

	public static Point[] copyArrayOfPoints(Point[] arrOfPoints) {
		Point[] ret = new Point[arrOfPoints.length];
		for(int i = 0; i < arrOfPoints.length; i++) {
			ret[i] = new Point(arrOfPoints[i]);
		}
		return ret;
	}

	public static List<Point> copyListOfPoints(List<Point> listOfPoints) {
		List<Point> ret = new ArrayList<>();
		for(int i = 0; i < listOfPoints.size(); i++) {
			ret.add(new Point(listOfPoints.get(i)));
		}
		return ret;
	}

	public static double distance(Point3d p1, Point3d p2) {
		return Math.sqrt(Math.pow(p1.getX() - p2.getX(),2) + Math.pow(p1.getY() - p2.getY(),2) + Math.pow(p1.getZ() - p2.getZ(),2));
	}

}

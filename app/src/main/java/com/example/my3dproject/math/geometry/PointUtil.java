package com.example.my3dproject.math.geometry;

import com.example.my3dproject.drawables.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for actions with points
 */
public class PointUtil {

	/**
	 * A method for copying an array of points.
	 *
	 * @param arrOfPoints the array to copy from
	 * @return the copied array
	 */
	public static Point[] copyArrayOfPoints(Point[] arrOfPoints) {
		Point[] ret = new Point[arrOfPoints.length];
		for(int i = 0; i < arrOfPoints.length; i++) {
			ret[i] = new Point(arrOfPoints[i]);
		}
		return ret;
	}
	/**
	 * A method for copying list of points.
	 *
	 * @param listOfPoints the list to copy from
	 * @return the copied list
	 */
	public static List<Point> copyListOfPoints(List<Point> listOfPoints) {
		List<Point> ret = new ArrayList<>();
		for(int i = 0; i < listOfPoints.size(); i++) {
			ret.add(new Point(listOfPoints.get(i)));
		}
		return ret;
	}

	/**
	 * A method for calculating the distance between two points.
	 *
	 * @param p1 the first point
	 * @param p2 the second point
	 * @return the distance between the points
	 */
	public static double distance(Point3d p1, Point3d p2) {
		return Math.sqrt(Math.pow(p1.getX() - p2.getX(),2) + Math.pow(p1.getY() - p2.getY(),2) + Math.pow(p1.getZ() - p2.getZ(),2));
	}

}

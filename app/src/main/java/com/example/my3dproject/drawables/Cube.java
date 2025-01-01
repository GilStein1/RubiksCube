package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.graphics.Color;

import androidx.annotation.ColorInt;

import com.example.my3dproject.math.geometry.Point2d;
import com.example.my3dproject.math.geometry.PointUtils;

import java.util.ArrayList;
import java.util.List;

public class Cube extends Drawable {

	private final Point[] points3d;
	private final Point[] points3dToDraw;
	private final List<Polygon> polygons;

	public Cube(double screenWidth, double screenHeight, double x, double y, double z, double size) {
		super(screenWidth, screenHeight);
		double halfOfSize = size / 2;
		this.points3d = new Point[]{
			new Point(screenWidth, screenHeight, x + halfOfSize, y + halfOfSize, z + halfOfSize, 0),
			new Point(screenWidth, screenHeight, x + halfOfSize, y - halfOfSize, z + halfOfSize, 0),
			new Point(screenWidth, screenHeight, x - halfOfSize, y + halfOfSize, z + halfOfSize, 0),
			new Point(screenWidth, screenHeight, x - halfOfSize, y - halfOfSize, z + halfOfSize, 0),
			new Point(screenWidth, screenHeight, x + halfOfSize, y + halfOfSize, z - halfOfSize, 0),
			new Point(screenWidth, screenHeight, x + halfOfSize, y - halfOfSize, z - halfOfSize, 0),
			new Point(screenWidth, screenHeight, x - halfOfSize, y + halfOfSize, z - halfOfSize, 0),
			new Point(screenWidth, screenHeight, x - halfOfSize, y - halfOfSize, z - halfOfSize, 0)
		};
		this.points3dToDraw = PointUtils.copyArrayOfPoints(points3d);
		this.polygons = new ArrayList<>();
		polygons.add(new Polygon(screenWidth, screenHeight, Color.BLUE, points3dToDraw[0], points3dToDraw[1], points3dToDraw[3], points3dToDraw[2]));
		polygons.add(new Polygon(screenWidth, screenHeight, Color.BLUE, points3dToDraw[5], points3dToDraw[4], points3dToDraw[6], points3dToDraw[7]));
		polygons.add(new Polygon(screenWidth, screenHeight, Color.BLUE, points3dToDraw[0], points3dToDraw[4], points3dToDraw[5], points3dToDraw[1]));
		polygons.add(new Polygon(screenWidth, screenHeight, Color.BLUE, points3dToDraw[3], points3dToDraw[7], points3dToDraw[6], points3dToDraw[2]));
		polygons.add(new Polygon(screenWidth, screenHeight, Color.BLUE, points3dToDraw[1], points3dToDraw[5], points3dToDraw[7], points3dToDraw[3]));
		polygons.add(new Polygon(screenWidth, screenHeight, Color.BLUE, points3dToDraw[2], points3dToDraw[6], points3dToDraw[4], points3dToDraw[0]));
	}

	public Point[] getAll3dPoints() {
		return points3d;
	}

	public Point[] getAll3dPointsToDraw() {
		return points3dToDraw;
	}

	public List<Polygon> getAllPolygons() {
		return polygons;
	}

	@Override
	public void update(double deltaTime, Point2d pointOfClick, int event) {
		for(Polygon polygon : polygons) {
			polygon.update(deltaTime, pointOfClick, event);
		}
	}

	@Override
	public void render(Canvas canvas) {}
}

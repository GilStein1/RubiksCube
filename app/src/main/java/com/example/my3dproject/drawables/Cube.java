package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.my3dproject.ScreenGeometryManager;
import com.example.my3dproject.math.geometry.Point2d;
import com.example.my3dproject.math.geometry.Point3d;
import com.example.my3dproject.math.geometry.PointUtils;
import com.example.my3dproject.math.geometry.Quaternion;

import java.util.ArrayList;
import java.util.List;

public class Cube extends Drawable {

	private double x, y, z;
	private double drawnX, drawnY, drawnZ;
	private final Point[] points3d;
	private final Point[] points3dToDraw;
	private final List<Polygon> polygons;

	public Cube(double x, double y, double z, double size) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		double halfOfSize = size / 2;
		this.points3d = new Point[]{
			new Point(x + halfOfSize, y + halfOfSize, z + halfOfSize),
			new Point(x + halfOfSize, y - halfOfSize, z + halfOfSize),
			new Point(x - halfOfSize, y + halfOfSize, z + halfOfSize),
			new Point(x - halfOfSize, y - halfOfSize, z + halfOfSize),
			new Point(x + halfOfSize, y + halfOfSize, z - halfOfSize),
			new Point(x + halfOfSize, y - halfOfSize, z - halfOfSize),
			new Point(x - halfOfSize, y + halfOfSize, z - halfOfSize),
			new Point(x - halfOfSize, y - halfOfSize, z - halfOfSize)
		};
		this.points3dToDraw = PointUtils.copyArrayOfPoints(points3d);
		this.polygons = new ArrayList<>();
		polygons.add(new Polygon(this, Color.BLUE, points3dToDraw[0], points3dToDraw[1], points3dToDraw[3], points3dToDraw[2]));
		polygons.add(new Polygon(this, Color.BLUE, points3dToDraw[5], points3dToDraw[4], points3dToDraw[6], points3dToDraw[7]));
		polygons.add(new Polygon(this, Color.BLUE, points3dToDraw[0], points3dToDraw[4], points3dToDraw[5], points3dToDraw[1]));
		polygons.add(new Polygon(this, Color.BLUE, points3dToDraw[3], points3dToDraw[7], points3dToDraw[6], points3dToDraw[2]));
		polygons.add(new Polygon(this, Color.BLUE, points3dToDraw[1], points3dToDraw[5], points3dToDraw[7], points3dToDraw[3]));
		polygons.add(new Polygon(this, Color.BLUE, points3dToDraw[2], points3dToDraw[6], points3dToDraw[4], points3dToDraw[0]));
	}

	public void rotateX(double angle, Quaternion currentRotation) {
		Quaternion xRotation = Quaternion.fromAxisAngle(angle, 1, 0, 0);
		currentRotation = xRotation.multiply(currentRotation.inverse());
		double[][] rotationMatrix = currentRotation.toRotationMatrix();
		for (int i = 0; i < points3d.length; i++) {
			Point p = points3dToDraw[i];
			double tx = p.getX() - drawnX;
			double ty = p.getY() - drawnY;
			double tz = p.getZ() - drawnZ;
			double newX = rotationMatrix[0][0] * tx + rotationMatrix[0][1] * ty + rotationMatrix[0][2] * tz;
			double newY = rotationMatrix[1][0] * tx + rotationMatrix[1][1] * ty + rotationMatrix[1][2] * tz;
			double newZ = rotationMatrix[2][0] * tx + rotationMatrix[2][1] * ty + rotationMatrix[2][2] * tz;
			points3d[i].moveTo(newX + x, newY + y, newZ + z);
		}
//		for(Point p : points3d) {
//			double currentAngle = Math.atan2(p.getY() - y, p.getZ() - z);
//			currentAngle += angle;
//			double distance = Math.sqrt(Math.pow(p.getY() - y, 2) + Math.pow(p.getZ() - z, 2));
//			p.moveTo(p.getX(), Math.sin(currentAngle)*distance + y, Math.cos(currentAngle)*distance + z);
//		}
	}

	public void rotateY(double angle, Quaternion currentRotation) {
		Quaternion yRotation = Quaternion.fromAxisAngle(angle, 0, 1, 0);
		currentRotation = yRotation.multiply(currentRotation.inverse());
		double[][] rotationMatrix = currentRotation.toRotationMatrix();
		for (int i = 0; i < points3d.length; i++) {
			Point p = points3dToDraw[i];
			double tx = p.getX() - drawnX;
			double ty = p.getY() - drawnY;
			double tz = p.getZ() - drawnZ;
			double newX = rotationMatrix[0][0] * tx + rotationMatrix[0][1] * ty + rotationMatrix[0][2] * tz;
			double newY = rotationMatrix[1][0] * tx + rotationMatrix[1][1] * ty + rotationMatrix[1][2] * tz;
			double newZ = rotationMatrix[2][0] * tx + rotationMatrix[2][1] * ty + rotationMatrix[2][2] * tz;
			points3d[i].moveTo(newX + x, newY + y, newZ + z);
		}
//		for(Point p : points3d) {
//			double currentAngle = Math.atan2(p.getZ() - z, p.getX() - x);
//			currentAngle += angle;
//			double distance = Math.sqrt(Math.pow(p.getX() - x, 2) + Math.pow(p.getZ() - z, 2));
//			p.moveTo(Math.cos(currentAngle)*distance + x, p.getY(), Math.sin(currentAngle)*distance + z);
//		}
	}

	public void rotateZ(double angle, Quaternion currentRotation) {
		Quaternion zRotation = Quaternion.fromAxisAngle(angle, 0, 0, 1);
		currentRotation = zRotation.multiply(currentRotation.inverse());
		double[][] rotationMatrix = currentRotation.toRotationMatrix();
		for (int i = 0; i < points3d.length; i++) {
			Point p = points3dToDraw[i];
			double tx = p.getX() - drawnX;
			double ty = p.getY() - drawnY;
			double tz = p.getZ() - drawnZ;
			double newX = rotationMatrix[0][0] * tx + rotationMatrix[0][1] * ty + rotationMatrix[0][2] * tz;
			double newY = rotationMatrix[1][0] * tx + rotationMatrix[1][1] * ty + rotationMatrix[1][2] * tz;
			double newZ = rotationMatrix[2][0] * tx + rotationMatrix[2][1] * ty + rotationMatrix[2][2] * tz;
			points3d[i].moveTo(newX + x, newY + y, newZ + z);
		}
//		for(Point p : points3d) {
//			double currentAngle = Math.atan2(p.getY() - y, p.getX() - x);
//			currentAngle += angle;
//			double distance = Math.sqrt(Math.pow(p.getY() - y, 2) + Math.pow(p.getZ() - z, 2));
//			p.moveTo(Math.cos(currentAngle)*distance + x, Math.sin(currentAngle)*distance + y, p.getZ());
//		}
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

	public Point3d getPos() {
		return new Point3d(x, y, z);
	}

	public void updateDrawnPos(double x, double y, double z) {
		this.drawnX = x;
		this.drawnY = y;
		this.drawnZ = z;
	}

	public void updateDrawnPos(Point3d pos) {
		updateDrawnPos(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public void update(double deltaTime, Point2d pointOfClick, int event) {
		for(Polygon polygon : polygons) {
			polygon.update(deltaTime, pointOfClick, event);
		}
	}

	@Override
	public void render(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		canvas.drawCircle(
			(float) ScreenGeometryManager.getInstance().getProjectionTranslatedX(new Point3d(drawnX, drawnY, drawnZ)),
			(float) ScreenGeometryManager.getInstance().getProjectionTranslatedY(new Point3d(drawnX, drawnY, drawnZ)),
			10, paint);
	}
}

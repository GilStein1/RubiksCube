package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.my3dproject.ScreenGeometryManager;
import com.example.my3dproject.math.geometry.CubeColors;
import com.example.my3dproject.math.geometry.Point2d;
import com.example.my3dproject.math.geometry.Point3d;
import com.example.my3dproject.math.geometry.PointUtils;
import com.example.my3dproject.math.geometry.Quaternion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cube extends Drawable {

	private double x, y, z;
	private double drawnX, drawnY, drawnZ;
	private final Point[] points3d;
	private final Point[] points3dToDraw;
	private final List<Polygon> polygons;
	private final List<Polygon> polygonsToDraw;

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
		this.polygonsToDraw = new ArrayList<>();
		CubeColors colors = CubeColors.getColorsFromPos(x, y, z);

		polygons.add(new Polygon(this, colors.getColors()[5], points3d[0], points3d[1], points3d[3], points3d[2]));
		polygons.add(new Polygon(this, colors.getColors()[3], points3d[5], points3d[4], points3d[6], points3d[7]));
		polygons.add(new Polygon(this, colors.getColors()[2], points3d[0], points3d[4], points3d[5], points3d[1]));
		polygons.add(new Polygon(this, colors.getColors()[4], points3d[3], points3d[7], points3d[6], points3d[2]));
		polygons.add(new Polygon(this, colors.getColors()[1], points3d[1], points3d[5], points3d[7], points3d[3]));
		polygons.add(new Polygon(this, colors.getColors()[0], points3d[2], points3d[6], points3d[4], points3d[0]));

		polygonsToDraw.add(new Polygon(this, colors.getColors()[5], points3dToDraw[0], points3dToDraw[1], points3dToDraw[3], points3dToDraw[2]));
		polygonsToDraw.add(new Polygon(this, colors.getColors()[3], points3dToDraw[5], points3dToDraw[4], points3dToDraw[6], points3dToDraw[7]));
		polygonsToDraw.add(new Polygon(this, colors.getColors()[2], points3dToDraw[0], points3dToDraw[4], points3dToDraw[5], points3dToDraw[1]));
		polygonsToDraw.add(new Polygon(this, colors.getColors()[4], points3dToDraw[3], points3dToDraw[7], points3dToDraw[6], points3dToDraw[2]));
		polygonsToDraw.add(new Polygon(this, colors.getColors()[1], points3dToDraw[1], points3dToDraw[5], points3dToDraw[7], points3dToDraw[3]));
		polygonsToDraw.add(new Polygon(this, colors.getColors()[0], points3dToDraw[2], points3dToDraw[6], points3dToDraw[4], points3dToDraw[0]));
	}

	public void rotateWithMatrix(double[][] matrix, Point3d centerOfRotation) {
		for (int i = 0; i < points3d.length; i++) {
			Point p = points3d[i];
			double tx = p.getX() - centerOfRotation.getX();
			double ty = p.getY() - centerOfRotation.getY();
			double tz = p.getZ() - centerOfRotation.getZ();
			double finalX = matrix[0][0] * tx + matrix[0][1] * ty + matrix[0][2] * tz + centerOfRotation.getX();
			double finalY = matrix[1][0] * tx + matrix[1][1] * ty + matrix[1][2] * tz + centerOfRotation.getY();
			double finalZ = matrix[2][0] * tx + matrix[2][1] * ty + matrix[2][2] * tz + centerOfRotation.getZ();
			points3d[i].moveTo(finalX, finalY, finalZ);
		}
		double tx = x - centerOfRotation.getX();
		double ty = y - centerOfRotation.getY();
		double tz = z - centerOfRotation.getZ();
		x = matrix[0][0] * tx + matrix[0][1] * ty + matrix[0][2] * tz + centerOfRotation.getX();
		y = matrix[1][0] * tx + matrix[1][1] * ty + matrix[1][2] * tz + centerOfRotation.getY();
		z = matrix[2][0] * tx + matrix[2][1] * ty + matrix[2][2] * tz + centerOfRotation.getZ();
	}

	public void rotateX(double angle, Point3d centerOfRotation) {
		double[][] matrix = Quaternion.fromAxisAngle(angle, 1, 0, 0).toRotationMatrix();
		rotateWithMatrix(matrix, centerOfRotation);
	}

	public void rotateY(double angle, Point3d centerOfRotation) {
		double[][] matrix = Quaternion.fromAxisAngle(angle, 0, 1, 0).toRotationMatrix();
		rotateWithMatrix(matrix, centerOfRotation);
	}

	public void rotateZ(double angle, Point3d centerOfRotation) {
		double[][] matrix = Quaternion.fromAxisAngle(angle, 0, 0, 1).toRotationMatrix();
		rotateWithMatrix(matrix, centerOfRotation);
	}

	public Point[] getAll3dPoints() {
		return points3d;
	}

	public Point[] getAll3dPointsToDraw() {
		return points3dToDraw;
	}

	public List<Polygon> getAllPolygons() {
		return polygonsToDraw;
	}

	public Point3d getPos() {
		return new Point3d(x, y, z);
	}

	public Polygon getPolygonFromDrawPolygon(Polygon drawPolygon) {
		return polygons.get(polygonsToDraw.indexOf(drawPolygon));
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
		for (Polygon polygon : polygonsToDraw) {
			polygon.update(deltaTime, pointOfClick, event);
		}
	}

	@Override
	public void render(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(30);
		canvas.drawText("(" + ((int) (x * 10) / 10.0) + "," + ((int) (y * 10) / 10.0) + "," + ((int) (z * 1000) / 1000.0) + ")",
			(float) ScreenGeometryManager.getInstance().getProjectionTranslatedX(new Point3d(drawnX, drawnY, drawnZ)) - 100,
			(float) ScreenGeometryManager.getInstance().getProjectionTranslatedY(new Point3d(drawnX, drawnY, drawnZ)),
			paint
		);
//		canvas.drawCircle(
//			(float) ScreenGeometryManager.getInstance().getProjectionTranslatedX(new Point3d(drawnX, drawnY, drawnZ)),
//			(float) ScreenGeometryManager.getInstance().getProjectionTranslatedY(new Point3d(drawnX, drawnY, drawnZ)),
//			10, paint);
	}
}

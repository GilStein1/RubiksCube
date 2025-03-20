package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.ColorInt;

import com.example.my3dproject.Constants;
import com.example.my3dproject.ScreenGeometryManager;
import com.example.my3dproject.math.Vec3D;
import com.example.my3dproject.math.geometry.Point2d;
import com.example.my3dproject.math.geometry.Point3d;
import com.example.my3dproject.math.geometry.PointUtils;

import java.util.Arrays;
import java.util.List;

public class Polygon extends Drawable {

	private final Cube parentCube;
	@ColorInt
	private final int color;
	private final List<Point> points;
	private final ScreenGeometryManager screenGeometryManager;
	private Path pathOfPolygon;
	private Path pathOfLines;
	private Paint paintOfFilledShape;
	private Vec3D normalVector;
	private boolean isSelected;

	public Polygon(Cube cube, @ColorInt int color, Point... points) {
		super();
		this.parentCube = cube;
		this.color = color;
		this.points = Arrays.asList(points);
		this.screenGeometryManager = ScreenGeometryManager.getInstance();
		this.pathOfPolygon = new Path();
		this.pathOfLines = new Path();
		this.paintOfFilledShape = new Paint();
		this.normalVector = updateNormalVector();
		this.isSelected = false;
	}

	public Cube getParentCube() {
		return parentCube;
	}

	public boolean isPointingToX() {
		return Math.abs(points.get(0).getX() - points.get(1).getX()) < 10;
	}

	public boolean isPointingToY() {
		return Math.abs(points.get(0).getY() - points.get(1).getY()) < 10;
	}

	public boolean isPointingToZ() {
		return Math.abs(points.get(0).getZ() - points.get(1).getZ()) < 10;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isPointingToPlayer() {
		Point3d middle = getMiddleOfPolygon();
		Point3d playerPos = Constants.EnvironmentConstants.PLAYER_POSITION;
		Vec3D playerDirection = Vec3D.fromDifferenceInPos(middle, playerPos);
		playerDirection.normalize();
		Vec3D normalVector = updateNormalVector();
		return (playerDirection.dotProduct(normalVector) < 0);
	}

	public boolean isPointInPolygon(Point2d point) {
		int intersections = 0;
		int n = points.size();
		for (int i = 0; i < n; i++) {
			Point2d p1 = points.get(i).getLastDrawnPoint();
			Point2d p2 = points.get((i + 1) % n).getLastDrawnPoint();
			if ((point.getY() > Math.min(p1.getY(), p2.getY())) &&
				(point.getY() <= Math.max(p1.getY(), p2.getY())) &&
				(point.getX() <= Math.max(p1.getX(), p2.getX()))) {
				double xIntersection = p1.getX() + (point.getY() - p1.getY()) *
					(p2.getX() - p1.getX()) / (p2.getY() - p1.getY());

				if (xIntersection > point.getX()) {
					intersections++;
				}
			}
		}
		return (intersections % 2 != 0);
	}

	public double getDistanceFromPlayer() {
		Point3d middle = getMiddleOfPolygon();
		Point3d player = Constants.EnvironmentConstants.PLAYER_POSITION;
		return PointUtils.distance(middle, player);
	}

	private Point3d getMiddleOfPolygon() {
		double xAvg = 0;
		double yAvg = 0;
		double zAvg = 0;
		for(Point point : points) {
			xAvg += point.getX();
			yAvg += point.getY();
			zAvg += point.getZ();
		}
		xAvg /= points.size();
		yAvg /= points.size();
		zAvg /= points.size();
		return new Point3d(xAvg, yAvg, zAvg);
	}

	private Vec3D updateNormalVector() {
		Vec3D vector = new Vec3D(0, 0, 0);
		vector.setX((points.get(0).getY() - points.get(1).getY()) * (points.get(0).getZ() - points.get(2).getZ()) - (points.get(0).getZ() - points.get(1).getZ()) * (points.get(0).getY() - points.get(2).getY()));
		vector.setY((points.get(0).getZ() - points.get(1).getZ()) * (points.get(0).getX() - points.get(2).getX()) - (points.get(0).getX() - points.get(1).getX()) * (points.get(0).getZ() - points.get(2).getZ()));
		vector.setZ((points.get(0).getX() - points.get(1).getX()) * (points.get(0).getY() - points.get(2).getY()) - (points.get(0).getY() - points.get(1).getY()) * (points.get(0).getX() - points.get(2).getX()));
		vector.normalize();
		return vector;
	}

	@Override
	public void update(double deltaTime, Point2d pointOfClick, int event) {
		normalVector = updateNormalVector();
		paintOfFilledShape.setColor(calculateColorWithShade(color));
		pathOfPolygon = new Path();
		pathOfLines = new Path();
		pathOfPolygon.moveTo(
			(float) screenGeometryManager.getProjectionTranslatedX(points.get(0).getPose()),
			(float) screenGeometryManager.getProjectionTranslatedY(points.get(0).getPose())
		);
		pathOfLines.moveTo(
			(float) screenGeometryManager.getProjectionTranslatedX(points.get(0).getPose()),
			(float) screenGeometryManager.getProjectionTranslatedY(points.get(0).getPose())
		);
		for (Point dot : points) {
			pathOfPolygon.lineTo(
				(float) screenGeometryManager.getProjectionTranslatedX(dot.getPose()),
				(float) screenGeometryManager.getProjectionTranslatedY(dot.getPose())
			);
			pathOfLines.lineTo(
				(float) screenGeometryManager.getProjectionTranslatedX(dot.getPose()),
				(float) screenGeometryManager.getProjectionTranslatedY(dot.getPose())
			);
		}
		pathOfPolygon.close();
		pathOfLines.close();
	}

	private int calculateColorWithShade(int color) {
		int red = (color >> 16) & 0xFF;
		int green = (color >> 8) & 0xFF;
		int blue = color & 0xFF;
		Vec3D lightDirection = Constants.EnvironmentConstants.LIGHT_DIRECTION;
		double dotProduct =
			normalVector.getX() * lightDirection.getX() +
				normalVector.getY() * lightDirection.getY() +
				normalVector.getZ() * lightDirection.getZ();
		dotProduct++;
		dotProduct /= 2;
		dotProduct = Math.sqrt(dotProduct);
		return Color.rgb(
			(int) (red * (dotProduct)),
			(int) (green * (dotProduct)),
			(int) (blue * (dotProduct))
		);
	}

	@Override
	public void render(Canvas canvas) {
		paintOfFilledShape.setStyle(Paint.Style.FILL);
		Paint paintOfLines = new Paint();
		paintOfLines.setColor(isSelected? Color.LTGRAY : Color.BLACK);
		paintOfLines.setStrokeWidth((float) (3*ScreenGeometryManager.getInstance().getScreenSizeRatio()));
		paintOfLines.setAntiAlias(false);
		paintOfLines.setStyle(Paint.Style.STROKE);
		isSelected = false;
		canvas.drawPath(pathOfPolygon, paintOfFilledShape);
		canvas.drawPath(pathOfLines, paintOfLines);
	}
}

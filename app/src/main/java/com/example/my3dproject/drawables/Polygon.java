package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.ColorInt;

import com.example.my3dproject.Constants;
import com.example.my3dproject.math.Vec3D;
import com.example.my3dproject.math.geometry.Point2d;
import com.example.my3dproject.math.geometry.Point3d;
import com.example.my3dproject.math.geometry.PointUtils;

import java.util.Arrays;
import java.util.List;

public class Polygon extends Drawable {

	@ColorInt
	private final int color;
	private final List<Point> points;
	private Path pathOfPolygon;
	private Paint paint;
	private Vec3D normalVector;

	public Polygon(double screenWidth, double screenHeight, @ColorInt int color, Point... points) {
		super(screenWidth, screenHeight);
		this.color = color;
		this.points = Arrays.asList(points);
		this.pathOfPolygon = new Path();
		this.paint = new Paint();
		this.normalVector = updateNormalVector();
	}

	public boolean isPointingToPlayer() {
		Point3d middle = getMiddleOfPolygon();
		Point3d playerPos = Constants.EnvironmentConstants.PLAYER_POSITION;
		Vec3D playerDirection = Vec3D.fromDifferenceInPos(middle, playerPos);
		playerDirection.normalize();
		Vec3D normalVector = updateNormalVector();
		return playerDirection.dotProduct(normalVector) > -0.1;
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
		paint.setColor(calculateColorWithShade(color));
		pathOfPolygon = new Path();
		pathOfPolygon.moveTo((float) points.get(0).getProjectionTranslatedX(), (float) points.get(0).getProjectionTranslatedY());
		for (Point dot : points) {
			pathOfPolygon.lineTo((float) dot.getProjectionTranslatedX(), (float) dot.getProjectionTranslatedY());
		}
		pathOfPolygon.close();
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
		paint.setStyle(Paint.Style.FILL);
		Paint paintOfLines = new Paint();
		paintOfLines.setColor(Color.BLACK);
		paintOfLines.setStrokeWidth(10);
		for(int i = 0; i < points.size(); i++) {
			canvas.drawLine(
				(float) points.get(i).getProjectionTranslatedX(),
				(float) points.get(i).getProjectionTranslatedY(),
				(float) points.get((i + 1)%points.size()).getProjectionTranslatedX(),
				(float) points.get((i + 1)%points.size()).getProjectionTranslatedY(),
				paintOfLines
			);
		}
		canvas.drawPath(pathOfPolygon, paint);
	}
}

package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.ColorInt;

import com.example.my3dproject.Constants;
import com.example.my3dproject.ScreenGeometryManager;
import com.example.my3dproject.UpdatableComponent;
import com.example.my3dproject.math.Vec3D;
import com.example.my3dproject.math.geometry.Point2d;
import com.example.my3dproject.math.geometry.Point3d;
import com.example.my3dproject.math.geometry.PointUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BigPolygon extends Polygon implements UpdatableComponent {

	private List<Polygon> smallPolygons;
	private Point[][] pointsOfSmallPolygons;
	private boolean isSelected;

	public BigPolygon(Cube cube, @ColorInt int color, Point... points) {
		super(cube, color, points);
		this.smallPolygons = new ArrayList<>();
		this.pointsOfSmallPolygons = new Point[3][3];
		this.isSelected = false;

		if(points.length == 4) {

			pointsOfSmallPolygons[0][0] = points[0];
			pointsOfSmallPolygons[pointsOfSmallPolygons.length-1][0] = points[1];
			pointsOfSmallPolygons[pointsOfSmallPolygons.length-1][pointsOfSmallPolygons[0].length-1] = points[2];
			pointsOfSmallPolygons[0][pointsOfSmallPolygons[0].length-1] = points[3];

			Vec3D dir = Vec3D.fromDifferenceInPos(points[1], points[0]).normalize();
			double distance = points[1].getDistanceFrom(points[0]);
			for(int i = 0; i < pointsOfSmallPolygons.length-2; i++) {
				double x = pointsOfSmallPolygons[i][0].getX() + dir.getX()*distance/(pointsOfSmallPolygons.length-1);
				double y = pointsOfSmallPolygons[i][0].getY() + dir.getY()*distance/(pointsOfSmallPolygons.length-1);
				double z = pointsOfSmallPolygons[i][0].getZ() + dir.getZ()*distance/(pointsOfSmallPolygons.length-1);
				pointsOfSmallPolygons[i+1][0] = new Point(x, y, z);
			}
			dir = Vec3D.fromDifferenceInPos(points[3], points[2]).normalize();
			distance = points[3].getDistanceFrom(points[2]);
			for(int i = 0; i < pointsOfSmallPolygons.length-2; i++) {
				double x = pointsOfSmallPolygons[i][pointsOfSmallPolygons.length-1].getX() + dir.getX()*distance/(pointsOfSmallPolygons.length-1);
				double y = pointsOfSmallPolygons[i][pointsOfSmallPolygons.length-1].getY() + dir.getY()*distance/(pointsOfSmallPolygons.length-1);
				double z = pointsOfSmallPolygons[i][pointsOfSmallPolygons.length-1].getZ() + dir.getZ()*distance/(pointsOfSmallPolygons.length-1);
				pointsOfSmallPolygons[i+1][pointsOfSmallPolygons.length-1] = new Point(x, y, z);
			}
			for(int i = 0; i < pointsOfSmallPolygons.length; i++) {
				dir = Vec3D.fromDifferenceInPos(pointsOfSmallPolygons[i][pointsOfSmallPolygons.length-1], pointsOfSmallPolygons[i][0]).normalize();
				distance = pointsOfSmallPolygons[i][pointsOfSmallPolygons.length-1].getDistanceFrom(pointsOfSmallPolygons[i][0]);
				for(int j = 0; j < pointsOfSmallPolygons.length-2; j++) {
					double x = pointsOfSmallPolygons[i][j].getX() + dir.getX()*distance/(pointsOfSmallPolygons.length-1);
					double y = pointsOfSmallPolygons[i][j].getY() + dir.getY()*distance/(pointsOfSmallPolygons.length-1);
					double z = pointsOfSmallPolygons[i][j].getZ() + dir.getZ()*distance/(pointsOfSmallPolygons.length-1);
					pointsOfSmallPolygons[i][j+1] = new Point(x, y, z);
				}
			}

			for(int i = 0; i < pointsOfSmallPolygons.length-1; i++) {
				for(int j = 0; j < pointsOfSmallPolygons[0].length-1; j++) {
					smallPolygons.add(new Polygon(cube, color, pointsOfSmallPolygons[i][j], pointsOfSmallPolygons[i + 1][j], pointsOfSmallPolygons[i + 1][j + 1], pointsOfSmallPolygons[i][j + 1]));
				}
			}

		}

	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public List<Polygon> getAllSmallPolygons() {
		return smallPolygons;
	}

	@Override
	public void updatePoints() {
		for(Polygon polygon : smallPolygons) {
			polygon.updatePoints();
		}
	}

	@Override
	public void update(double deltaTime, Point2d pointOfClick, int event) {
		super.update(deltaTime, pointOfClick, event);
		updatePoints();
		if(points.size() == 4) {

			Vec3D dir = Vec3D.fromDifferenceInPos(pointsOfSmallPolygons[pointsOfSmallPolygons.length-1][0], pointsOfSmallPolygons[0][0]).normalize();
			double distance = pointsOfSmallPolygons[pointsOfSmallPolygons.length-1][0].getDistanceFrom(pointsOfSmallPolygons[0][0]);
			for(int i = 0; i < pointsOfSmallPolygons.length-2; i++) {
				double x = pointsOfSmallPolygons[i][0].getX() + dir.getX()*distance/(pointsOfSmallPolygons.length-1);
				double y = pointsOfSmallPolygons[i][0].getY() + dir.getY()*distance/(pointsOfSmallPolygons.length-1);
				double z = pointsOfSmallPolygons[i][0].getZ() + dir.getZ()*distance/(pointsOfSmallPolygons.length-1);
				pointsOfSmallPolygons[i+1][0].moveTo(x, y, z);
			}
			dir = Vec3D.fromDifferenceInPos(pointsOfSmallPolygons[pointsOfSmallPolygons.length-1][pointsOfSmallPolygons[0].length-1], pointsOfSmallPolygons[0][pointsOfSmallPolygons[0].length-1]).normalize();
			distance = pointsOfSmallPolygons[pointsOfSmallPolygons.length-1][pointsOfSmallPolygons[0].length-1].getDistanceFrom(pointsOfSmallPolygons[0][pointsOfSmallPolygons[0].length-1]);
			for(int i = 0; i < pointsOfSmallPolygons.length-2; i++) {
				double x = pointsOfSmallPolygons[i][pointsOfSmallPolygons.length-1].getX() + dir.getX()*distance/(pointsOfSmallPolygons.length-1);
				double y = pointsOfSmallPolygons[i][pointsOfSmallPolygons.length-1].getY() + dir.getY()*distance/(pointsOfSmallPolygons.length-1);
				double z = pointsOfSmallPolygons[i][pointsOfSmallPolygons.length-1].getZ() + dir.getZ()*distance/(pointsOfSmallPolygons.length-1);
				pointsOfSmallPolygons[i+1][pointsOfSmallPolygons.length-1].moveTo(x, y, z);
			}
			for(int i = 0; i < pointsOfSmallPolygons.length; i++) {
				dir = Vec3D.fromDifferenceInPos(pointsOfSmallPolygons[i][pointsOfSmallPolygons.length-1], pointsOfSmallPolygons[i][0]).normalize();
				distance = pointsOfSmallPolygons[i][pointsOfSmallPolygons.length-1].getDistanceFrom(pointsOfSmallPolygons[i][0]);
				for(int j = 0; j < pointsOfSmallPolygons.length-2; j++) {
					double x = pointsOfSmallPolygons[i][j].getX() + dir.getX()*distance/(pointsOfSmallPolygons.length-1);
					double y = pointsOfSmallPolygons[i][j].getY() + dir.getY()*distance/(pointsOfSmallPolygons.length-1);
					double z = pointsOfSmallPolygons[i][j].getZ() + dir.getZ()*distance/(pointsOfSmallPolygons.length-1);
					pointsOfSmallPolygons[i][j+1].moveTo(x, y, z);
				}
			}

		}
	}

	@Override
	public void render(Canvas canvas, boolean isDarkMode) {

		Paint paintOfLines = new Paint();
		paintOfLines.setColor(isSelected? Color.LTGRAY : Color.BLACK);
		paintOfLines.setStrokeWidth((float) (3*ScreenGeometryManager.getInstance().getScreenSizeRatio()));
		paintOfLines.setAntiAlias(false);
		paintOfLines.setStyle(Paint.Style.STROKE);
		isSelected = false;
		canvas.drawPath(pathOfLines, paintOfLines);
//		for(int i = 0; i < pointsOfSmallPolygons.length; i++) {
//			for(int j = 0; j < pointsOfSmallPolygons[0].length; j++) {
//				if(pointsOfSmallPolygons[i][j] != null) {
//					pointsOfSmallPolygons[i][j].render(canvas, isDarkMode);
//				}
//			}
//		}
		for(Polygon polygon : smallPolygons) {
//			polygon.updatePoints();
//			polygon.render(canvas, isDarkMode);
		}
	}

}

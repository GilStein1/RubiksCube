package com.example.my3dproject.drawables;

import android.graphics.Canvas;

import com.example.my3dproject.RotationOperation;
import com.example.my3dproject.math.Vec3D;
import com.example.my3dproject.math.geometry.Point3d;
import com.example.my3dproject.math.geometry.Quaternion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class RubiksCube extends Drawable {

	private final double x, y, z;
	private final List<Cube> cubes;
	private final List<Cube> cubesThatDoNotRotate;
	private final List<Point> points3d;
	private final List<Point> notRotatedPoints3d;
	private final List<Point> points3dToDraw;
	private final List<Point> notRotatedPointsToDraw;
	private final List<BigPolygon> drawnPolygons;
	private final List<Polygon> smallPolygons;
	private final List<BigPolygon> notRotatedPolygons;
	private Quaternion currentRotation;
	private final double rubiksCubeSize;
	private final double smallCubesSize;

	public RubiksCube(double x, double y, double z, double size) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.rubiksCubeSize = size;
		this.points3d = new ArrayList<>();
		this.notRotatedPoints3d = new ArrayList<>();
		this.points3dToDraw = new ArrayList<>();
		this.notRotatedPointsToDraw = new ArrayList<>();
		this.drawnPolygons = new ArrayList<>();
		this.smallPolygons = new ArrayList<>();
		this.notRotatedPolygons = new ArrayList<>();
		this.cubes = new ArrayList<>();
		this.cubesThatDoNotRotate = new ArrayList<>();
		double sizeOfSmallCubes = size / 3;
		this.smallCubesSize = sizeOfSmallCubes;
		this.currentRotation = new Quaternion(1, 0, 0, 0);
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));

		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));

		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));

		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));

		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));

		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));

		for (Cube cube : cubes) {
			points3d.addAll(Arrays.asList(cube.getAll3dPoints()));
			points3dToDraw.addAll(Arrays.asList(cube.getAll3dPointsToDraw()));
			drawnPolygons.addAll(cube.getAllPolygons());
		}

		for(BigPolygon bigPolygon : drawnPolygons) {
			smallPolygons.addAll(bigPolygon.getAllSmallPolygons());
		}

		for(Cube cube : cubesThatDoNotRotate) {
			notRotatedPolygons.addAll(cube.getAllPolygons());
			notRotatedPoints3d.addAll(Arrays.asList(cube.getAll3dPoints()));
			notRotatedPointsToDraw.addAll(Arrays.asList(cube.getAll3dPointsToDraw()));
		}
	}

	public void rotate(double xAngle, double yAngle, double zAngle) {
		Quaternion xRotation = Quaternion.fromAxisAngle(xAngle, 1, 0, 0);
		Quaternion yRotation = Quaternion.fromAxisAngle(yAngle, 0, 1, 0);
		Quaternion zRotation = Quaternion.fromAxisAngle(zAngle, 0, 0, 1);
		currentRotation = xRotation.multiply(yRotation).multiply(zRotation).multiply(currentRotation);
		double[][] rotationMatrix = currentRotation.toRotationMatrix();
		for (int i = 0; i < points3d.size(); i++) {
			Point p = points3d.get(i);
			double tx = p.getX() - x;
			double ty = p.getY() - y;
			double tz = p.getZ() - z;
			double newX = rotationMatrix[0][0] * tx + rotationMatrix[0][1] * ty + rotationMatrix[0][2] * tz;
			double newY = rotationMatrix[1][0] * tx + rotationMatrix[1][1] * ty + rotationMatrix[1][2] * tz;
			double newZ = rotationMatrix[2][0] * tx + rotationMatrix[2][1] * ty + rotationMatrix[2][2] * tz;
			points3dToDraw.get(i).moveTo(newX + x, newY + y, newZ + z);
		}
		for (int i = 0; i < notRotatedPoints3d.size(); i++) {
			Point p = notRotatedPoints3d.get(i);
			double tx = p.getX() - x;
			double ty = p.getY() - y;
			double tz = p.getZ() - z;
			double newX = rotationMatrix[0][0] * tx + rotationMatrix[0][1] * ty + rotationMatrix[0][2] * tz;
			double newY = rotationMatrix[1][0] * tx + rotationMatrix[1][1] * ty + rotationMatrix[1][2] * tz;
			double newZ = rotationMatrix[2][0] * tx + rotationMatrix[2][1] * ty + rotationMatrix[2][2] * tz;
			notRotatedPointsToDraw.get(i).moveTo(newX + x, newY + y, newZ + z);
		}
		for (Cube cube : cubes) {
			Point3d p = cube.getPos();
			double tx = p.getX() - x;
			double ty = p.getY() - y;
			double tz = p.getZ() - z;
			double newX = rotationMatrix[0][0] * tx + rotationMatrix[0][1] * ty + rotationMatrix[0][2] * tz;
			double newY = rotationMatrix[1][0] * tx + rotationMatrix[1][1] * ty + rotationMatrix[1][2] * tz;
			double newZ = rotationMatrix[2][0] * tx + rotationMatrix[2][1] * ty + rotationMatrix[2][2] * tz;
			cube.updateDrawnPos(newX + x, newY + y, newZ + z);
		}
		for (Cube cube : cubesThatDoNotRotate) {
			Point3d p = cube.getPos();
			double tx = p.getX() - x;
			double ty = p.getY() - y;
			double tz = p.getZ() - z;
			double newX = rotationMatrix[0][0] * tx + rotationMatrix[0][1] * ty + rotationMatrix[0][2] * tz;
			double newY = rotationMatrix[1][0] * tx + rotationMatrix[1][1] * ty + rotationMatrix[1][2] * tz;
			double newZ = rotationMatrix[2][0] * tx + rotationMatrix[2][1] * ty + rotationMatrix[2][2] * tz;
			cube.updateDrawnPos(newX + x, newY + y, newZ + z);
		}
	}

	public boolean checkIfCubeIsSolved() {
		Vec3D initialUpVec = cubes.get(0).getUpOrientationVector();
		Vec3D initialRightVec = cubes.get(0).getRightOrientationVector();
		for(int i = 0; i < cubes.size(); i++) {
			boolean upIsGood = initialUpVec.cosineSimilarity(cubes.get(i).getUpOrientationVector()) > 0.999;
			boolean rightIsGood = initialRightVec.cosineSimilarity(cubes.get(i).getRightOrientationVector()) > 0.999;
			if(
				!((i == 4 || i == 12 || i == 10 || i == 14 || i == 22 || i == 16 || i == 13) || (upIsGood && rightIsGood))
			) {
				return false;
			}
		}
		return true;
	}

	public void rotateXAroundCube(Cube cubeToRotateAround, double angle) {
		for (Cube cube : cubes) {
			if (Math.abs(cube.getPos().getX() - cubeToRotateAround.getPos().getX()) < RotationOperation.CUBE_ROTATION_TOLERANCE) {
				cube.rotateX(
					(angle),
					new Point3d(cubeToRotateAround.getPos().getX(), 0,  0)
				);
			}
		}
	}

	public void rotateYAroundCube(Cube cubeToRotateAround, double angle) {
		for (Cube cube : cubes) {
			if (Math.abs(cube.getPos().getY() - cubeToRotateAround.getPos().getY()) < RotationOperation.CUBE_ROTATION_TOLERANCE) {
				cube.rotateY(
					(angle),
					new Point3d(0, cubeToRotateAround.getPos().getY(),  0)
				);
			}
		}
	}

	public void rotateZAroundCube(Cube cubeToRotateAround, double angle) {
		for (Cube cube : cubes) {
			if (Math.abs(cube.getPos().getZ() - cubeToRotateAround.getPos().getZ()) < RotationOperation.CUBE_ROTATION_TOLERANCE) {
				cube.rotateZ(
					(angle),
					new Point3d(0, 0, cubeToRotateAround.getPos().getZ())
				);
			}
		}
	}

	@Override
	public void render(Canvas canvas, boolean isDarkMode) {
//		Log.w("Rubik's Cube", "render");
		drawnPolygons.sort(Comparator.comparingDouble(BigPolygon::getDistanceFromPlayer));
		smallPolygons.sort(Comparator.comparingDouble(Polygon::getDistanceFromPlayer));
		notRotatedPolygons.sort(Comparator.comparingDouble(BigPolygon::getDistanceFromPlayer));
//		Log.w("Gil", String.valueOf(smallPolygons.size()));
		for (Polygon polygon : smallPolygons) {
			if (polygon.isPointingToPlayer()) {
//				polygon.updatePoints();
				polygon.render(canvas, isDarkMode);
			}
		}
		for (BigPolygon polygon : drawnPolygons) {
			if (polygon.isPointingToPlayer()) {
				polygon.render(canvas, isDarkMode);
			}
		}
		for (BigPolygon polygon : notRotatedPolygons) {
			if (polygon.isPointingToPlayer()) {
				polygon.setSelected(false);
			}
		}
	}

	public List<BigPolygon> getAllDrawnBigPolygons() {
		return drawnPolygons;
	}

	public List<BigPolygon> getAllNotRotatedPolygons() {
		return notRotatedPolygons;
	}

	public Quaternion getCurrentRotation() {
		return currentRotation;
	}

	public double getRubiksCubeSize() {
		return rubiksCubeSize;
	}

	public double getSmallCubesSize() {
		return smallCubesSize;
	}

}

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

/**
 * Represents a complete 3D Rubik's Cube composed of 27 smaller cubes.
 * This class manages the cube's rotation, individual face rotations, solved state checking,
 * and rendering.
 */
public class RubiksCube extends Drawable {

	// Center position of the entire Rubik's Cube in 3D space
	private final double x, y, z;

	// Collection of all 27 individual cubes that make up the Rubik's Cube
	private final List<Cube> cubes;

	// Duplicate collection used for maintaining non-rotated reference positions
	private final List<Cube> cubesThatDoNotRotate;

	// All 3D points from all cubes (original positions)
	private final List<Point> points;

	// All 3D points from non-rotated cubes (reference positions)
	private final List<Point> notRotatedPoints;

	// All 3D points used for rendering (transformed positions)
	private final List<Point> pointsToDraw;

	// All 3D points for non-rotated rendering (transformed reference positions)
	private final List<Point> notRotatedPointsToDraw;

	// All polygons from rotated cubes that will be rendered
	private final List<Polygon> drawnPolygons;

	// All polygons from non-rotated cubes (used as reference)
	private final List<Polygon> notRotatedPolygons;

	// Current rotation state of the entire cube represented as a Quaternion
	private Quaternion currentRotation;

	// The size of the Rubik's Cube
	private final double rubiksCubeSize;

	// Size of the individual small cubes
	private final double smallCubesSize;

	/**
	 * Creates a new Rubik's Cube at the specified position with the given size.
	 *
	 * @param x X-coordinate of the cube's center
	 * @param y Y-coordinate of the cube's center
	 * @param z Z-coordinate of the cube's center
	 * @param size Overall size of the Rubik's Cube
	 */
	public RubiksCube(double x, double y, double z, double size) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.rubiksCubeSize = size;

		// Initialize all ArrayLists
		this.points = new ArrayList<>();
		this.notRotatedPoints = new ArrayList<>();
		this.pointsToDraw = new ArrayList<>();
		this.notRotatedPointsToDraw = new ArrayList<>();
		this.drawnPolygons = new ArrayList<>();
		this.notRotatedPolygons = new ArrayList<>();
		this.cubes = new ArrayList<>();
		this.cubesThatDoNotRotate = new ArrayList<>();

		// Calculate size of individual cubes
		double sizeOfSmallCubes = size / 3;
		this.smallCubesSize = sizeOfSmallCubes;

		// Initialize rotation
		this.currentRotation = new Quaternion(1, 0, 0, 0);

		// Create all 27 small cubes
		// Left layer (x = -1)
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));

		// Middle layer (x = 0)
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));

		// Right layer (x = +1)
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));

		// Create identical cubes for the non-rotated reference state
		// This maintains the original solved configuration for comparison
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

		// Collect all points and polygons from the cubes
		for (Cube cube : cubes) {
			points.addAll(Arrays.asList(cube.getAll3dPoints()));
			pointsToDraw.addAll(Arrays.asList(cube.getAll3dPointsToDraw()));
			drawnPolygons.addAll(cube.getAllPolygons());
		}

		// Collect all points and polygons from the non-rotated reference cubes
		for(Cube cube : cubesThatDoNotRotate) {
			notRotatedPolygons.addAll(cube.getAllPolygons());
			notRotatedPoints.addAll(Arrays.asList(cube.getAll3dPoints()));
			notRotatedPointsToDraw.addAll(Arrays.asList(cube.getAll3dPointsToDraw()));
		}
	}

	/**
	 * Rotates the entire Rubik's Cube by the specified angles around each axis.
	 *
	 * @param xAngle Rotation angle around X-axis in radians
	 * @param yAngle Rotation angle around Y-axis in radians
	 * @param zAngle Rotation angle around Z-axis in radians
	 */
	public void rotate(double xAngle, double yAngle, double zAngle) {
		// Create quaternions for each axis rotation
		Quaternion xRotation = Quaternion.fromAxisAngle(xAngle, 1, 0, 0);
		Quaternion yRotation = Quaternion.fromAxisAngle(yAngle, 0, 1, 0);
		Quaternion zRotation = Quaternion.fromAxisAngle(zAngle, 0, 0, 1);

		// Combine rotations and update current rotation state
		currentRotation = xRotation.multiply(yRotation).multiply(zRotation).multiply(currentRotation);

		// Convert quaternion to rotation matrix
		double[][] rotationMatrix = currentRotation.toRotationMatrix();

		// Transform all rotated cube points
		for (int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			// Translate to origin, rotate, then translate back
			double tx = p.getX() - x;
			double ty = p.getY() - y;
			double tz = p.getZ() - z;

			// Apply rotation matrix
			double newX = rotationMatrix[0][0] * tx + rotationMatrix[0][1] * ty + rotationMatrix[0][2] * tz;
			double newY = rotationMatrix[1][0] * tx + rotationMatrix[1][1] * ty + rotationMatrix[1][2] * tz;
			double newZ = rotationMatrix[2][0] * tx + rotationMatrix[2][1] * ty + rotationMatrix[2][2] * tz;

			// Update drawing position
			pointsToDraw.get(i).moveTo(newX + x, newY + y, newZ + z);
		}

		// Transform all non-rotated reference cube points
		for (int i = 0; i < notRotatedPoints.size(); i++) {
			Point p = notRotatedPoints.get(i);
			double tx = p.getX() - x;
			double ty = p.getY() - y;
			double tz = p.getZ() - z;

			double newX = rotationMatrix[0][0] * tx + rotationMatrix[0][1] * ty + rotationMatrix[0][2] * tz;
			double newY = rotationMatrix[1][0] * tx + rotationMatrix[1][1] * ty + rotationMatrix[1][2] * tz;
			double newZ = rotationMatrix[2][0] * tx + rotationMatrix[2][1] * ty + rotationMatrix[2][2] * tz;

			notRotatedPointsToDraw.get(i).moveTo(newX + x, newY + y, newZ + z);
		}

		// Update cube center positions for rotated cubes
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

		// Update cube center positions for non-rotated reference cubes
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

	/**
	 * Checks if the Rubik's Cube is in a solved state.
	 *
	 * @return True if the cube is solved (all faces properly aligned)
	 */
	public boolean checkIfCubeIsSolved() {
		// Get reference orientation from the first cube
		Vec3D initialUpVec = cubes.get(0).getUpOrientationVector();
		Vec3D initialRightVec = cubes.get(0).getRightOrientationVector();

		// Check alignment of all cubes
		for(int i = 0; i < cubes.size(); i++) {
			boolean upIsGood = initialUpVec.cosineSimilarity(cubes.get(i).getUpOrientationVector()) > 0.999;
			boolean rightIsGood = initialRightVec.cosineSimilarity(cubes.get(i).getRightOrientationVector()) > 0.999;

			// Special handling for center cubes (indices 4, 12, 10, 14, 22, 16, 13)
			// which don't need to be aligned since they are at the centers
			if(
				!((i == 4 || i == 12 || i == 10 || i == 14 || i == 22 || i == 16 || i == 13) || (upIsGood && rightIsGood))
			) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Rotates a layer of cubes around the X-axis.
	 *
	 * @param cubeToRotateAround Reference cube that determines which layer to rotate
	 * @param angle Rotation angle in radians
	 */
	public void rotateXAroundCube(Cube cubeToRotateAround, double angle) {
		for (Cube cube : cubes) {
			// Check if cube is in the same X-layer
			if (Math.abs(cube.getPos().getX() - cubeToRotateAround.getPos().getX()) < RotationOperation.CUBE_POSITION_TOLERANCE_FOR_ROTATION) {
				cube.rotateX(
					(angle),
					new Point3d(cubeToRotateAround.getPos().getX(), 0,  0)
				);
			}
		}
	}

	/**
	 * Rotates a layer of cubes around the Y-axis.
	 *
	 * @param cubeToRotateAround Reference cube that determines which layer to rotate
	 * @param angle Rotation angle in radians
	 */
	public void rotateYAroundCube(Cube cubeToRotateAround, double angle) {
		for (Cube cube : cubes) {
			// Check if cube is in the same Y-layer
			if (Math.abs(cube.getPos().getY() - cubeToRotateAround.getPos().getY()) < RotationOperation.CUBE_POSITION_TOLERANCE_FOR_ROTATION) {
				cube.rotateY(
					(angle),
					new Point3d(0, cubeToRotateAround.getPos().getY(),  0)
				);
			}
		}
	}

	/**
	 * Rotates a layer of cubes around the Z-axis.
	 *
	 * @param cubeToRotateAround Reference cube that determines which layer to rotate
	 * @param angle Rotation angle in radians
	 */
	public void rotateZAroundCube(Cube cubeToRotateAround, double angle) {
		for (Cube cube : cubes) {
			// Check if cube is in the same Z-layer
			if (Math.abs(cube.getPos().getZ() - cubeToRotateAround.getPos().getZ()) < RotationOperation.CUBE_POSITION_TOLERANCE_FOR_ROTATION) {
				cube.rotateZ(
					(angle),
					new Point3d(0, 0, cubeToRotateAround.getPos().getZ())
				);
			}
		}
	}

	/**
	 * Renders the Rubik's Cube to the provided canvas.
	 *
	 * @param canvas The Android Canvas to draw on
	 * @param isDarkMode Whether to use dark mode colors
	 */
	@Override
	public void render(Canvas canvas, boolean isDarkMode) {
		// Sort polygons by distance from player
		drawnPolygons.sort(Comparator.comparingDouble(Polygon::getDistanceFromPlayer));
		notRotatedPolygons.sort(Comparator.comparingDouble(Polygon::getDistanceFromPlayer));

		// Renders only the polygons that face the player
		for (Polygon polygon : drawnPolygons) {
			if (polygon.isPointingToPlayer()) {
				polygon.render(canvas, isDarkMode);
			}
		}

		// Deselects the selected polygon
		for (Polygon polygon : notRotatedPolygons) {
			if (polygon.isPointingToPlayer()) {
				polygon.setSelected(false);
			}
		}
	}

	/**
	 * Returns all polygons from the rotated cubes that are rendered.
	 *
	 * @return List of all drawable polygons
	 */
	public List<Polygon> getAllDrawnPolygons() {
		return drawnPolygons;
	}

	/**
	 * Returns all polygons from the non-rotated reference cubes.
	 *
	 * @return List of all reference polygons
	 */
	public List<Polygon> getAllNotRotatedPolygons() {
		return notRotatedPolygons;
	}

	/**
	 * Returns the current rotation of the cube as a quaternion.
	 *
	 * @return Current rotation quaternion
	 */
	public Quaternion getCurrentRotation() {
		return currentRotation;
	}

	/**
	 * Returns the size of the Rubik's Cube.
	 *
	 * @return Total size of the cube
	 */
	public double getRubiksCubeSize() {
		return rubiksCubeSize;
	}

	/**
	 * Returns the size of the small cubes.
	 *
	 * @return Size of the small cubes
	 */
	public double getSmallCubesSize() {
		return smallCubesSize;
	}

}
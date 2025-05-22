package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.my3dproject.ScreenGeometryManager;
import com.example.my3dproject.math.Vec3D;
import com.example.my3dproject.math.geometry.CubeColors;
import com.example.my3dproject.math.geometry.Point3d;
import com.example.my3dproject.math.geometry.PointUtil;
import com.example.my3dproject.math.geometry.Quaternion;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single cube in 3D space that can be rotated and rendered.
 * This is typically used as one of the 27 small cubes that make up a Rubik's cube.
 * Each cube has 8 vertices (points) and 6 faces (polygons).
 */
public class Cube extends Drawable {

	// Position coordinates of the cube's center
	private double x, y, z;

	// Position coordinates used for rendering (may differ from actual position during rotations)
	private double drawnX, drawnY, drawnZ;

	// The 8 corner points of the cube (vertices)
	private final Point[] points;

	// Copy of points used for rendering transformations
	private final Point[] pointsToDraw;

	// The 6 faces of the cube as polygons
	private final List<Polygon> polygons;

	// Copy of polygons used for rendering transformations
	private final List<Polygon> polygonsToDraw;

	/**
	 * Creates a new cube at the specified position with the given size.
	 *
	 * @param x X coordinate of the cube's center
	 * @param y Y coordinate of the cube's center
	 * @param z Z coordinate of the cube's center
	 * @param size Side length of the cube
	 */
	public Cube(double x, double y, double z, double size) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;

		// Calculate half the size for positioning vertices relative to center
		double halfOfSize = size / 2;

		// Define the 8 vertices of the cube
		// Each vertex is positioned relative to the center
		this.points = new Point[] {
			new Point(x + halfOfSize, y + halfOfSize, z + halfOfSize),
			new Point(x + halfOfSize, y - halfOfSize, z + halfOfSize),
			new Point(x - halfOfSize, y + halfOfSize, z + halfOfSize),
			new Point(x - halfOfSize, y - halfOfSize, z + halfOfSize),
			new Point(x + halfOfSize, y + halfOfSize, z - halfOfSize),
			new Point(x + halfOfSize, y - halfOfSize, z - halfOfSize),
			new Point(x - halfOfSize, y + halfOfSize, z - halfOfSize),
			new Point(x - halfOfSize, y - halfOfSize, z - halfOfSize)
		};

		// Create a copy of points for rendering transformations
		this.pointsToDraw = PointUtil.copyArrayOfPoints(points);

		// Initialize polygon lists
		this.polygons = new ArrayList<>();
		this.polygonsToDraw = new ArrayList<>();

		// Get colors for each face based on the cube's position
		CubeColors colors = CubeColors.getColorsFromPos(x, y, z);

		// Create the 6 faces of the cube as polygons
		// Each polygon is defined by 4 vertices in counter-clockwise order

		polygons.add(new Polygon(this, colors.getColors()[5], points[0], points[1], points[3], points[2]));
		polygons.add(new Polygon(this, colors.getColors()[3], points[5], points[4], points[6], points[7]));
		polygons.add(new Polygon(this, colors.getColors()[2], points[0], points[4], points[5], points[1]));
		polygons.add(new Polygon(this, colors.getColors()[4], points[3], points[7], points[6], points[2]));
		polygons.add(new Polygon(this, colors.getColors()[1], points[1], points[5], points[7], points[3]));
		polygons.add(new Polygon(this, colors.getColors()[0], points[2], points[6], points[4], points[0]));

		// Create corresponding polygons for rendering using the pointsToDraw array
		polygonsToDraw.add(new Polygon(this, colors.getColors()[5], pointsToDraw[0], pointsToDraw[1], pointsToDraw[3], pointsToDraw[2]));
		polygonsToDraw.add(new Polygon(this, colors.getColors()[3], pointsToDraw[5], pointsToDraw[4], pointsToDraw[6], pointsToDraw[7]));
		polygonsToDraw.add(new Polygon(this, colors.getColors()[2], pointsToDraw[0], pointsToDraw[4], pointsToDraw[5], pointsToDraw[1]));
		polygonsToDraw.add(new Polygon(this, colors.getColors()[4], pointsToDraw[3], pointsToDraw[7], pointsToDraw[6], pointsToDraw[2]));
		polygonsToDraw.add(new Polygon(this, colors.getColors()[1], pointsToDraw[1], pointsToDraw[5], pointsToDraw[7], pointsToDraw[3]));
		polygonsToDraw.add(new Polygon(this, colors.getColors()[0], pointsToDraw[2], pointsToDraw[6], pointsToDraw[4], pointsToDraw[0]));
	}

	/**
	 * Applies a rotation matrix to all points and the cube's center position.
	 *
	 * @param matrix 3x3 rotation matrix
	 * @param centerOfRotation Point around which to rotate
	 */
	private void rotateWithMatrix(double[][] matrix, Point3d centerOfRotation) {
		// Rotate each vertex of the cube
		for (int i = 0; i < points.length; i++) {
			Point p = points[i];

			// Translate point to origin relative to center of rotation
			double tx = p.getX() - centerOfRotation.getX();
			double ty = p.getY() - centerOfRotation.getY();
			double tz = p.getZ() - centerOfRotation.getZ();

			// Apply rotation matrix
			double finalX = matrix[0][0] * tx + matrix[0][1] * ty + matrix[0][2] * tz + centerOfRotation.getX();
			double finalY = matrix[1][0] * tx + matrix[1][1] * ty + matrix[1][2] * tz + centerOfRotation.getY();
			double finalZ = matrix[2][0] * tx + matrix[2][1] * ty + matrix[2][2] * tz + centerOfRotation.getZ();

			// Update point position
			points[i].moveTo(finalX, finalY, finalZ);
		}

		// Also rotate the cube's center position
		double tx = x - centerOfRotation.getX();
		double ty = y - centerOfRotation.getY();
		double tz = z - centerOfRotation.getZ();
		x = matrix[0][0] * tx + matrix[0][1] * ty + matrix[0][2] * tz + centerOfRotation.getX();
		y = matrix[1][0] * tx + matrix[1][1] * ty + matrix[1][2] * tz + centerOfRotation.getY();
		z = matrix[2][0] * tx + matrix[2][1] * ty + matrix[2][2] * tz + centerOfRotation.getZ();
	}

	/**
	 * Rotates the cube around the X-axis.
	 *
	 * @param angle Rotation angle in radians
	 * @param centerOfRotation Point around which to rotate
	 */
	public void rotateX(double angle, Point3d centerOfRotation) {
		double[][] matrix = Quaternion.fromAxisAngle(angle, 1, 0, 0).toRotationMatrix();
		rotateWithMatrix(matrix, centerOfRotation);
	}

	/**
	 * Rotates the cube around the Y-axis.
	 *
	 * @param angle Rotation angle in radians
	 * @param centerOfRotation Point around which to rotate
	 */
	public void rotateY(double angle, Point3d centerOfRotation) {
		double[][] matrix = Quaternion.fromAxisAngle(angle, 0, 1, 0).toRotationMatrix();
		rotateWithMatrix(matrix, centerOfRotation);
	}

	/**
	 * Rotates the cube around the Z-axis.
	 *
	 * @param angle Rotation angle in radians
	 * @param centerOfRotation Point around which to rotate
	 */
	public void rotateZ(double angle, Point3d centerOfRotation) {
		double[][] matrix = Quaternion.fromAxisAngle(angle, 0, 0, 1).toRotationMatrix();
		rotateWithMatrix(matrix, centerOfRotation);
	}

	/**
	 * @return Array of all 3D points (vertices) of the cube
	 */
	public Point[] getAll3dPoints() {
		return points;
	}

	/**
	 * @return Array of all 3D points used for drawing/rendering
	 */
	public Point[] getAll3dPointsToDraw() {
		return pointsToDraw;
	}

	/**
	 * @return List of all polygons (faces) used for drawing/rendering
	 */
	public List<Polygon> getAllPolygons() {
		return polygonsToDraw;
	}

	/**
	 * @return Current position of the cube's center as a 3D point
	 */
	public Point3d getPos() {
		return new Point3d(x, y, z);
	}

	/**
	 * Gets the original (non-rotated) polygon corresponding to a drawn polygon.
	 *
	 * @param drawPolygon A polygon from the polygonsToDraw list
	 * @return The corresponding polygon from the original polygons list
	 */
	public Polygon getNotRotatedPolygonFromDrawnPolygon(Polygon drawPolygon) {
		return polygons.get(polygonsToDraw.indexOf(drawPolygon));
	}

	/**
	 * Updates the position used for rendering (may differ from actual position).
	 *
	 * @param x X coordinate for rendering
	 * @param y Y coordinate for rendering
	 * @param z Z coordinate for rendering
	 */
	public void updateDrawnPos(double x, double y, double z) {
		this.drawnX = x;
		this.drawnY = y;
		this.drawnZ = z;
	}

	/**
	 * Gets the "up" orientation vector of the cube based on its current rotation.
	 * This is used to determine if the cube is properly oriented in a solved Rubik's cube.
	 *
	 * @return Normalized vector pointing "up" relative to the cube's orientation
	 */
	public Vec3D getUpOrientationVector() {
		return Vec3D.fromDifferenceInPos(points[0], points[1]).normalize();
	}

	/**
	 * Gets the "right" orientation vector of the cube based on its current rotation.
	 * This is used to determine if the cube is properly oriented in a solved Rubik's cube.
	 *
	 * @return Normalized vector pointing "right" relative to the cube's orientation
	 */
	public Vec3D getRightOrientationVector() {
		return Vec3D.fromDifferenceInPos(points[0], points[2]).normalize();
	}

	/**
	 * Renders the cube on the canvas. Currently displays coordinate information as text.
	 * The actual cube faces are rendered by their individual polygons.
	 *
	 * @param canvas Android Canvas to draw on
	 * @param isDarkMode Whether dark mode is enabled
	 */
	@Override
	public void render(Canvas canvas, boolean isDarkMode) {
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(30);

		// Display the cube's coordinates as text (for debugging purposes)
		canvas.drawText("(" + ((int) (x * 10) / 10.0) + "," + ((int) (y * 10) / 10.0) + "," + ((int) (z * 1000) / 1000.0) + ")",
			(float) ScreenGeometryManager.getInstance().getProjectionTranslatedX(new Point3d(drawnX, drawnY, drawnZ)) - 100,
			(float) ScreenGeometryManager.getInstance().getProjectionTranslatedY(new Point3d(drawnX, drawnY, drawnZ)),
			paint
		);

	}
}
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
import com.example.my3dproject.math.geometry.PointUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a 3D polygon face that can be rendered on a 2D canvas.
 * This class handles the projection, lighting, and rendering of polygon faces
 * in a 3D environment.
 */
public class Polygon extends Drawable implements UpdatableComponent {

	// Reference to the parent cube that contains this polygon
	private final Cube parentCube;

	// Base color of the polygon (before lighting calculations)
	@ColorInt
	private final int color;

	// List of 3D points that define the polygon's vertices
	private final List<Point> points;

	// Path object for drawing the filled polygon shape
	private Path pathOfPolygon;

	// Path object for drawing the polygon's outline
	private Path pathOfLines;

	// Paint object for rendering the filled polygon
	private Paint paintOfFilledShape;

	// Normal vector of the polygon surface
	private Vec3D normalVector;

	// Boolean indicating if this polygon is currently selected
	private boolean isSelected;

	/**
	 * Creates a new polygon with the specified parent cube, color, and vertices.
	 *
	 * @param parentCube The cube that contains this polygon face
	 * @param color The base color of the polygon
	 * @param points Variable number of Point objects defining the polygon vertices
	 */
	public Polygon(Cube parentCube, @ColorInt int color, Point... points) {
		super();
		this.parentCube = parentCube;
		this.color = color;
		this.points = Arrays.asList(points);
		this.pathOfPolygon = new Path();
		this.pathOfLines = new Path();
		this.paintOfFilledShape = new Paint();
		this.normalVector = updateNormalVector();
		this.isSelected = false;
	}

	/**
	 * Returns the parent cube that contains this polygon.
	 *
	 * @return The parent Cube object
	 */
	public Cube getParentCube() {
		return parentCube;
	}

	/**
	 * Sets the selection state of this polygon.
	 *
	 * @param isSelected True if the polygon should be selected
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	/**
	 * Determines if this polygon is facing towards the player position.
	 * @return True if the polygon is facing the player (front-facing)
	 */
	public boolean isPointingToPlayer() {
		Point3d middle = getMiddleOfPolygon();// Middle of the polygon
		Point3d playerPos = Constants.EnvironmentConstants.PLAYER_POSITION; // The position of the player.
		Vec3D playerDirection = Vec3D.fromDifferenceInPos(middle, playerPos); // The vector that goes from the player to the middle of the polygon.
		playerDirection.normalize(); //Normalizing the vector.
		Vec3D normalVector = updateNormalVector(); // Calculating the normal vector of the polygon.
		return (playerDirection.dotProduct(normalVector) < 0); // if the dot product is positive, then the polygon does not face the player.
	}

	/**
	 * Tests if a 2D screen point lies within the projected polygon boundaries.
	 *
	 * @param point The 2D screen coordinate to test
	 * @return True if the point is inside the polygon
	 */
	public boolean isPointInPolygon(Point2d point) {
		int intersections = 0;
		int n = points.size();

		// Ray casting algorithm: cast a ray from the point to the right
		// and count intersections with polygon edges
		for (int i = 0; i < n; i++) {
			Point2d p1 = points.get(i).getLastDrawnPoint();
			Point2d p2 = points.get((i + 1) % n).getLastDrawnPoint();

			// Check if the ray intersects with this edge
			if ((point.getY() > Math.min(p1.getY(), p2.getY())) &&
				(point.getY() <= Math.max(p1.getY(), p2.getY())) &&
				(point.getX() <= Math.max(p1.getX(), p2.getX()))) {

				// Calculate x-coordinate of intersection
				double xIntersection = p1.getX() + (point.getY() - p1.getY()) *
					(p2.getX() - p1.getX()) / (p2.getY() - p1.getY());

				if (xIntersection > point.getX()) {
					intersections++;
				}
			}
		}

		// Point is inside if number of intersections is odd
		return (intersections % 2 != 0);
	}

	/**
	 * Calculates the 3D distance from the polygon's center to the player position.
	 *
	 * @return The distance from polygon center to player
	 */
	public double getDistanceFromPlayer() {
		Point3d player = Constants.EnvironmentConstants.PLAYER_POSITION;
		Point3d middle = getMiddleOfPolygon();
		return PointUtil.distance(middle, player);
	}

	/**
	 * Calculates the center of the polygon.
	 *
	 * @return The 3D center point of the polygon
	 */
	private Point3d getMiddleOfPolygon() {
		double xAvg = 0;
		double yAvg = 0;
		double zAvg = 0;

		// Sum all vertex coordinates
		for(Point point : points) {
			xAvg += point.getX();
			yAvg += point.getY();
			zAvg += point.getZ();
		}

		// Calculate average
		xAvg /= points.size();
		yAvg /= points.size();
		zAvg /= points.size();

		return new Point3d(xAvg, yAvg, zAvg);
	}

	/**
	 * Calculates and returns the normal vector of the polygon surface.
	 *
	 * @return The normalized surface normal vector
	 */
	public Vec3D updateNormalVector() {
		Vec3D vector = new Vec3D(0, 0, 0);

		// Calculate normal using cross product of two edge vectors
		// Normal = (P0-P1) × (P0-P2)
		vector.setX((points.get(0).getY() - points.get(1).getY()) * (points.get(0).getZ() - points.get(2).getZ()) -
			(points.get(0).getZ() - points.get(1).getZ()) * (points.get(0).getY() - points.get(2).getY()));
		vector.setY((points.get(0).getZ() - points.get(1).getZ()) * (points.get(0).getX() - points.get(2).getX()) -
			(points.get(0).getX() - points.get(1).getX()) * (points.get(0).getZ() - points.get(2).getZ()));
		vector.setZ((points.get(0).getX() - points.get(1).getX()) * (points.get(0).getY() - points.get(2).getY()) -
			(points.get(0).getY() - points.get(1).getY()) * (points.get(0).getX() - points.get(2).getX()));

		vector.normalize();
		return vector;
	}

	/**
	 * Updates the polygon's state and prepares rendering paths.
	 * This method is called each frame to update the polygon's 2D projection
	 * based on current 3D vertex positions.
	 *
	 * @param deltaTime Time elapsed since last update
	 * @param pointOfClick Screen coordinates of user click
	 * @param event Input event type
	 */
	@Override
	public void update(double deltaTime, Point2d pointOfClick, int event) {
		// Recalculate normal vector for current vertex positions
		normalVector = updateNormalVector();

		// Reset drawing paths
		pathOfPolygon = new Path();
		pathOfLines = new Path();

		// Create paths by projecting 3D vertices to 2D screen coordinates
		// Start path at first vertex
		pathOfPolygon.moveTo(
			(float) ScreenGeometryManager.getInstance().getProjectionTranslatedX(points.get(0).getPose()),
			(float) ScreenGeometryManager.getInstance().getProjectionTranslatedY(points.get(0).getPose())
		);
		pathOfLines.moveTo(
			(float) ScreenGeometryManager.getInstance().getProjectionTranslatedX(points.get(0).getPose()),
			(float) ScreenGeometryManager.getInstance().getProjectionTranslatedY(points.get(0).getPose())
		);

		// Add lines to all other vertices
		for (Point dot : points) {
			pathOfPolygon.lineTo(
				(float) ScreenGeometryManager.getInstance().getProjectionTranslatedX(dot.getPose()),
				(float) ScreenGeometryManager.getInstance().getProjectionTranslatedY(dot.getPose())
			);
			pathOfLines.lineTo(
				(float) ScreenGeometryManager.getInstance().getProjectionTranslatedX(dot.getPose()),
				(float) ScreenGeometryManager.getInstance().getProjectionTranslatedY(dot.getPose())
			);
		}

		// Close the paths to complete the polygon shape
		pathOfPolygon.close();
		pathOfLines.close();
	}

	/**
	 * Calculates the final color of the polygon with shades.
	 *
	 * @param color The base color of the polygon
	 * @param lightDirection The direction vector of the light source
	 * @param normalVector The surface normal vector of the polygon
	 * @return The final color with shades
	 */
	private int calculateColorWithShade(int color, Vec3D lightDirection, Vec3D normalVector) {
		// Extract RGB components from color integer
		int red = (color >> 16) & 0xFF;
		int green = (color >> 8) & 0xFF;
		int blue = color & 0xFF;

		// Calculate lighting intensity using dot product
		double dotProduct =
			normalVector.getX() * lightDirection.getX() +
				normalVector.getY() * lightDirection.getY() +
				normalVector.getZ() * lightDirection.getZ();

		// Normalize dot product to range [0, 1]
		dotProduct++;  // Convert from [-1, 1] to [0, 2]
		dotProduct /= 2;  // Convert to [0, 1]
		dotProduct = Math.sqrt(dotProduct); // Take the square root of the value for smoother lighting

		// Apply lighting intensity to each color component
		return Color.rgb(
			(int) (red * (dotProduct)),
			(int) (green * (dotProduct)),
			(int) (blue * (dotProduct))
		);
	}

	/**
	 * Converts standard colors to dark mode equivalents.
	 *
	 * @param color The original color
	 * @return The dark mode equivalent color
	 */
	private int getDarkModeColor(int color) {
		if(color == Color.YELLOW) {
			return Color.rgb(255, 255, 71);  // Brighter yellow
		}
		else if(color == Color.RED) {
			return Color.rgb(255, 0, 255);  // Magenta instead of red
		}
		else if(color == Color.BLUE) {
			return Color.rgb(51, 153, 255);  // Lighter blue
		}
		else if(color == Color.rgb(0,197,0)) {  // Green
			return Color.rgb(0, 255, 102);  // Brighter green
		}
		else if(color == Color.rgb(255,165,0)) {  // Orange
			return Color.rgb(225, 122, 70);  // Adjusted orange
		}
		return color;  // Return original color if no dark mode mapping
	}

	/**
	 * Renders the polygon onto the provided canvas.
	 *
	 * @param canvas The Android Canvas to draw on
	 * @param isDarkMode Whether to use dark mode colors
	 */
	@Override
	public void render(Canvas canvas, boolean isDarkMode) {
		// Calculate final color with lighting effects
		paintOfFilledShape.setColor(isDarkMode ?
			calculateColorWithShade(getDarkModeColor(color), Constants.EnvironmentConstants.LIGHT_DIRECTION, normalVector)
			: calculateColorWithShade(color, Constants.EnvironmentConstants.LIGHT_DIRECTION, normalVector));
		paintOfFilledShape.setStyle(Paint.Style.FILL);

		// Setup outline paint
		Paint paintOfLines = new Paint();
		paintOfLines.setColor(isSelected? Color.LTGRAY : Color.BLACK);  // Highlight selected polygons
		paintOfLines.setStrokeWidth((float) (3*ScreenGeometryManager.getInstance().getScreenSizeRatio()));
		paintOfLines.setAntiAlias(false);
		paintOfLines.setStyle(Paint.Style.STROKE);

		// Reset selection state after rendering
		isSelected = false;

		// Draw the filled polygon and its outline
		canvas.drawPath(pathOfPolygon, paintOfFilledShape);
		canvas.drawPath(pathOfLines, paintOfLines);
	}
}
package com.example.my3dproject.math;

public class MatrixUtil {

	public static double[][] createRotationMatrixWithoutRotation() {
		return new double[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
	}

	public static double[][] createXRotationMatrix(double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		return new double[][]{{1, 0, 0}, {0, cos, -sin}, {0, sin, cos}};
	}

	public static double[][] createYRotationMatrix(double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		return new double[][]{{cos, 0, sin}, {0, 1, 0}, {-sin, 0, cos}};
	}

	public static double[][] createZRotationMatrix(double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		return new double[][]{{cos, -sin, 0}, {sin, cos, 0}, {0, 0, 1}};
	}

	public static double[][] multiplyMatrices(double[][] a, double[][] b) {
		double[][] result = new double[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				result[i][j] = 0;
				for (int k = 0; k < 3; k++) {
					result[i][j] += a[i][k] * b[k][j];
				}
			}
		}
		return result;
	}

	public static double[][] normalizeMatrix(double[][] matrix) {
		double[] xAxis = matrix[0];
		double[] yAxis = matrix[1];
		double[] zAxis = matrix[2];
		xAxis = normalizeVector(xAxis);
		yAxis = subtractVectors(yAxis, projectVector(yAxis, xAxis));
		yAxis = normalizeVector(yAxis);
		zAxis = subtractVectors(zAxis, projectVector(zAxis, xAxis));
		zAxis = subtractVectors(zAxis, projectVector(zAxis, yAxis));
		zAxis = normalizeVector(zAxis);
		return new double[][]{xAxis, yAxis, zAxis};
	}

	public static double[] normalizeVector(double[] vector) {
		double length = Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
		return new double[]{vector[0] / length, vector[1] / length, vector[2] / length};
	}

	public static double[] subtractVectors(double[] a, double[] b) {
		return new double[]{a[0] - b[0], a[1] - b[1], a[2] - b[2]};
	}

	public static double[] projectVector(double[] a, double[] b) {
		double dotProduct = dot(a, b);
		double lengthSquared = dot(b, b);
		return new double[]{(dotProduct / lengthSquared) * b[0], (dotProduct / lengthSquared) * b[1], (dotProduct / lengthSquared) * b[2]};
	}

	public static double dot(double[] a, double[] b) {
		return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
	}

}

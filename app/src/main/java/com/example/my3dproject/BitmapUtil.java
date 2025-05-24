package com.example.my3dproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for bitmap processing operations including image transformation,
 * encoding, and loading from various sources.
 */
public class BitmapUtil {

	/**
	 * Standard width for profile pictures in pixels.
	 */
	public static final int PROFILE_PICTURE_WIDTH = 128;

	/**
	 * Standard height for profile pictures in pixels.
	 */
	public static final int PROFILE_PICTURE_HEIGHT = 128;

	/**
	 * Creates a circular bitmap from the input bitmap by cropping it to a square
	 * and drawing it within a circular mask.
	 *
	 * @param bitmap The source bitmap to be converted to circular shape
	 * @return A new circular bitmap with dimensions equal to the smaller dimension of the input
	 */
	public static Bitmap getCircularBitmap(Bitmap bitmap) {
		int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
		Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();

		paint.setAntiAlias(true);
		paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

		canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);

		return output;
	}

	/**
	 * Converts a bitmap to a Base64 encoded string representation.
	 *
	 * @param bitmap The bitmap to be encoded
	 * @return Base64 encoded string of the bitmap data
	 */
	public static String convertTo64Base(Bitmap bitmap){
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
		byte[] data = byteArrayOutputStream.toByteArray();
		return Base64.encodeToString(data, Base64.DEFAULT);
	}

	/**
	 * Loads a bitmap from a content URI and automatically corrects its orientation
	 * based on EXIF data.
	 *
	 * @param imageUri The URI of the image to load
	 * @param contentResolver ContentResolver to access the image data
	 * @return A properly oriented bitmap loaded from the URI
	 * @throws IOException If there's an error reading the image or EXIF data
	 */
	public static Bitmap getBitmapOutOfImageUri(Uri imageUri, android.content.ContentResolver contentResolver) throws IOException {
		//gets a bitmap from the uri
		InputStream inputStream = contentResolver.openInputStream(imageUri);
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		inputStream.close();

		//gets the orientation of the image
		InputStream exifInputStream = contentResolver.openInputStream(imageUri);
		ExifInterface exif = new ExifInterface(exifInputStream);
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
		exifInputStream.close();

		int rotationDegrees = 0;
		switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90: rotationDegrees = 90; break;
			case ExifInterface.ORIENTATION_ROTATE_180: rotationDegrees = 180; break;
			case ExifInterface.ORIENTATION_ROTATE_270: rotationDegrees = 270; break;
		}

		if (rotationDegrees != 0) { // if the orientation is not 0 degrees, rotate the image
			Matrix matrix = new Matrix(); // rotation matrix for the image
			matrix.postRotate(rotationDegrees);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}

		return bitmap;
	}


}
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

public class BitmapUtil {

	public static final int PROFILE_PICTURE_WIDTH = 128;
	public static final int PROFILE_PICTURE_HEIGHT = 128;

	public static Bitmap getCircularBitmap(Bitmap bitmap) {
		int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
		Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, size, size);

		paint.setAntiAlias(true);
		paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

		canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);

		return output;
	}

	public static String convertTo64Base(Bitmap bitmap){
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
		byte[] data = byteArrayOutputStream.toByteArray();
		return Base64.encodeToString(data, Base64.DEFAULT);
	}

	public static Bitmap getBitmapOutOfImageUri(Uri imageUri, android.content.ContentResolver contentResolver) throws IOException {
		InputStream inputStream = contentResolver.openInputStream(imageUri);
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		inputStream.close();

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

		if (rotationDegrees != 0) {
			Matrix matrix = new Matrix();
			matrix.postRotate(rotationDegrees);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}

		return bitmap;
	}


}

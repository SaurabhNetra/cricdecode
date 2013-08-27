package co.acjs.cricdecode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ZoomButtonsController;
import android.widget.ZoomButtonsController.OnZoomListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class CropPic extends SherlockFragmentActivity {
	private ImageView imageView;
	private ViewManager viewManager;
	private Matrix matrix;
	private int size;
	private final int outputSize = 100;
	public Uri mCapturedImageURI = null;
	public Uri targetUri = null;
	private int width;
	private int height;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crop_pic);

		width = ProfileData.mPrefs.getInt("width", 0);
		height = ProfileData.mPrefs.getInt("height", 0);

		WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display mDisplay = mWindowManager.getDefaultDisplay();

		if (mDisplay.getRotation() == 1 || mDisplay.getRotation() == 3) {
			size = ((int) (width * 0.7));
		} else {

			size = width < height ? width : height;

		}
		size -= 50;

		Log.w("On create", "Called");

		imageView = (ImageView) findViewById(R.id.imageViewCrop);
		imageView.getLayoutParams().width = size;
		imageView.getLayoutParams().height = size;
		viewManager = (ViewManager) imageView.getParent();

		if (getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)) {
			createZoomControls();
		}

		if (!ProfileEditFragment.profilePicturePath.equals("")) {
			imageView.setImageBitmap(BitmapFactory
					.decodeFile(ProfileEditFragment.profilePicturePath));
		} else {
			if (android.os.Build.VERSION.SDK_INT >= 16) {
				imageView.setBackground(getResources().getDrawable(
						R.drawable.picture_bg));

			} else {
				imageView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.picture_bg));
			}
		}

		imageView.setOnTouchListener(new OnTouchListener() {
			float initX;
			float initY;
			float midX;
			float midY;
			float scale;
			float initDistance;
			float currentDistance;
			boolean isMultitouch = false;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					initX = event.getX();
					initY = event.getY();
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					isMultitouch = true;
					initDistance = (float) Math.sqrt(Math.pow(
							initX - event.getX(1), 2)
							+ Math.pow(initY - event.getY(1), 2));
					break;
				case MotionEvent.ACTION_MOVE:
					if (isMultitouch) {
						matrix = imageView.getImageMatrix();
						currentDistance = (float) Math.sqrt(Math.pow(initX
								- event.getX(1), 2)
								+ Math.pow(initY - event.getY(1), 2));
						scale = 1 + 0.001f * (currentDistance - initDistance);
						midX = 0.5f * (initX + event.getX(1));
						midY = 0.5f * (initY + event.getY(1));
						matrix.postScale(scale, scale, midX, midY);
						imageView.setImageMatrix(matrix);
						imageView.invalidate();
					} else {
						imageView.scrollBy((int) (initX - event.getX()),
								(int) (initY - event.getY()));
						initX = event.getX();
						initY = event.getY();
					}
					break;
				case MotionEvent.ACTION_UP:
					isMultitouch = false;
					break;
				case MotionEvent.ACTION_POINTER_UP:
					isMultitouch = false;
					break;
				}
				return true;
			}
		});

		if (savedInstanceState != null) {
			Log.w("On savedInstanc", "Not null");
			restoreInstanceState(savedInstanceState);
		}
	}

	public void createZoomControls() {
		ZoomButtonsController zoomButtonsController = new ZoomButtonsController(
				imageView);
		zoomButtonsController.setVisible(true);
		zoomButtonsController.setAutoDismissed(false);
		Log.w("CropPic", "Set Zoom ");
		zoomButtonsController.setOnZoomListener(new OnZoomListener() {

			public void onZoom(boolean zoomIn) {
				matrix = imageView.getImageMatrix();
				if (zoomIn) {
					matrix.postScale(1.05f, 1.05f, 0.5f * size, 0.5f * size);
					imageView.setImageMatrix(matrix);
				} else {
					matrix.postScale(0.95f, 0.95f, 0.5f * size, 0.5f * size);
					imageView.setImageMatrix(matrix);
				}
				imageView.invalidate();
			}

			public void onVisibilityChanged(boolean visible) {
			}
		});
		RelativeLayout.LayoutParams zoomLayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		zoomLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		zoomLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		zoomLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.imageViewCrop);

		viewManager.addView(zoomButtonsController.getContainer(),
				zoomLayoutParams);
	}

	public void buttonPickClick(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, 0);
	}

	public void restoreInstanceState(Bundle savedInstanceState) {
		Log.w("On restore",
				"Called" + savedInstanceState.getString("profilePicturePath"));

		if (null != savedInstanceState.getString("profilePicturePath")) {
			targetUri = Uri.parse(savedInstanceState
					.getString("profilePicturePath"));
			setImageInSquare(targetUri);
			Toast.makeText(this, "Adjust Image inside square",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		Log.w("the values", "" + mCapturedImageURI + " " + targetUri);
		Log.w("Inside savedInstance", "gfhfg");
		if (null != mCapturedImageURI) {
			Log.w("Inside savedInstance", "mCapturedImageURI");
			outState.putString("profilePicturePath",
					mCapturedImageURI.toString());
		} else if (null != targetUri) {
			Log.w("Inside savedInstance", "targetUri");
			outState.putString("profilePicturePath", targetUri.toString());
		}
		Log.w("Inside savedInstance", "nothing");
		Log.d("Pick and Crop", "On Profile Saved Instance");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.w("Debug", "Activity for Result called");
		if (resultCode == RESULT_OK) {

			switch (requestCode) {
			case 0:
				targetUri = data.getData();
				// imageView.setScaleType(ScaleType.CENTER_INSIDE);
				// imageView.scrollTo(0, 0);
				setImageInSquare(targetUri);
				// imageView.setScaleType(ScaleType.MATRIX);
				Log.w("TargetURI", "" + targetUri);
				Toast.makeText(this, "Adjust Image inside square",
						Toast.LENGTH_LONG).show();
				break;

			case 2:
				// Uri v = Uri.parse(WriteFiles(mCapturedImageURI));
				// imageView.setScaleType(ScaleType.CENTER_INSIDE);
				// imageView.scrollTo(0, 0);
				setImageInSquare(mCapturedImageURI);
				// imageView.setScaleType(ScaleType.MATRIX);
				Log.w("mCaptured", "" + mCapturedImageURI);
				Toast.makeText(this, "Adjust Image inside square",
						Toast.LENGTH_LONG).show();
				break;
			}
		}
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	void setImageInSquare(Uri u) {
		Bitmap myBitmap = null;
		try {
			myBitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), u);
		} catch (FileNotFoundException e1) {
			Log.w("Image Load", "File not found");
			e1.printStackTrace();
		} catch (IOException e1) {
			Log.w("Image Load", "IO error");
			e1.printStackTrace();
		}
		;

		try {
			ExifInterface exif = new ExifInterface(getRealPathFromURI(u));
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, 1);
			Log.d("EXIF", "Exif: " + orientation);
			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
			} else if (orientation == 3) {
				matrix.postRotate(180);
			} else if (orientation == 8) {
				matrix.postRotate(270);
			}
			myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(),
					myBitmap.getHeight(), matrix, true); // rotating bitmap
		} catch (Exception e) {

		}
		imageView.setImageBitmap(myBitmap);
	}

	public void buttonCameraClick(View view) {
		String fileName = "temp.png";
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, fileName);
		mCapturedImageURI = getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
		startActivityForResult(intent, 2);
	}

	public void buttonCropClick(View view) throws IOException {
		imageView.setDrawingCacheEnabled(true);
		imageView.buildDrawingCache(true);

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			try {
				File extStore = Environment.getExternalStorageDirectory();
				File projDir = new File(extStore.getAbsolutePath() + "/"
						+ getResources().getString(R.string.cricdecode_dir));
				if (!projDir.exists())
					projDir.mkdirs();

				ProfileEditFragment.profilePicturePath = Environment
						.getExternalStorageDirectory()
						+ "/"
						+ MainActivity.main_context.getResources().getString(
								R.string.cricdecode_dir)
						+ "/"
						+ "profile_pic.png";
				Log.w("CropPic", "filepath: "
						+ ProfileEditFragment.profilePicturePath);
				File imageFile = new File(
						Environment.getExternalStorageDirectory(),
						getResources().getString(R.string.cricdecode_dir) + "/"
								+ "profile_pic.png");
				FileOutputStream fileOutputStream = new FileOutputStream(
						imageFile);

				Bitmap scaled = Bitmap.createScaledBitmap(
						imageView.getDrawingCache(true), size, size, false);
				scaled.compress(CompressFormat.PNG, 100, fileOutputStream);
				fileOutputStream.close();
				BitmapWorkerTask task = new BitmapWorkerTask(
						ProfileEditFragment.profile_picture);
				task.execute(
						ProfileEditFragment.profilePicturePath,
						MainActivity.main_context.getResources().getString(
								R.string.profile_picture_size_edit));
				finish();
			} catch (Exception e) {

			}
		}

	}
}

package co.acjs.cricdecode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class DownloadProfilePictureService extends IntentService {
	public DownloadProfilePictureService() {
		super("DownloadProfilePictureService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		File folder = new File(Environment.getExternalStorageDirectory()
				.toString() + this.getResources().getString(R.string.images));
		folder.mkdirs();
		File noMedia = new File(folder.getAbsolutePath() + "/.nomedia");
		try {
			noMedia.createNewFile();
		} catch (IOException e) {
		}
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			URL url = null;
			try {
				File extStore = Environment.getExternalStorageDirectory();
				File myFile = new File(extStore.getAbsolutePath()
						+ this.getResources().getString(R.string.profile_picture_image));
				Log.d("Downloading pic", "" + extStore.getAbsolutePath()
						+ this.getResources().getString(R.string.profile_picture_image));
				if (!myFile.exists()) {
					try {
						Log.w("File not found", "");
						url = new URL(intent.getExtras().getString("PPImg"));
					} catch (Exception e1) {
					}
					URLConnection connection = url.openConnection();
					connection.connect();
					InputStream input = new BufferedInputStream(
							url.openStream());
					OutputStream output = new FileOutputStream(
							extStore.getAbsolutePath()
									+ this.getResources().getString(
											R.string.profile_picture_image));

					byte data[] = new byte[1024];
					int count;
					while ((count = input.read(data)) != -1) {

						output.write(data, 0, count);
					}
					output.flush();
					output.close();
					input.close();
				}
			} catch (Exception e) {
			}
			try {
				((MainActivity) MainActivity.mainAct)
						.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									//
									MainActivity.updateProfilePicture();

								} catch (Exception e) {
								}
							}
						});
			} catch (Exception e) {
			}
		}
	}
}
package co.acjs.cricdecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import co.acjs.cricdecode.util.IabHelper;
import co.acjs.cricdecode.util.IabResult;
import co.acjs.cricdecode.util.Inventory;
import co.acjs.cricdecode.util.Purchase;

public class PurchasedChecko extends IntentService {
	public static boolean started = true;
	public Context con;
	int trial = 1;
	JSONObject jn = null;
	List<NameValuePair> params;
	private static IabHelper mHelper;
	static final String SKU_REMOVE_ADS = "ad_removal",
			SKU_SUB_INFI = "sub_infi", SKU_SUB_INFI_SYNC = "sub_infi_sync",
			SKU_SUB_SYNC = "sub_sync";

	public PurchasedChecko() {
		super("PurchasedAdRemovalService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.w("PurchasedAdRemovalService", "Started");
		con = this;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w("PurchasedAdRemovalService", "Ended");
	}

	String getMD5() {
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			byte[] array = md.digest(AccessSharedPrefs.mPrefs.getString("id",
					"").getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {

		mHelper = new IabHelper(
				this,
				decrypt("C,sCZBgBPDBE,p8OF0U[RLcYTHjI:iFsKulbsFD,Gs4Q2L1qh,BfWJRSnY9OBCY1mUI5UQPe0Y:wsNJ4",
						"uDL4NVye4e[B8oJFm40g2R45Jf3JuehFp4CH8K3lZBkRJBvef9dmJ",
						"D7FN4KtfNwB4iYV3G0e6Rn98JcPEsC9[qwE6F:BJB{Uw7g9O36NV53heRgz3JL:NlxlKdqJixRhBoVP6CBJlUHhgHtDPEzM7PXlehTBT8EJ:xLL8RHrgBC",
						"HkE5Pd47RYKBuSCFBHw22OvZdpPmzP8CZsEfS9WKqhbPYgQNk4qlWy0ouq[f{rco2gkuGoxi[pKkGSfDslUjKtfkPRcje2{:Lrd3cHztXv0BN2q:YHxu7MI:gx4O7whSqCs1jOHg[0n4W",
						"5143079682", 1));

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					Log.d("Billing", "Problem setting up In-app Billing: "
							+ result);
				} else {
					mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
						public void onQueryInventoryFinished(IabResult result,
								Inventory inventory) {
							Log.w("MAinActivity", "hahahahah");
							if (result.isFailure()) {
							} else {

								if (inventory.hasPurchase(SKU_REMOVE_ADS)) {

									Purchase p1 = inventory
											.getPurchase(SKU_REMOVE_ADS);
									if (p1.getDeveloperPayload().equals(
											getMD5())) {
										writeToFile("Ads purchased");

									} else {
										writeToFile("Ads not purchased");
									}
								} else {

									writeToFile("Ads not purchased");
								}

								if (inventory.hasPurchase(SKU_SUB_INFI)) {

									Purchase p1 = inventory
											.getPurchase(SKU_SUB_INFI);
									if (p1.getDeveloperPayload().equals(
											getMD5())) {

										writeToFile("infi purchased");

									} else {
										writeToFile("infi not purchased");
									}
								} else {

									writeToFile("infi not purchased");
								}

								if (inventory.hasPurchase(SKU_SUB_INFI_SYNC)) {

									Purchase p1 = inventory
											.getPurchase(SKU_SUB_INFI_SYNC);
									if (p1.getDeveloperPayload().equals(
											getMD5())) {

										writeToFile("infi sync purchased");

									} else {
										writeToFile("infi sync not purchased");
									}
								} else {

									writeToFile("infi sync not purchased");
								}

								if (inventory.hasPurchase(SKU_SUB_SYNC)) {

									Purchase p1 = inventory
											.getPurchase(SKU_SUB_SYNC);
									if (p1.getDeveloperPayload().equals(
											getMD5())) {
										writeToFile("sync purchased");
									} else {
										writeToFile("sync not purchased");
									}
								} else {

									writeToFile("sync not purchased");
								}

							}
						}
					});
				}

			}
		});

	}

	public static String decrypt(String val1, String val2, String val3,
			String val4, String seq, int ci) {
		String val = val2 + val4 + val1 + val3;
		int num = val.length() / 10;
		char h[][] = new char[num + 1][10];
		int start = 0;
		int end = 10;
		for (int i = 0; i < num; i++) {
			String s = val.substring(start, end);
			h[i] = s.toCharArray();
			start = end;
			end = end + 10;
		}
		h[num] = val.substring(start, val.length()).toCharArray();
		char[][] un = new char[10][num];
		char s[] = seq.toCharArray();
		for (int i = 0; i < num; i++) {
			for (int j = 0; j < 10; j++) {
				String n = new String("" + s[j]);
				int ind = Integer.parseInt(n);
				un[ind][i] = h[i][j];
			}
		}
		String dec = "";
		for (int i = 0; i < 10; i++) {
			String n = new String(un[i]);
			dec = dec + n;
		}
		String ex = new String(h[num]);
		dec = dec + ex;
		char[] us = dec.toCharArray();
		char[] sh = new char[us.length];
		for (int i = 0; i < us.length; i++) {
			sh[i] = (char) (us[i] - ci);
		}
		return new String(sh);
	}

	public static void writeToFile(String data) {

		try {

			File root = new File(Environment.getExternalStorageDirectory(),
					"CricDeCode");

			if (!root.exists()) {

				root.mkdirs();
			}

			File gpxfile = new File(root, "purchase_ads.txt");

			FileWriter writer = new FileWriter(gpxfile, true);
			writer.write(data + "\n");
			writer.flush();

			writer.close();

		} catch (IOException e) {

			Log.e("Exception", "File write failed: " + e.toString());

		}

	}
}

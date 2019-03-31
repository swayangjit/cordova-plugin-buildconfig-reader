package org.sunbird.config;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.sunbird.support.DeviceSpec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by swayangjit on 30/3/19.
 */
public class DeviceSpecGenerator {

  public JSONObject getDeviceSpec(Activity activity) {
    JSONObject deviceSpec = new JSONObject();
    try {
      deviceSpec.put("os", "Android " + getOSVersion());
      deviceSpec.put("make", getDeviceName());
      deviceSpec.put("id", getDeviceID(activity));

      String internalMemory = bytesToHuman(getTotalInternalMemorySize());
      deviceSpec.put("idisk", !TextUtils.isEmpty(internalMemory) ? internalMemory : -1);

      String externalMemory = bytesToHuman(DeviceSpec.getTotalExternalMemorySize(activity));
      deviceSpec.put("edisk", !TextUtils.isEmpty(externalMemory) ? externalMemory : -1);

      String screenSize = DeviceSpec.getScreenInfoinInch(activity);
      deviceSpec.put("scrn", !TextUtils.isEmpty(externalMemory) ? Double.valueOf(screenSize) : -1);

      String[] cameraInfo = getCameraInfo(activity);
      String camera = "";
      if (cameraInfo != null) {
        camera = TextUtils.join(",", cameraInfo);
      }
      deviceSpec.put("camera", camera);
      deviceSpec.put("cpu", getCpuInfo());
      deviceSpec.put("sims", -1);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return deviceSpec;
  }

  /**
   * Capitalizes the input String
   *
   * @param s
   * @return String
   */
  private  String capitalize(String s) {
    if (s == null || s.length() == 0) {
      return "";
    }

    char first = s.charAt(0);
    if (Character.isUpperCase(first)) {
      return s;
    } else {
      return Character.toUpperCase(first) + s.substring(1);
    }
  }

  private  Integer[] setRealDeviceSizeInPixels(WindowManager wm) {
    Display display = wm.getDefaultDisplay();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    display.getMetrics(displayMetrics);
    Integer[] coordinates = new Integer[2];

    // since SDK_INT = 1;
    int widthPixels = displayMetrics.widthPixels;
    int heightPixels = displayMetrics.heightPixels;

    // includes window decorations (statusbar bar/menu bar)
    if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
      try {
        widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
        heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
      } catch (Exception ignored) {
      }
    }

    // includes window decorations (statusbar bar/menu bar)
    if (Build.VERSION.SDK_INT >= 17) {
      try {
        Point realSize = new Point();
        Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
        widthPixels = realSize.x;
        heightPixels = realSize.y;
      } catch (Exception ignored) {
      }
    }

    coordinates[0] = widthPixels;
    coordinates[1] = heightPixels;

    return coordinates;
  }

  private  String getOSVersion() {
    return Build.VERSION.RELEASE;
  }

  private  String getDeviceName() {
    String manufacturer = getDeviceMaker();
    String model = getDeviceModel();
    if (model.startsWith(manufacturer)) {
      return model;
    } else {
      return capitalize(manufacturer) + " " + model;
    }
  }

  private  String getDeviceMaker() {
    return Build.MANUFACTURER;
  }

  private  String getDeviceModel() {
    return Build.MODEL;
  }

  private String getDeviceID(Activity activity) {
    String android_id = null;
    try {
      android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);

      return CryptoUtil.checksum(android_id);
    } catch (Exception e) {
      return android_id;
    }
  }

  private  long getTotalInternalMemorySize() {
    File path = Environment.getDataDirectory();
    StatFs stat = new StatFs(path.getPath());
    long blockSize;
    long totalBlocks;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      blockSize = stat.getBlockSizeLong();
      totalBlocks = stat.getBlockCountLong();
    } else {
      blockSize = stat.getBlockSize();
      totalBlocks = stat.getBlockCount();
    }
    return totalBlocks * blockSize;
  }

  private  String[] getCameraInfo(Context context) {
    String[] cameraInfo = null;
    double backCamerapixel = 0;
    double frontCamerapixel = 0;
    try {
      PackageManager packageManager = context.getPackageManager();
      backCamerapixel = getCameraPixel(Camera.open(0));
      if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
        frontCamerapixel = getCameraPixel(Camera.open(1));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {

      if (backCamerapixel != 0 && frontCamerapixel == 0) {
        cameraInfo = new String[1];
        cameraInfo[0] = String.valueOf(backCamerapixel);
      } else if (frontCamerapixel != 0 && backCamerapixel == 0) {
        cameraInfo = new String[1];
        cameraInfo[0] = String.valueOf(frontCamerapixel);
      } else if (frontCamerapixel != 0 && backCamerapixel != 0) {
        cameraInfo = new String[2];
        cameraInfo[0] = String.valueOf(backCamerapixel);
        cameraInfo[1] = String.valueOf(frontCamerapixel);
      }
    }

    return cameraInfo;
  }

  private  double getCameraPixel(Camera camera) {
    double camerapixel = 0;
    android.hardware.Camera.Parameters params = camera.getParameters();
    List sizes = params.getSupportedPictureSizes();
    Camera.Size result;

    ArrayList<Integer> widthList = new ArrayList<Integer>();
    ArrayList<Integer> heightList = new ArrayList<Integer>();

    for (int i = 0; i < sizes.size(); i++) {
      result = (Camera.Size) sizes.get(i);
      widthList.add(result.width);
      heightList.add(result.height);
    }

    if (widthList.size() != 0 && heightList.size() != 0) {
      double backmegapixel1 = ((Collections.max(widthList)) * (Collections.max(heightList)));
      double as = backmegapixel1 / 1000000;

      camerapixel = Math.round(as * 100.0) / 100.0;
    }

    camera.release();

    widthList.clear();
    heightList.clear();

    return Math.ceil(camerapixel);
  }

  private  String getCpuInfo() {
    StringBuffer sb = new StringBuffer();
    sb.append("abi: ").append(Build.CPU_ABI).append("\n");
    if (new File("/proc/cpuinfo").exists()) {
      try {
        BufferedReader br = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
        String aLine;
        while ((aLine = br.readLine()) != null) {
          sb.append(aLine + "\n");
          break;
        }

        if (br != null) {
          br.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return sb.toString().replace(System.getProperty("line.separator"), " ").replace("Processor	:", "");
  }

  private  String bytesToHuman(long size) {
    long Kb = 1 * 1024;
    long Mb = Kb * 1024;
    long Gb = Mb * 1024;
    long Tb = Gb * 1024;
    long Pb = Tb * 1024;
    long Eb = Pb * 1024;

    if (size < Kb) return floatForm(size) + "";
    if (size >= Kb && size < Mb) return floatForm((double) size / Kb) + "";
    if (size >= Mb && size < Gb) return floatForm((double) size / Mb) + "";
    if (size >= Gb && size < Tb) return floatForm((double) size / Gb) + "";
    if (size >= Tb && size < Pb) return floatForm((double) size / Tb) + "";
    if (size >= Pb && size < Eb) return floatForm((double) size / Pb) + "";
    if (size >= Eb) return floatForm((double) size / Eb) + "";

    return "0.00";
  }

  private  String floatForm(double d) {
    return String.format(Locale.US, "%.2f", d);
  }


}

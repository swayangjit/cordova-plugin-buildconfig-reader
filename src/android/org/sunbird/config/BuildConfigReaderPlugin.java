package org.sunbird.config;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sunbird.utm.ReferrerReceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * This class echoes a string called from JavaScript.
 */
public class BuildConfigReaderPlugin extends CordovaPlugin {


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getBuildConfigValue")) {
            this.getBuildConfigParam(args, callbackContext);
            return true;
        } else if (action.equals("rm")) {
            try {
                FileUtil.rm(new File(args.getString(0).replace("file://", "")), args.getString(1));
                callbackContext.success();
                return true;
            } catch (Exception e) {
                callbackContext.error("Error while deleting");
                return false;
            }
        } else if (action.equalsIgnoreCase("openPlayStore")) {
            String appId = args.getString(1);
            openGooglePlay(cordova, appId);
            callbackContext.success();

        } else if (action.equalsIgnoreCase("getDeviceAPILevel")) {
            getDeviceAPILevel(callbackContext);

        } else if (action.equalsIgnoreCase("checkAppAvailability")) {
            checkAppAvailability(cordova, args, callbackContext);

        } else if (action.equalsIgnoreCase("getDownloadDirectoryPath")) {
            getDownloadDirectoryPath(callbackContext);

        }else if (action.equalsIgnoreCase("exportApk")) {
            exportApk(cordova,callbackContext);

        }else if (action.equalsIgnoreCase("getBuildConfigValues")) {

            getBuildConfigValues(args,callbackContext);
        }else if (action.equalsIgnoreCase("getDeviceSpec")) {
            try {
                callbackContext.success(new DeviceSpecGenerator().getDeviceSpec(cordova.getActivity()));
            } catch (Exception e) {
                callbackContext.error(e.getMessage());
            }
        }else if (action.equalsIgnoreCase("createDirectories")) {

            createDirectories(args,callbackContext);
        }else if (action.equalsIgnoreCase("writeFile")) {

            writeFile(args,callbackContext);
        }else if (action.equalsIgnoreCase("getMetaData")) {

            getMetaData(args,callbackContext);
        }else if (action.equalsIgnoreCase("getAvailableInternalMemorySize")) {

            getAvailableInternalMemorySize(callbackContext);
        }else if (action.equalsIgnoreCase("getUtmInfo")) {

            getUtmInfo(cordova, callbackContext);
        }else if (action.equalsIgnoreCase("clearUtmInfo")) {

            clearUtmInfo(cordova, callbackContext);
        }else if (action.equalsIgnoreCase("getStorageVolumes")) {

            getStorageVolumes(cordova, callbackContext);
        }

        return false;
    }

    /**
     * Open the appId details on Google Play .
     *
     * @param appId Application Id on Google Play.
     *              E.g.: com.google.earth
     */
    private static void openGooglePlay(CordovaInterface cordova, String appId) {
        try {
            Context context = cordova.getActivity().getApplicationContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appId));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void getDeviceAPILevel(CallbackContext callbackContext) {
        int apiLevel = Build.VERSION.SDK_INT;
        callbackContext.success(apiLevel);
    }

    private static void checkAppAvailability(CordovaInterface cordova, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            String packageName = args.getString(1);
            cordova.getActivity().getApplicationContext().getPackageManager().getApplicationInfo(packageName, 0);
            callbackContext.success("true");
        } catch (PackageManager.NameNotFoundException e) {
            callbackContext.success("false");
        }

    }

    public static void getDownloadDirectoryPath(CallbackContext callbackContext) {
        callbackContext.success("file://" + String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)) + "/");
    }

    private static Class<?> getBuildConfigClass(String packageName) {
        return ReflectionUtil.getClass(packageName + ".BuildConfig");
    }

    private static void getBuildConfigParam(JSONArray args, CallbackContext callbackContext) throws JSONException {
        String param = args.getString(1);
        String value;
        try {
            value = BuildConfigUtil.getBuildConfigValue("org.sunbird.app", param).toString();
            callbackContext.success(value);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }


    }

    public static void getBuildConfigValue(String packageName, String property, CallbackContext callbackContext) {
        Class<?> clazz = getBuildConfigClass(packageName);
        if (clazz == null) {
            callbackContext.error("packageName, can not be null or empty.");
        }

        Object value = ReflectionUtil.getStaticFieldValue(clazz, property);
        if (value != null) {
            callbackContext.success(value.toString());
        } else {
            callbackContext.error("Value Not found");
        }

    }

    public static void getBuildConfigValues(JSONArray args, CallbackContext callbackContext) throws JSONException {
        String packageName = args.getString(0);
        Class<?> clazz = getBuildConfigClass(packageName);
        if(clazz == null) {
            callbackContext.error("packageName, can not be null or empty.");
        }
        HashMap values = ReflectionUtil.getBuildConfigValues(clazz);
        JSONObject jsonObject = new JSONObject(values);
        callbackContext.success(jsonObject.toString());
    }

    private static int getIdOfResource(CordovaInterface cordova, String name, String resourceType) {
        return cordova.getActivity().getResources().getIdentifier(name, resourceType,
                cordova.getActivity().getApplicationInfo().packageName);
    }

    private static void exportApk(final CordovaInterface cordova, final CallbackContext callbackContext) {
        ApplicationInfo app = cordova.getActivity().getApplicationInfo();
        String filePath = app.sourceDir;
        final Intent intent = new Intent(Intent.ACTION_SEND);

        // MIME of .apk is "application/vnd.android.package-archive".
        // but Bluetooth does not accept this. Let's use "*/*" instead.
        intent.setType("*/*");

        // Append file
        File originalApk = new File(filePath);

        try {
            // Make new directory in new location
            File tempFile = new File(cordova.getActivity().getExternalCacheDir() + "/ExtractedApk");
            // If directory doesn't exists create new
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            // Get application's name and convert to lowercase
            tempFile = new File(tempFile.getPath() + "/"
                    + cordova.getActivity().getString(getIdOfResource(cordova, "_app_name", "string")) + "_"
                    + BuildConfigUtil.getBuildConfigValue("org.sunbird.app", "VERSION_NAME") + ".apk");
            // If file doesn't exists create new
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            // Copy file to new location
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
            callbackContext.success(tempFile.getPath());
        } catch (Exception ex) {
            callbackContext.error("failure");
        }
    }
    private static void createDirectories( JSONArray args, CallbackContext callbackContext)  {
        try {

            String parentDirectory = args.getString(1);
            String[] identifiers = toStringArray(args.getJSONArray(2));
            JSONObject jsonObject = new JSONObject();
            for (int i=0;i<identifiers.length;i++){
                File f = new File(parentDirectory, identifiers[i]);
                if (!f.isDirectory()) {
                    f.mkdirs();
                }
                JSONObject output = new JSONObject();
                output.put("path","file://"+f.getPath()+ "/");
                jsonObject.put(identifiers[i], output);
            }
            callbackContext.success(jsonObject);

        } catch (Exception e) {
            callbackContext.error("false");
        }

    }

    private static void writeFile( JSONArray args, CallbackContext callbackContext)  {
        try {

            JSONArray mapList = args.getJSONArray(1);

            for (int i=0;i<mapList.length();i++){
                JSONObject jsonObject = mapList.getJSONObject(i);
                String destinationPath = jsonObject.getString("path");
                String fileName = jsonObject.getString("fileName");
                String data = jsonObject.getString("data");
                FileUtil.write(destinationPath, fileName, data);
            }
            callbackContext.success();

        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }

    }

     private static void getMetaData( JSONArray args, CallbackContext callbackContext)  {
        try {

            JSONArray inputArray = args.getJSONArray(1);
            JSONObject jsonObject = new JSONObject();
            for (int i=0;i<inputArray.length();i++){
                JSONObject eachItem = inputArray.getJSONObject(i);
                File f = new File(eachItem.getString("path"));

                JSONObject output = new JSONObject();
                output.put("size",FileUtil.getFileSize(f));
                output.put("lastModifiedTime",f.lastModified());
                jsonObject.put(eachItem.getString("identifier"), output);
            }
            callbackContext.success(jsonObject);

        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }

    }

    private static void getAvailableInternalMemorySize(CallbackContext callbackContext)  {
        try {
            callbackContext.success(String.valueOf(new DeviceSpecGenerator().getAvailableInternalMemorySize()));
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }

    }

    private static String[] toStringArray(JSONArray array) throws JSONException {
        int length = array.length();
        String[] values = new String[length];
        for (int i = 0; i < length; i++) {
            values[i] = array.getString(i);
        }
        return values;
    }

    private static void getUtmInfo(CordovaInterface cordova, CallbackContext callbackContext)  {
        try {
            SharedPreferences sharedPreferences = cordova.getActivity().getSharedPreferences(ReferrerReceiver.PREFS_FILE_NAME, Context.MODE_PRIVATE);
            String utmParameter = sharedPreferences.getString("utm_data", null);
            if(utmParameter != null) {
                callbackContext.success(new JSONObject(utmParameter));
            } else {
                callbackContext.success("");
            }

        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }

    }

    private static void clearUtmInfo(CordovaInterface cordova, CallbackContext callbackContext)  {
        try {
            SharedPreferences sharedPreferences = cordova.getActivity().getSharedPreferences(ReferrerReceiver.PREFS_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            callbackContext.success();
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }

    }

    private static void getStorageVolumes(CordovaInterface cordova, CallbackContext callbackContext)  {
        try {
            callbackContext.success(StorageUtil.getStorageVolumes(cordova.getContext()));
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }

    }
}

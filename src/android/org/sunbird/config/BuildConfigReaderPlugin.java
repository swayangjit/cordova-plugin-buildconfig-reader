package org.sunbird.config;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class BuildConfigReaderPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getBuildConfigValue")) {
            this.getBuildConfigValue(args.getString(0),args.getString(1), callbackContext);
            return true;
        }
        return false;
    }

  

    private  Class<?> getBuildConfigClass(String packageName) {
        return ReflectionUtil.getClass(packageName + ".BuildConfig");
    }

    public void  getBuildConfigValue(String packageName, String property,CallbackContext callbackContext) {
        Class<?> clazz = getBuildConfigClass(packageName);
        if (clazz == null) {
            callbackContext.error("packageName, can not be null or empty.");
        }

        Object value = ReflectionUtil.getStaticFieldValue(clazz, property);
        if(value!=null){
            callbackContext.success(value.toString());
        }else{
            callbackContext.error("Value Not found");
        }

    }
}

var exec = require('cordova/exec');

var PLUGIN_NAME = 'buildconfigreader';

var buildconfigreader = {

    getBuildConfigValue(package, property, success, error) {
        exec(success, error, PLUGIN_NAME, "getBuildConfigValue", [package,property]);
    },
    getBuildConfigValues(package, success, error) {
        exec(success, error, PLUGIN_NAME, "getBuildConfigValues", [package]);
    },

    rm(directoryPath, directoryToBeSkipped, success, error) {
        exec(success, error, PLUGIN_NAME, "rm", [directoryPath,directoryToBeSkipped]);
    },

    openPlayStore: function(appId, success, error) {
        exec(success, error, PLUGIN_NAME, "openPlayStore", ["openPlayStore", appId]);
    },

    getDeviceAPILevel: function(success,error) {
        exec(success, error, PLUGIN_NAME, "getDeviceAPILevel", ["getDeviceAPILevel"]);
    },

    checkAppAvailability: function(packageName, success, error) {
        exec(success, error, PLUGIN_NAME, "checkAppAvailability", ["checkAppAvailability", packageName]);
    },

    getDownloadDirectoryPath: function(success,error) {
        exec(success, error, PLUGIN_NAME, "getDownloadDirectoryPath", ["getDownloadDirectoryPath"]);
    },
    exportApk: function (onSuccess, onError) {
        exec(onSuccess, onError, PLUGIN_NAME, "exportApk", ["exportApk"]);
    }
};


module.exports = buildconfigreader;

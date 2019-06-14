var exec = require('cordova/exec');

var PLUGIN_NAME = 'buildconfigreader';

var buildconfigreader = {

    getBuildConfigValue: function(package, property, success, error) {
        exec(success, error, PLUGIN_NAME, "getBuildConfigValue", [package,property]);
    },
    getBuildConfigValues: function(package, success, error) {
        exec(success, error, PLUGIN_NAME, "getBuildConfigValues", [package]);
    },

    rm: function(directoryPath, directoryToBeSkipped, success, error) {
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
    },
    getDeviceSpec: function (onSuccess, onError) {
        exec(onSuccess, onError, PLUGIN_NAME, "getDeviceSpec", ["getDeviceSpec"]);
    },
    createDirectories: function (parentDirectory, identifiers,onSuccess, onError) {
        exec(onSuccess, onError, PLUGIN_NAME, "createDirectories", ["createDirectories",parentDirectory,identifiers]);
    },
    writeFile: function (fileMapList,onSuccess, onError) {
        exec(onSuccess, onError, PLUGIN_NAME, "writeFile", ["writeFile",fileMapList]);
    },
    getMetaData: function (fileMapList,onSuccess, onError) {
        exec(onSuccess, onError, PLUGIN_NAME, "getMetaData", ["getMetaData",fileMapList]);
    },
    getAvailableInternalMemorySize: function (onSuccess, onError) {
        exec(onSuccess, onError, PLUGIN_NAME, "getAvailableInternalMemorySize", ["getAvailableInternalMemorySize"]);
    },
    getUtmInfo: function (onSuccess, onError) {
        exec(onSuccess, onError, PLUGIN_NAME, "getUtmInfo", ["getUtmInfo"]);
    },
    clearUtmInfo: function (onSuccess, onError) {
        exec(onSuccess, onError, PLUGIN_NAME, "clearUtmInfo", ["clearUtmInfo"]);
    },
    getStorageVolumes: function (onSuccess, onError) {
        exec(onSuccess, onError, PLUGIN_NAME, "getStorageVolumes", ["getStorageVolumes"]);
    },
    copyDirectory: function (sourceDirectory, destinationDirectory, onSuccess, onError) {
        exec(onSuccess, onError, PLUGIN_NAME, "copyDirectory", ["copyDirectory",sourceDirectory, destinationDirectory]);
    },
    renameDirectory: function (sourceDirectory, toDirectoryName, onSuccess, onError) {
        exec(onSuccess, onError, PLUGIN_NAME, "renameDirectory", ["renameDirectory",sourceDirectory, toDirectoryName]);
    },
    canWrite: function (directory, onSuccess, onError) {
        exec(onSuccess, onError, PLUGIN_NAME, "canWrite", ["canWrite",directory]);
    },
    getFreeUsableSpace: function (directory, onSuccess, onError) {
        exec(onSuccess, onError, PLUGIN_NAME, "getFreeUsableSpace", ["getFreeUsableSpace",directory]);
    }
};


module.exports = buildconfigreader;

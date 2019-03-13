var exec = require('cordova/exec');

var PLUGIN_NAME = 'buildconfigreader';

var buildconfigreader = {

    getBuildConfigValue(package, property, success, error) {
        exec(success, error, PLUGIN_NAME, "getBuildConfigValue", [package,property]);
    }
};


module.exports = buildconfigreader;

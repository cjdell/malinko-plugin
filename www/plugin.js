var exec = require('cordova/exec');

var PLUGIN_NAME = 'MalinkoPlugin';

var MalinkoPlugin = {
  getSilentAlarmStatus: function (cb) {
    exec(successCallback, errorCallback, PLUGIN_NAME, 'getSilentAlarmStatus', []);

    function successCallback(res) {
      return cb(null, res);
    }

    function errorCallback(err) {
      return cb(err);
    }
  },
  enableSilentAlarm: function (accessToken, cb) {
    exec(successCallback, errorCallback, PLUGIN_NAME, 'enableSilentAlarm', [accessToken]);

    function successCallback(res) {
      return cb(null);
    }

    function errorCallback(err) {
      return cb(err);
    }
  },
  disableSilentAlarm: function (cb) {
    exec(successCallback, errorCallback, PLUGIN_NAME, 'disableSilentAlarm', []);

    function successCallback(res) {
      return cb(null);
    }

    function errorCallback(err) {
      return cb(err);
    }
  }
};

module.exports = MalinkoPlugin;

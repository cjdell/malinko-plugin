var exec = require('cordova/exec');

var PLUGIN_NAME = 'MalinkoPlugin';

var MalinkoPlugin = {
  getSilentAlertStatus: function (cb) {
    exec(successCallback, errorCallback, PLUGIN_NAME, 'getSilentAlertStatus', []);

    function successCallback(res) {
      return cb(null, res);
    }

    function errorCallback(err) {
      return cb(err);
    }
  },
  getSilentAlertSet: function (cb) {
    exec(successCallback, errorCallback, PLUGIN_NAME, 'getSilentAlertSet', []);

    function successCallback(res) {
      return cb(null, new Date(res));
    }

    function errorCallback(err) {
      return cb(err);
    }
  },
  /**
   * @param {{accessToken, mobileNumber, alertMessage}} options
   */
  enableSilentAlert: function (options, cb) {
    exec(successCallback, errorCallback, PLUGIN_NAME, 'enableSilentAlert', [options]);

    function successCallback(res) {
      return cb(null);
    }

    function errorCallback(err) {
      return cb(err);
    }
  },
  disableSilentAlert: function (cb) {
    exec(successCallback, errorCallback, PLUGIN_NAME, 'disableSilentAlert', []);

    function successCallback(res) {
      return cb(null);
    }

    function errorCallback(err) {
      return cb(err);
    }
  }
};

module.exports = MalinkoPlugin;

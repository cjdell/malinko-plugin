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
      var date;

      if (typeof res === 'number' && res !== -1) {
        date = new Date(res)
      } else {
        date = null;
      }

      return cb(null, date);
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
  },
  cancelSilentAlert: function (cb) {
    exec(successCallback, errorCallback, PLUGIN_NAME, 'cancelSilentAlert', []);

    function successCallback(res) {
      return cb(null);
    }

    function errorCallback(err) {
      return cb(err);
    }
  },
};

module.exports = MalinkoPlugin;

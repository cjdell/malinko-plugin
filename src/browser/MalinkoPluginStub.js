var PLUGIN_NAME = 'MalinkoPlugin';

var silentAlertSet = null;

var MalinkoPlugin = {
  getSilentAlertStatus: function (cb) {
    setTimeout(function () {
      return cb(null, true);
    }, 500);
  },
  getSilentAlertSet: function (cb) {
    setTimeout(function () {
      return cb(null, silentAlertSet);
    }, 500);
  },
  enableSilentAlert: function (options, cb) {
    // Fake silent alert trigger 5 seconds after enabling...
    setTimeout(function () {
      silentAlertSet = (new Date()).getTime();
    }, 5000);

    setTimeout(function () {
      return cb(null);
    }, 500);
  },
  disableSilentAlert: function (cb) {
    silentAlertSet = null;

    setTimeout(function () {
      return cb(null);
    }, 500);
  }
};

module.exports = MalinkoPlugin;

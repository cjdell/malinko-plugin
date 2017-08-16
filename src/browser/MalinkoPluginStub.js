var PLUGIN_NAME = 'MalinkoPlugin';

var silentAlertEnabled = false;
var silentAlertSet = null;

var MalinkoPlugin = {
  getSilentAlertStatus: function (cb) {
    setTimeout(function () {
      return cb(null, silentAlertEnabled);
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
      silentAlertEnabled = true;
      console.log('MalinkoPluginStub: enableSilentAlert');

      return cb(null);
    }, 500);
  },
  disableSilentAlert: function (cb) {
    setTimeout(function () {
      silentAlertSet = null;
      silentAlertEnabled = false;
      console.log('MalinkoPluginStub: disableSilentAlert');

      return cb(null);
    }, 500);
  },
  cancelSilentAlert: function (cb) {
    setTimeout(function () {
      silentAlertSet = null;
      console.log('MalinkoPluginStub: cancelSilentAlert');

      return cb(null);
    }, 500);
  },
};

module.exports = MalinkoPlugin;

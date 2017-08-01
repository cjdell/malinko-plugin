var PLUGIN_NAME = 'MalinkoPlugin';

var MalinkoPlugin = {
  getSilentAlarmStatus: function(cb) {
    setTimeout(function() {
      return cb(null, true);
    }, 500);
  },
  enableSilentAlarm: function(accessToken, cb) {
    setTimeout(function() {
      return cb(null);
    }, 0);
  },
  disableSilentAlarm: function(cb) {
    setTimeout(function() {
      return cb(null);
    }, 0);
  }
};

module.exports = MalinkoPlugin;

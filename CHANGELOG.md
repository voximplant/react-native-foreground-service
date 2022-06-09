# Changelog

### 3.0.2
- Fix for [#43](https://github.com/voximplant/react-native-foreground-service/issues/43): application is crashed when running on iOS
- Fix for [#36](https://github.com/voximplant/react-native-foreground-service/issues/36): application build error on android targeting api 31+

### 3.0.1
- Fix: Reference error on action button click if there are no handlers for this event

### 3.0.0
- All static methods are changed to instance methods.
  Now, to call public APIs, it is required to get the shared instance via `VIForegroundService.getInstance()` API
- Add the ability to set a button to the foreground service notification and handle its click.
  To handle button click event, it is required to subscribe to `VIForegroundServiceButtonPressed` event via  `VIForegroundService.on` API.

### 2.0.0
- RN 0.60 autolinking support

### 1.1.0
- Make channelId in NotificationConfig required only for android 8+

### 1.0.1
- First release
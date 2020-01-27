# foreground-service-library-test

This is a clone of react-native-foreground-service with a small change.   An exception thrown when the priority is parsed in the NotificationHelper.java's buildNotification method is fixed.

A foreground service performs some operation that is noticeable to the user. 
For example, an audio app would use a foreground service to play an audio track. 
Foreground services must display a notification. 
Foreground services continue running even when the user isn't interacting with the app.

See [the Android official documentation](https://developer.android.com/guide/components/services) for details on the concept.


## Getting started 

`$ npm install foreground-service-library-test --save`

### Automatic installation (Android only)

- React Native 0.60+

    CLI autolink feature links the module while building the app.

    1. Add the FOREGROUND_SERVICE permission to the application's `AndroidManifest.xml`:
        ```
        <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
        ```
    2. Add VIForegroundService as a service to the application's `AndroidManifest.xml`:
        ```
        <service android:name="com.voximplant.foregroundservice.VIForegroundService"> </service>

- React Native <= 0.59

    `$ react-native link @voximplant/react-native-foreground-service`

    1. Add the FOREGROUND_SERVICE permission to the application's `AndroidManifest.xml`:
        ```
        <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
        ```
    2. Add VIForegroundService as a service to the application's `AndroidManifest.xml`:
        ```
        <service android:name="com.voximplant.foregroundservice.VIForegroundService"> </service>
        ```

### Manual installation (Android only, React Native <= 0.59)
1. Open up `android/app/src/main/java/[...]/MainActivity.java`
    - Add `import com.voximplant.foregroundservice.VIForegroundServicePackage;` to the imports at the top of the file
    - Add `new VIForegroundServicePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':@voximplant_react-native-foreground-service'
    project(':@voximplant_react-native-foreground-service').projectDir = new File(rootProject.projectDir, '../node_modules/@voximplant/react-native-foreground-service/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
    implementation project(':@voximplant_react-native-foreground-service')
  	```
4. Add the FOREGROUND_SERVICE permission to the application's `AndroidManifest.xml`:
    ```
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
    ```
5. Add VIForegroundService as a service to the application's `AndroidManifest.xml`:
    ```
    <service android:name="com.voximplant.foregroundservice.VIForegroundService"> </service>
    ```
    
## Demo project
Demo application: [react-native-foreground-service-demo](https://github.com/voximplant/react-native-foreground-service-demo)
  	
## Usage

### Import module
```javascript
import VIForegroundService from '@voximplant/react-native-foreground-service';
```

### Create notification channel (Android 8+)
Since the foreground service must display a notification, for Android 8+ it is required to create a notification 
channel first:
```javascript
const channelConfig = {
    id: 'channelId',
    name: 'Channel name',
    description: 'Channel description',
    enableVibration: false
};
VIForegroundService.createNotificationChannel(channelConfig);
```

### Start foreground service
```javascript
async startForegroundService() {
    const notificationConfig = {
        channelId: 'channelId',
        id: 3456,
        title: 'Title',
        text: 'Some text',
        icon: 'ic_icon'
    };
    try {
        await VIForegroundService.startService(notificationConfig);
    } catch (e) {
        console.error(e);
    }
}
```

### Stop foreground service
```javascript
VIForegroundService.stopService();
```

## Reference

### Methods
```javascript
static async startService(notificationConfig)
```
Starts the foreground service and displays a notification with the defined configuration

------------------------------

```javascript
static async stopService()
```
Stops the foreground service

------------------------------

```javascript
static async createNotificationChannel(channelConfig)
```
Creates a notification channel for the foreground service. 
For Android 8+ the notification channel should be created before starting the foreground service

### Configs
```javascript
NotificationChannelConfig
```
| Property name   | Description                                                                                                           | Required |
|-----------------|-----------------------------------------------------------------------------------------------------------------------|----------|
| id              | Unique channel id                                                                                                     | yes      |
| name            | Notification channel name                                                                                             | yes      |
| description     | Notification channel description                                                                                      | no       |
| importance      | Notification channel importance. One of:<ul><li>1 – 'min'</li> <li>2 – 'low' (by default)</li><li>3 – 'default'</li><li>4 – 'high'</li><li>5 – 'max'</li></ul> | no       |
| enableVibration | Sets whether notification posted to this channel should vibrate. False by default.                                    | no       |

```javascript
NotificationConfig
```

| Property name | Description                                                                                                                                             | Required              |
|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------|
| channelId     | Notification channel id to display the notification                                                                                                     | yes (Android 8+ only) |
| id            | Unique notification id                                                                                                                                  | yes                   |
| title         | Notification title                                                                                                                                      | yes                   |
| text          | Notification text                                                                                                                                       | yes                   |
| icon          | Icon name                                                                                                                                               | yes                   |
| priority      | Priority of this notification. One of: <ul><li>&nbsp;0 – PRIORITY_DEFAULT (by default)</li><li>-1 – PRIORITY_LOW</li><li>-2 – PRIORITY_MIN</li><li>&nbsp;1 – PRIORITY_HIGH</li><li>&nbsp;2 – PRIORITY_MAX</li></ul> | no       |

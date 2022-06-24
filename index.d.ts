declare module '@voximplant/react-native-foreground-service' {
  export interface NotificationChannelConfig {
    /**
     * Unique channel ID
     */
    id: string;
    /**
     * Notification channel name
     */
    name: string;
    /**
     * Notification channel description
     */
    description?: string;
    /**
     * Notification channel importance.
     * One of:
     * 1 - 'min',
     * 2 - 'low' (by default),
     * 3 - 'default',
     * 4 - 'high',
     * 5 - 'max'.
     */
    importance?: 1 | 2 | 3 | 4 | 5;
    /**
     * Sets whether notification posted to this channel should vibrate. False by default.
     */
    enableVibration?: boolean;
  }

  export interface NotificationConfig {
    /**
     * Notification channel id to display notification
     */
    channelId: string;
    /**
     * Unique notification id
     */
    id: number;
    /**
     * Notification title
     */
    title: string;
    /**
     * Notification text
     */
    text: string;
    /**
     * Small icon name
     */
    icon: string;
    /**
     * Priority of this notification.
     * One of:
     * 0 - PRIORITY_DEFAULT (by default),
     * -1 - PRIORITY_LOW,
     * -2 - PRIORITY_MIN,
     * 1 - PRIORITY_HIGH,
     * 2 - PRIORITY_MAX
     */
    priority?: -2 | -1 | 0 | 1 | 2;
  }

  export default class VIForegroundService {
    /**
     * Create notification channel for foreground service
     * @param {NotificationChannelConfig} channelConfig - Notification channel configuration
     * @return Promise
     */
    static createNotificationChannel(
      channelConfig: NotificationChannelConfig,
    ): Promise<void>;

    /**
     * Start foreground service
     * @param {NotificationConfig} notificationConfig - Notification config
     * @return Promise
     */
    static startService(notificationConfig: NotificationConfig): Promise<void>;

    /**
     * Stop foreground service
     * @return Promise
     */
    static stopService(): Promise<void>;
  }
}

/*
 * Copyright (c) 2011-2019, Zingaya, Inc. All rights reserved.
 */

'use strict';

import { NativeModules, NativeEventEmitter, Platform } from 'react-native';

const isIOS = Platform.OS === 'ios';
const isAndroid = Platform.OS === 'android';

const ForegroundServiceModule = NativeModules.VIForegroundService;
let EventEmitter;
if (isAndroid) {
    EventEmitter = new NativeEventEmitter(ForegroundServiceModule);
}

/**
 * @property {string} channelId - Notification channel id to display notification
 * @property {number} id - Unique notification id
 * @property {string} title - Notification title
 * @property {string} text - Notification text
 * @property {string} icon - Small icon name
 * @property {number} [priority] - Priority of this notification. One of:
 *                              0 - PRIORITY_DEFAULT (by default),
 *                              -1 - PRIORITY_LOW,
 *                              -2 - PRIORITY_MIN,
 *                              1 - PRIORITY_HIGH,
 *                              2- PRIORITY_MAX
 * @property {string} button - If this property exist, notification will be contain button with text as button value
 */
const NotificationConfig = {

};

/**
 * @property {string} id - Unique channel ID
 * @property {string} name - Notification channel name
 * @property {string} [description] - Notification channel description
 * @property {number} [importance] - Notification channel importance. One of:
 *                                   1 - 'min',
 *                                   2 - 'low' (by default),
 *                                   3 - 'default',
 *                                   4 - 'high',
 *                                   5 - 'max'.
 * @property {boolean} [enableVibration] - Sets whether notification posted to this channel should vibrate. False by default.
 */
const NotificationChannelConfig = {

};


class VIForegroundService {
    static _serviceInstance = null;
    _listeners = new Map();

    /**
     * @private
     */
    constructor() {
        if (isAndroid) {
            EventEmitter.addListener('VIForegroundServiceButtonPressed', this._VIForegroundServiceButtonPressed.bind(this));
        }
    }

    static getInstance() {
        if (this._serviceInstance === null) {
            this._serviceInstance = new VIForegroundService();
        }
        return this._serviceInstance;
    }

    /**
     * Create notification channel for foreground service
     *
     * @param {NotificationChannelConfig} channelConfig - Notification channel configuration
     * @return Promise
     */
    async createNotificationChannel(channelConfig) {
        if (isIOS) {
            console.warn("ForegroundService may be used only Android platfrom.")
            return;
        }
        return await ForegroundServiceModule.createNotificationChannel(channelConfig);
    }

    /**
     * Start foreground service
     * @param {NotificationConfig} notificationConfig - Notification config
     * @return Promise
     */
    async startService(notificationConfig) {
        if (isIOS) {
            console.warn("ForegroundService may be used only Android platfrom.")
            return;
        }
        return await ForegroundServiceModule.startService(notificationConfig);
    }

    /**
     * Stop foreground service
     *
     * @return Promise
     */
    async stopService() {
        if (isIOS) {
            console.warn("ForegroundService may be used only Android platfrom.")
            return;
        }
        return await ForegroundServiceModule.stopService();
    }

    /**
     * Adds a handler to be invoked when button on notification will be pressed.
     * The data arguments emitted will be passed to the handler function.
     *
     * @param event - Name of the event to listen to
     * @param handler - Function to invoke when the specified event is emitted
     */
    on(event, handler) {
        if (isIOS) {
            console.warn("ForegroundService may be used only Android platfrom.")
            return;
        }
        if (!handler || !(handler instanceof Function)) {
            console.warn(`ForegroundService: on: handler is not a Function`);
            return;
        }
        if (!this._listeners.has(event)) {
          this._listeners.set(event, new Set());
        }
        this._listeners.get(event)?.add(handler);
    }

    /**
     * Removes the registered `handler` for the specified event.
     *
     * If `handler` is not provided, this function will remove all registered handlers.
     *
     * @param event - Name of the event to stop to listen to.
     * @param handler - Handler function.
     */
    off(event, handler) {
        if (isIOS) {
            console.warn("ForegroundService may be used only Android platfrom.")
            return;
        }
        if (!this._listeners.has(event)) {
          return;
        }
        if (handler && handler instanceof Function) {
          this._listeners.get(event)?.delete(handler);
        } else {
          this._listeners.set(event, new Set());
        }
    }

    /**
     * @private
     */
     _emit(event, ...args) {
        const handlers = this._listeners.get(event);
        if (handlers) {
            handlers.forEach((handler) => handler(...args));
        } else {
            console.log(`[VIForegroundService]: _emit: no handlers for event: ${event}`);
        }
    }

    /**
     * @private
     */
    _VIForegroundServiceButtonPressed(event) {
        this._emit('VIForegroundServiceButtonPressed', event);
    }
}

export default VIForegroundService;
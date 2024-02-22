# Push Notifications

This article covers the how to integrating the Hubspot SDK for push notifications.

**Prerequisite**: 
* The app should have push notification permission available.

## Overview
The way push notification is configured, which allow the Hubspot SDK to handle the notifications itself, and trigger a callback whenever a chat needs to be displayed. This may be convenient for apps that have not yet configured push notifications.
In `Hubspotmanager`, we have a method which determines the notification is from the Hubspot or it is from the different apps. 

## Push Notifications Mechanism
If the push notification is from the Hubspot, the Hubspot uses the payload key which has `hs` as a prefix. 
If any of the payload has `hs` prefix, it shows the notification is from the Hubspot. The `HubspotManager` class verify the payload and returns true/false based on the criteria.


#### Check for Hubspot Notifications in your notification handler
Here are the steps, to check and handle the Hubspot notification in the internal notification builder. 

1. Create your own class which extends the `HubspotFirebaseMessagingService`.
```kotlin
class CustomPushService : HubspotFirebaseMessagingService() { 
    // Detail Implementations
}
```
2. In the class, user needs to make sure to call `super.onNewToken()` only if user wants to override the method from the `HubspotFirebaseMessagingService` class. If not, user can ignore the `super.onNewToken()` and implement your own logic in that.
```kotlin
class CustomPushService: HubspotFirebaseMessagingService() {
  override fun onNewToken() {
    super.onNewToken()
  } 
}


class CustomPushService: OtherService() {
  override fun onNewToken() {
    someCustomLogic() // Make sure to do not add super.onNewToken()
  } 
}
```
3. In your class, override the `onMessageReceived(message: RemoteMessage)` method. In the `onMessageReceived(message: RemoteMessage)` method, check the message payload is from the Hubspot or not by using the `HubspotManager.isHubspotNotification`
If user wants to override the method from `HubspotFirebaseMessagingService`, then use the `super.onMessageReceived(message)` else use custom logic to handle the notifications.
See Below Code:
```kotlin
 override fun onMessageReceived(message: RemoteMessage) {
        if (HubspotManager.isHubspotNotification(message.data)) {
            super.onMessageReceived(message) // It goes into HubspotFirebaseMessagingService class
        } else {
            // Create your own implementation for the Notifications
        }
    }
```
**Note**:
`HubspotManager.isHubspotNotification` : Check the push notification is from the hubspot or not and returns boolean value. If the value is true meaning, the notification is from the hubspot else from the different app. 

5. If it is not from the Hubspot app, you can create your own implementation in the above method.
6. For testing the push notification, user can use the firebase console or user can use the firebase fcm calls to send the push notifications to the mobile applications.

#### Hubspot SDK Handling Notifications internally

In the notification payload, if any of the key presents which has `hs` prefix, it goes to `HubspotFirebaseMessagingService`.
The `HubspotFirebaseMessagingService` extends the `FirebaseMessagingService` class and it has the override method for `onNewToken` and `onMessageReceived`. 

`onNewToken` -> Used for sending a token to the Hubspot Server
`onMessageReceived` -> Shows the message which is from the fcm server/backend server

#### Handling user opening a notification

- When a user taps on a notification, if the notification has any `hs` property available it opens the HubspotWebView or else it opens the screen, which you have integrated while getting the Push Notifications.  
- `HubspotFirebaseMessageService` class automatically handles the notification and open the chat view by passing the push payload parameters.  
- It passes the data from the `HubspotFirebaseMessageService` class to `HubspotWebActivity`, then the activity receives the data as a intent. `HubspotWebView` uses that data to show the chatURL into webviews. 
- The chat URL receives all the data from the `HubspotWebActivity`, append the data as a query parameters and loads the URL.
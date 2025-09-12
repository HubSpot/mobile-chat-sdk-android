![Maven Central Version](https://img.shields.io/maven-central/v/com.hubspot.mobilechatsdk/mobile-chat-sdk-android)

## Overview

HubSpot's Android Mobile Chat SDK is designed to seamlessly integrate HubSpot Chat into any Android mobile application (see [here](https://github.hubspot.com/mobile-chat-sdk-ios/) for the iOS documentation). With this SDK, developers can effortlessly provide their users with a fast, efficient, and empathetic in-app customer support experience.

### Key Features

* Integrate HubSpot Chat into your mobile app to deliver real-time, in-app customer support.
* Leverage HubSpot's powerful Bots and Knowledge Base to deflect customer inquiries 24/7.
* Alert users of new messages via push notifications.
* Customize the chat experience to align with your brand and user interface.

## Introduction

This is app is intended to demonstrate the HubSpot SDK and Push Capabilities to the demo application.

For push notifications, this app contains a standard implementation of the `FirebaseMessagingService`.

## Steps to use the app

1. Add your `google-services.json` file in the `app` folder.
Be sure that the `package_name` in the file is `com.example.demo`.
2. Create your hubspot-info.json file in the `app` folder. The folder structure should be `app -> src -> main -> assets -> hubspot-info.json`

![HubspotConfig](https://github.com/HubSpot/mobile-chat-sdk-android/blob/main/hubspot/HubspotConfig.png)

3. Sync the app
4. Build and Run the app

For more information, please refer to the following links:
* https://central.sonatype.com/artifact/com.hubspot.mobilechatsdk/mobile-chat-sdk-android
* https://developers.hubspot.com/docs/api-reference/mobile-chat-sdk/android
* https://knowledge.hubspot.com/chatflows/create-and-customize-a-mobile-chatflow


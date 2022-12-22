## Introduction
This app is a clone of the popular app ClipDrop, which allows you to remove the background from an image in real-time. This app was developed in Kotlin and utilizes the ClipDrop API to perform the background removal.

## Features
- Take a picture using your camera
- Remove the background from the image in real-time
- Save the resulting image to your device
- Send the resulting image to a Python GUI app built with tkinter

## Prerequisites
- Android Studio
- An API key for the ClipDrop API (sign up for one [here](https://clipdrop.co/apis/docs/remove-background))
- A Python environment with Flask installed

## Setup
- Clone the repository and open the project in Android Studio:
`https://github.com/koushikjoshi/ClipDropClone`
- Create a file called keys.xml in the root directory of the project
- In keys.xml, add the following code:

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="api_key">YOUR_API_KEY_HERE</string>
</resources>
```

- Replace YOUR_API_KEY_HERE with your actual ClipDrop API key
- Run the app on an emulator or device

## Usage
- Tap the "Take Picture" button to open your camera
- Take a picture of an object with a clear background
- The app will automatically remove the background in real-time
- Tap the "Save" button to save the resulting image to your device
- Click on the "send" button to send it to your PC.

## Notes
- Make sure the Python app `app.py` is running before attempting to send the image
- The Python app must be on the same network as the device running the Android app

## Built With
- Android Studio - The mobile development platform used
- Kotlin - The programming language used
- ClipDrop API - The API used to remove the background from the images

### Libraries:

#### Python:
- Python Flask - for to communicate between the app and the PC
- PIL

#### Android:
- Retrofit - for API calls
- OkHttp3: Used as the HTTP client for Retrofit.
- Gson: Used for serialization and deserialization of the API response.
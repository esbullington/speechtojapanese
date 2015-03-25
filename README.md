# 言 Speech to Japanese text Android app

言, or *lu* is an Android app that leverages IBM Watson Service's speech-to-text API in combination with the Google Translate API to provide a quick way to render a spoken phrase into easy-to-read Japanese text.  Once the user's spoken phrase has been processed, a full-screen Japanese translation remains open and accessible until dismissed by the user.

## Table of Contents

* [How It Works](#how-it-works)
  * [IBM Watson Services](#ibm-watson-services))
  * [Google Translate API](#google-translate-api)
* [Installation](#installation)
* [Building](#building)
  * [Android Studio](#android-studio)
  * [Command Line](#command-line)
* [Ideas for Improvement](#ideas-for-improvement)

### Installation

The app can be side loaded by downloading and installing the APK file from the [following HTTPS link on Github]() or you may build and install the app yourself using the [instructions provided below](#building).

### Building

#### Android Studio

Import the app into Android Studio as a regular Gradle project and run `Shift + F10`.

#### Command line

Assuming you have the `adb` command line tool on your path, and are running in an environment with GNU Make (Linux, OSX with xcode installed, Windows with Cygwin), you can build the project using the `make` command.  Then, to install and run the app on your connected Android device, run the command `make install`.  Other targets may be displayed by running `make list` or by viewing the Makefile.


### Ideas for Improvement

* The app should also display the text being translated, in case of an error in the voice recognization.
* The audio recorder currently runs on a thread, while the HTTP requests are made in an AsyncTask.  These should probably be combined.

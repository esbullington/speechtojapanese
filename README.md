## Iu: A Speech-to-Japanese-Text App

言う, or *iu*, is an Android app that leverages IBM Watson Service's [speech-to-text API]() in combination with the Google Translate API to provide a quick way to render a spoken phrase into easy-to-read Japanese text.  Once the user's spoken phrase has been processed, a full-screen Japanese translation remains open and accessible until dismissed by the user.

### Table of Contents

* [How It Works](#how-it-works)
  * [IBM Watson Services](#ibm-watson-services)
  * [Google Translate API](#google-translate-api)
* [Building](#building)
  * [Android Studio](#android-studio)
  * [Command Line](#command-line)
* [Ideas for Improvement](#ideas-for-improvement)
* [License](#license)

### How It Works

#### IBM Watson Services

This app uses the speech-to-text API from [IBM's Watson Services](http://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/services-catalog.html), which was recently released as a beta service.

#### Google Translate API

Once speech has been turned to text by the Watson speech-to-text service, it's sent to the well-known Google Translate API, which dynamically translates the text from English to Japanese.  However, I plan to soon switch over to a "home-grown" machine translation solution using the open source Moses SMT, with corpora specifically targeted to the subject area (short phrases relating to tourism and travel).  This would have the added benefit of being free-of-charge (Google now charges for use of its translation API).


### Building

You'll need to provide your own Google API and IBM Watson API keys, insert them in the appropriate places in the "template.properties" file under the `src/main/assets` directory, and rename the file to `credentials.properties`.


#### Android Studio

Import the app into Android Studio as a regular Gradle project and run `Shift + F10`.

#### Command line

Assuming you have the `adb` command line tool on your path, and are running in an environment with GNU Make (Linux, OSX with xcode installed, Windows with Cygwin), you can build the project using the `make` command.  Then, to install and run the app on your connected Android device, run the command `make install`.  Other targets may be displayed by running `make list` or by viewing the Makefile.


### Ideas for Improvement

- Replace the Google Translate API with an instance of Moses or Joshua running on a Bluemix instance (for fast connection to the IBM Watson speech-to-text API). Assuming the SMT is trained with appropriate corpora with tourism-related data and other short phrases from everyday life, it may even prove to be more accurate than Google Translate in the context of situations commonly encountered by tourists and travelers.
- The app should also display the text being translated, in case of an error in the voice recognization.
- The audio recorder currently runs on a thread, while the HTTP requests are made in an AsyncTask.  These should probably be combined.

### License

Copyright (c) 2015 Eric. S Bullington

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

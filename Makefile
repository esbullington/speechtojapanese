APP_DIRECTORY=/home/eric/repos/SpeechToJapanese

build:
	$(APP_DIRECTORY)/gradlew assembleDebug

release:
	$(APP_DIRECTORY)/gradlew assembleRelease

run:
	adb shell am start -n com.ericbullington.speechtojapanese/com.ericbullington.speechtojapanese.MainActivity

install:
	adb install -r $(APP_DIRECTORY)/build/outputs/apk/SpeechToJapanese-debug.apk && make run

.PHONY: build release run install

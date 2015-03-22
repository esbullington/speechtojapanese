APP_DIRECTORY=/home/eric/repos/SpeechToJapanese

default:
	make build && make install

build:
	$(APP_DIRECTORY)/gradlew assembleDebug

release:
	$(APP_DIRECTORY)/gradlew assembleRelease

restart:
	adb shell am force-stop com.ericbullington.speechtojapanese
	adb shell am start -n com.ericbullington.speechtojapanese/com.ericbullington.speechtojapanese.MainActivity

run:
	adb shell am start -n com.ericbullington.speechtojapanese/com.ericbullington.speechtojapanese.MainActivity

install:
	adb install -r $(APP_DIRECTORY)/build/outputs/apk/SpeechToJapanese-debug.apk && make run


.PHONY: build release run install

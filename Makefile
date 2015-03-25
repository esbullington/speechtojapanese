APP_DIRECTORY=/home/eric/repos/SpeechToJapanese

default:
	make build

all:
	make build && make install

build:
	$(APP_DIRECTORY)/gradlew assembleDebug

release:
	$(APP_DIRECTORY)/gradlew assembleRelease

restart:
	adb shell am force-stop com.ericbullington.speechtojapanese
	adb shell am start -n com.ericbullington.speechtojapanese/com.ericbullington.speechtojapanese.ui.MainActivity

run:
	adb shell am start -n com.ericbullington.speechtojapanese/com.ericbullington.speechtojapanese.ui.MainActivity

install:
	adb install -r $(APP_DIRECTORY)/build/outputs/apk/SpeechToJapanese-debug.apk && make run


.PHONY: build release run install

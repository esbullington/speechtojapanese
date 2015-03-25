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

## From http://stackoverflow.com/a/26339924/1002047

list:
	@echo "AVAILABLE TARGETS:"
	@$(MAKE) -pRrq -f $(lastword $(MAKEFILE_LIST)) : 2>/dev/null | awk -v RS= -F: '/^# File/,/^# Finished Make data base/ {if ($$1 !~ "^[#.]") {print $$1}}' | sort | egrep -v -e '^[^[:alnum:]]' -e '^$@$$' | xargs | sed 's/ /\n/ig'


.PHONY: build release run install list restart all

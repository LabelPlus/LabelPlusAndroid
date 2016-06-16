LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_LIB_TYPE := STATIC
OPENCV_INSTALL_MODULES := on
include J:/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES := main.cpp Scanner.cpp
LOCAL_LDLIBS += -llog -ldl -Wl,-x,--as-needed
LOCAL_MODULE := bdrscan

include $(BUILD_SHARED_LIBRARY)

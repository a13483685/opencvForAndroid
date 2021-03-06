//
// Created by Administrator on 2018/6/21.
//
#include<jni.h>
#include<opencv2/opencv.hpp>
#include <iostream>
#include<vector>
#include <android/log.h>

#define  LOG_TAG    "MYHAARDETECTION"

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

using namespace cv;
using namespace std;

extern "C" {

CascadeClassifier face_detector;
JNIEXPORT void JNICALL
Java_com_example_administrator_opencvforandroid_CamreaViewActivity_initLoad(JNIEnv *env, jobject,
                                                                            jstring haarfilePath) {
    const char *nativeString = env->GetStringUTFChars(haarfilePath, 0);
    bool isOpen = false;
    isOpen = face_detector.load(nativeString);
    if (!isOpen) {
        LOGD("Method Description: %s", "load failed");
    }
    env->ReleaseStringUTFChars(haarfilePath, nativeString);
    LOGD("Method Description: %s", nativeString);
}
JNIEXPORT void JNICALL
Java_com_example_administrator_opencvforandroid_CamreaViewActivity_faceDetect(JNIEnv *, jobject,
                                                                              jlong addrRgba) {
    int flag = 1000;
    Mat &mRgb = *(Mat *) addrRgba;
    Mat gray;
    cvtColor(mRgb, gray, COLOR_BGR2GRAY);
    vector<Rect> faces;
    LOGD("This is a number from JNI: %d", flag * 2);
    face_detector.detectMultiScale(gray, faces, 1.1, 1, 0, Size(50, 50), Size(300, 300));
    LOGD("This is a number from JNI: %d", flag * 3);
    if (faces.empty()) return;
    for (int i = 0; i < faces.size(); i++) {
        rectangle(mRgb, faces[i], Scalar(255, 0, 0), 2, 8, 0);
        LOGD("Face Detection : %s", "Found Face");
    }
}
}

#include <jni.h>
#include <string>
#include <thread>
#include <mutex>
#include <atomic>
#include <chrono>

#include <android/sensor.h>
#include <android/log.h>
#include <android_native_app_glue.h>

JavaVM* globalVM{nullptr};
std::atomic<bool> stopped{false};
std::thread mainThread;

bool isStopped() {
    return stopped;
}

void setIsStopped(bool value) {
    stopped = value;
}

void run() {
    while(!isStopped()) {
        std::this_thread::sleep_for( std::chrono::seconds{1} );
    }
}

static void processAndroidCmd(struct android_app* app, int32_t cmd) {
    switch (cmd) {
        case APP_CMD_INIT_WINDOW:
            break;
        case APP_CMD_TERM_WINDOW:
            break;
        case APP_CMD_CONFIG_CHANGED:
            break;
        case APP_CMD_LOST_FOCUS:
            break;
    }
}

void android_main(struct android_app* app) {
    if (false == mainThread.joinable()) {
        mainThread = std::thread{run};
    }

    app->onAppCmd = processAndroidCmd;

    while (true) {
        int events;
        struct android_poll_source *source;

        while (ALooper_pollAll(0, NULL, &events, (void **) &source) >= 0) {

            // Process this event.
            if (source != NULL) {
                source->process(app, source);
            }

            if (app->destroyRequested != 0) {
                return;
            }
        }
    }
}

extern "C" JNIEXPORT void JNICALL
Java_domain_namespace_nativeapplication_CallbacksToNative_stopThread(JNIEnv* /*env*/, jclass /* this */) {
    if (mainThread.joinable()) {
        setIsStopped(true);
        mainThread.join();
    }
}

extern "C" JNIEXPORT jboolean JNICALL
Java_domain_namespace_nativeapplication_CallbacksToNative_isThreadStopped(JNIEnv* /*env*/, jclass /* this */) {
    return isStopped();
}

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    globalVM = vm;

    // Find your class. JNI_OnLoad is called from the correct class loader context for this to work.

    // Register your class' native methods.

    return JNI_VERSION_1_6;
}
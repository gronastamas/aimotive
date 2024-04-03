#include <jni.h>
#include <string>
#include <android/log.h>
#include <fstream>
#include <sstream>

extern "C" {

JNIEXPORT void

    JNICALL
    Java_com_example_aimotiveassignment_MainViewModel_storeCoordinates(
            JNIEnv *env,
            jobject,
            jstring filePath,
            jdouble latitude,
            jdouble longitude) {

        const char *filePathValue = env->GetStringUTFChars(filePath, NULL);

        std::ostringstream oss;
        oss << std::fixed << std::setprecision(6);
        oss << latitude << ", " << longitude;
        std::string coordinateString = oss.str();

        std::ofstream outFile(filePathValue, std::ios::out | std::ios::app);
        if (outFile.is_open()) {

            outFile << coordinateString << std::endl;
            outFile.close();
        }
        __android_log_print(ANDROID_LOG_VERBOSE, "AI_MOTIVE",
                            "storeCoordinates, latitude = %f,  longitude = %f", latitude, longitude);
    }
}




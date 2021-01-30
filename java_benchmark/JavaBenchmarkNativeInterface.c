#include <jni.h>
#include "JavaBenchmark.h"
#include "JavaBenchmarkNativeLogic.h"

JNIEXPORT jlong JNICALL Java_JavaBenchmark_threadMigrationTime (JNIEnv *env, jclass cls) {
	long val = threadMigration();
	return val;
}
#include <stdio.h>
#include <jni.h>
#include "Encryption.h"

 JNIEXPORT jintArray JNICALL Java_Decryption_decrypt
(JNIEnv *env, jobject object, jintArray value, jintArray key) {
	jint *k;
	jint *v;
	jlong final;
	unsigned int delta = 0x9e3779b9, n=32, sum=0;

	v = (jint *)(*env)->GetIntArrayElements(env, value, 0);
	k = (jint *)(*env)->GetIntArrayElements(env, key, 0);

	jint y = (jint) v[0];
	jint z = (jint) v[1];

	sum = delta<<5;
	while (n-- > 0){
		z -= (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
		y -= (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		sum -= delta;
	}
	v[0] = y;
	v[1] = z;

	jintArray result = (*env)->NewIntArray(env, 2);
	(*env)->SetIntArrayRegion(env, result, 0, 2, v);
	return result;
}
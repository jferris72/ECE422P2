#include <stdio.h>
#include <jni.h>
#include "Encryption.h"

 JNIEXPORT jintArray JNICALL Java_Encryption_encrypt
(JNIEnv *env, jobject object, jbyteArray value, jintArray key) {
	jint *k;
	jint *v;
	jlong final;
	unsigned int delta = 0x9e3779b9, n=32, sum=0;


	v = (jint *)(*env)->GetIntArrayElements(env, value, 0);
	k = (jint *)(*env)->GetIntArrayElements(env, key, 0);

	jint y = v[0];
	jint z = v[1];

	while (n-- > 0) {
		sum += delta;
		y += (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		z += (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
	}

	v[0] = y;
	v[1] = z;

	value = (jintArray) &v;

	return value;
}
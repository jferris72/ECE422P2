JAVA_HOME = /usr/lib/jvm/java-8-openjdk-amd64

make:
	javac *.java
	javah Encryption
	javah Decryption
	gcc -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -shared -fpic -o libencrypt.so lib_Encryption.c 
	gcc -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -shared -fpic -o libdecrypt.so lib_Decryption.c
# 	export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.
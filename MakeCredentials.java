import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.*;
import java.nio.file.*;
import javax.crypto.*;
import java.security.SecureRandom;
import java.util.Arrays;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;

/*
 * Password salting taken from http://stackoverflow.com/questions/33085493/hash-a-password-with-sha-512-in-java
 */


public class MakeCredentials {
	public static void main(String[] args) {
		String username = args[0];
		String password = args[1];
		String concat = username + password;

		newCredentials(concat);
	}

	public static String get_SHA_512_SecurePassword(String passwordToHash, String salt) {
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(salt.getBytes("UTF-8"));
			byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for(int i=0; i< bytes.length ;i++){
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		} 
	    catch (NoSuchAlgorithmException e){
			e.printStackTrace();
	    }
	    catch(Exception e) {
	    	System.out.println("failed to secure password");
	    }
	    return generatedPassword;
	}

	public static void newCredentials(String credentials) {
		try {
			String salt = "asdkfjhsleirtvolm";
			String hash = get_SHA_512_SecurePassword(credentials, salt);
			
			File fout = new File("credentials.txt");
			FileOutputStream fos = new FileOutputStream(fout);
		 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		 
			bw.write(hash);
			bw.newLine();	
		 
			bw.close();
		} catch (Exception e) {
			System.out.println("failed to add name");
		}
	}
}
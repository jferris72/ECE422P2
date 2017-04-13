import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.lang.String; 
import java.nio.*;
import java.nio.file.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.io.File;
import java.io.FileOutputStream;
import java.io.*;




public class Client {
	static {
    	System.loadLibrary("encrypt");
    	System.loadLibrary("decrypt");
    }

	private static DataInputStream input;
	private static DataOutputStream output;
	private static String fileName;

	public static void main(String args[]) {
		//Read in parameters
		String username = args[0];
		String password = args[1];
		String concat = username + " " + password;
		try {
			byte[] credentials = concat.getBytes("UTF-8");
			connect(credentials);
		} catch (Exception e) {
			System.out.println("Credentials failed");
		}

	}

	public static void connect(byte[] credentials) {
		try {
			String serverAddress = "127.0.0.1";
			Socket socket = new Socket(serverAddress, 16000);

			TinyEncrypt tinyEncrypt = new TinyEncrypt();
			byte[] credentialsEncrypt = tinyEncrypt.encryptBytes(credentials);

			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			//SEND CREDENTIALS
			output.writeInt(credentialsEncrypt.length);
			output.write(credentialsEncrypt);

			int response = input.readInt();
			if(response == 1) {
				System.exit(0);
			}

			BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

			while(true) {
				//SEND FILE NAMES
				System.out.println("Enter filename: ");
				fileName = consoleReader.readLine();
				byte[] fileBytes = fileName.getBytes();
				byte[] encryptedFile = tinyEncrypt.encryptBytes(fileBytes);
				output.writeInt(encryptedFile.length);
				output.write(encryptedFile);
				//RECEIVE FILE
				int exists = input.readInt();
				if (exists == 1) {
					int actualLen = input.readInt();
					int len = input.readInt();
					byte[] newFile = new byte[len];
					if (len > 0) {
						input.readFully(newFile);
					}
					//WRITE FILE
					byte[] decryptedFile = tinyEncrypt.decryptBytes(newFile);
					byte[] finalBytes = new byte[actualLen];
					for(int i = 0; i < actualLen; i++) {
						finalBytes[i] = decryptedFile[i];
					}
					Files.write(Paths.get(fileName), finalBytes);
				} else {
					System.out.println("file does not exist");
				}
			}
		} catch(IOException e) {
			System.out.println("failed to connect to server");
		}
	}

}
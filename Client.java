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

			output.writeInt(credentialsEncrypt.length);
			output.write(credentialsEncrypt);

			int len = input.readInt();
			byte[] encryptedBytes = new byte[len];

			if (len > 0) {
				input.readFully(encryptedBytes);
			}

			System.out.println(Arrays.toString(encryptedBytes));



			// for(int i = 0; i < 2; i++) {
			// 	System.out.println(input.readLine());
			// }

			// String fileString = input.readLine();
			// byte[] fileBytes = fileString.getBytes();
			// TinyEncrypt tinyEncrypt = new TinyEncrypt();
			// byte[] fileBytes = tinyEncrypt.decryptBytes(fileBytes);
			// Files.write(Paths.get("output.txt"), fileBytes);

			// BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			// byte[] encryptedBytes;
			// bis.read(encryptedBytes, 0, 999999);
			
			byte[] fileBytes = tinyEncrypt.decryptBytes(encryptedBytes);
			Files.write(Paths.get("output.txt"), fileBytes);
			System.out.println(Arrays.toString(fileBytes));

			File file = new File("./", "fuck.txt");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(fileBytes);
			String mystring = new String(fileBytes).trim();
			System.out.println(mystring);
			

			BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

			while(true) {
				// System.out.println("Enter filename: ");
				// fileName = consoleReader.readLine();
				// output.println(fileName);
				// System.out.println(input.readLine());
			}
		} catch(IOException e) {
			System.out.println("failed to connect to server");
		}
	}

}
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

/*
 * Password salting taken from http://stackoverflow.com/questions/33085493/hash-a-password-with-sha-512-in-java
 */


public class Server {

	static {
    	System.loadLibrary("encrypt");
    	System.loadLibrary("decrypt");
    }
	
	public static void main(String[] args) {
		try {
			ServerSocket listener = new ServerSocket(16000);
			System.out.println("Server listening on socket 16000");
			int clientNumber = 0;

			try {
				while(true) {
					new EncryptionServer(listener.accept(), clientNumber++).start();
				}
			} catch (IOException e) {
				System.out.println("failed to make server");
			
			} finally {
				try {
					listener.close();
				} catch(IOException e) {
					System.out.println("fuq");
				}
			}
		} catch(IOException e) {
			System.out.println("failed to make server listener");
		}
	}

	private static class EncryptionServer extends Thread {
		private Socket socket;
		private int clientNumber;
		TinyEncrypt tinyEncrypt;


		public EncryptionServer(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			this.tinyEncrypt = new TinyEncrypt();
			System.out.println("New connection on" + socket);
		}

		public void run() {
			try {
				// KeyGenerator keyGen = KeyGenerator.getInstance("AES");
				// keyGen.init(128, new SecureRandom());
				// SecretKey key = keyGen.generateKey();
				// System.out.println("KEY: ");

				DataInputStream input = new DataInputStream(socket.getInputStream());
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
				// READ USERNAME
				int len = input.readInt();
				byte[] clientCredentials = new byte[len];

				if (len > 0) {
					input.readFully(clientCredentials);
				}

				// System.out.println(Arrays.toString(clientCredentials));
				checkCredentials(tinyEncrypt.decryptBytes(clientCredentials));

				// output.println("Connected with server");
				// output.println("to quit type 'quit'");
				String fileName = "test.txt";
				byte[] fileRead = Files.readAllBytes(Paths.get(fileName));
				byte[] encrypted = tinyEncrypt.encryptBytes(fileRead);
				// System.out.println(Arrays.toString(encrypted));

				output.writeInt(encrypted.length);
				output.write(encrypted);

				byte[] decrypted = tinyEncrypt.decryptBytes(encrypted);
				Files.write(Paths.get("decrypted.txt"), decrypted);
				System.out.println(Arrays.toString(decrypted));
				String mystring = new String(decrypted).trim();
				System.out.println(mystring);


				// output.println(encrypted);

				while(true) {
					// String inputString = input.readLine();
					// if(inputString == null || inputString.equals("quit")) {
					// 	System.out.println("client quit");
					// 	break;
					// }
					// output.println(inputString.toUpperCase());
				}
			} catch (Exception e) {
				System.out.println("error handling client");
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					System.out.println("failed to close socket");
				}
			}
		}

		public String get_SHA_512_SecurePassword(String passwordToHash, String salt) {
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
		    	System.out.println("failed to secure pasdword");
		    }
		    return generatedPassword;
		}

		public Boolean checkCredentials(byte[] credentials) {
			String str = new String(credentials);
			String[] split = str.split("\\s+");
			String username = split[0];
			String password = split[1];
			System.out.println(username);
			System.out.println(password);
			return true;
		}
	}
}
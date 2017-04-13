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
import java.io.File;
import java.io.FileOutputStream;
import java.io.*;


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
				//CHECK CREDENTIALS
				if(checkCredentials(tinyEncrypt.decryptBytes(clientCredentials))) {
					System.out.println("good login");
					int resp = 0;
					output.writeInt(resp);
				} else {
					System.out.println("failed login");
					int resp = 1;
					output.writeInt(resp);
					return;
				}

				//RETURN FILES
				while(true) {
					len = input.readInt();
					byte[] fileName = new byte[len];
					if (len > 0) {
						input.readFully(fileName);
					}

					byte[] decrFileName = tinyEncrypt.decryptBytes(fileName);
					String file = new String(decrFileName).trim();
					Boolean exists = new File(file).exists();
					if (exists == false) {
						output.writeInt(0);
					} else {
						output.writeInt(1);
						try {
							byte[] fileRead = Files.readAllBytes(Paths.get(file));
							byte[] encrypted = tinyEncrypt.encryptBytes(fileRead);
							output.writeInt(fileRead.length);
							output.writeInt(encrypted.length);
							output.write(encrypted);
						} catch (Exception e) {
							output.writeInt(0);
							output.writeInt(0);
							byte[] error = {0};
							output.write(error);
						}
					}
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

		//RETURN CREDENTIALS SALTED
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
		    	System.out.println("failed to secure password");
		    }
		    return generatedPassword;
		}

		//CHECK CREDENTIALS
		public Boolean checkCredentials(byte[] credentials) {
			try {
				String str = new String(credentials).trim();
				String salt = "asdkfjhsleirtvolm";
				String hash = get_SHA_512_SecurePassword(str, salt);
				Boolean found = false;

				try (BufferedReader br = new BufferedReader(new FileReader("credentials.txt"))) {
					// System.out.println(hash);
					String line;
					while ((line = br.readLine()) != null) {
						if(line.equals(hash)) {
							found = true;
							break;
						}
					}
				}

				return found;
			} catch(Exception e) {
				return false;
			}
		}
	}
}
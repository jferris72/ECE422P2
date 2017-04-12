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
			TinyEncrypt tiny = new TinyEncrypt();
			byte[] bytes = {10, 20, 30, 40, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21};
			bytes = tiny.encryptBytes(bytes);
			System.out.println(Arrays.toString(bytes));
			System.out.println(Arrays.toString(tiny.decryptBytes(bytes)));

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

		public EncryptionServer(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.println("New connection on" + socket);
		}

		public void run() {
			try {
				// KeyGenerator keyGen = KeyGenerator.getInstance("AES");
				// keyGen.init(128, new SecureRandom());
				// SecretKey key = keyGen.generateKey();
				// System.out.println("KEY: ");

				BufferedReader input = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
				PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

				output.println("Connected with server");
				output.println("to quit type 'quit'");
				String fileName = "test.txt";
				byte[] fileRead = Files.readAllBytes(Paths.get(fileName));
				TinyEncrypt tinyEncrypt = new TinyEncrypt();
				byte[] encrypted = tinyEncrypt.encryptBytes(fileRead);

				output.println(encrypted);

				while(true) {
					String inputString = input.readLine();
					if(inputString == null || inputString.equals("quit")) {
						System.out.println("client quit");
						break;
					}
					output.println(inputString.toUpperCase());
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
	}
}
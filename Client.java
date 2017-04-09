import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.lang.String; 


public class Client {
	static {
    	System.loadLibrary("encrypt");
    }

	private static BufferedReader input;
	private static PrintWriter output;
	private static String fileName;

	public static void main(String args[]) {
		//Read in parameters
		String username = args[0];
		String password = args[1];

		connect();
	}

	public static void connect() {
		try {
			String serverAddress = "127.0.0.1";
			Socket socket = new Socket(serverAddress, 16000);

			input = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);

			for(int i = 0; i < 3; i++) {
				System.out.println(input.readLine());
			}
			
			// byte[] outBytes;
			// Encryption encrypt = new Encryption();
			// outBytes = encrypt.encrypt(value, key);

			// String s = new String(input.readLine(), "UTF-8");
			// System.out.println(s);

			BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

			while(true) {
				System.out.println("Enter filename: ");
				fileName = consoleReader.readLine();
				output.println(fileName);
				System.out.println(input.readLine());
			}
		} catch(IOException e) {
			System.out.println("failed to connect to server");
		}
	}
}
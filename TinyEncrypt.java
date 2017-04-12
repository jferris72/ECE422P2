import java.util.Arrays;

public class TinyEncrypt {
	static {
    	System.loadLibrary("encrypt");
    	System.loadLibrary("decrypt");
    }

	public TinyEncrypt() {}

	public byte[] encryptBytes(byte[] data) {
		int length = data.length;
		int index = length/8;
		int remainder = length - (index * 8);
		index = (index * 8);
		byte[] encryptedData = new byte[index+8];
		byte[] firstBytes = new byte[4];
		byte[] secondBytes = new byte[4];
		int[] values = new int[2];
		Encryption encryption = new Encryption();
		Decryption decryption = new Decryption();
		int[] key = {10, 20, 30, 40};
		// System.out.println("before for loop" +  length);
		for(int i = 0; i < index; i+= 8) {
			for(int j = 0; j < 4; j++) {
				firstBytes[j] = data[i + j];
				secondBytes[j] = data[i + j + 4];
			}
			// System.out.println(Arrays.toString(firstBytes));
			// System.out.println(Arrays.toString(secondBytes));
			values[0] = toInt(firstBytes);
			values[1] = toInt(secondBytes);
			// System.out.println(values[0]);
			// System.out.println(values[1]);
			int[] temp = encryption.encrypt(values, key);
			System.out.println("encrypt:");
			System.out.println(temp[0]);
			System.out.println(temp[1]);
			// temp = decryption.decrypt(temp, key);
			firstBytes = toBytes(temp[0]);
			secondBytes = toBytes(temp[1]);

			System.out.println(Arrays.toString(firstBytes));
			System.out.println(Arrays.toString(secondBytes));
			for(int j = 0; j < 4; j++) {
				encryptedData[j + i] = firstBytes[j];
				encryptedData[j + i + 4] = secondBytes[j];
			}
			// System.out.println("encrypted Values: %d", temp[0]);
			// temp = decryption.decrypt(values, key);
			// System.out.println("decr values: %d", values[0]);

		}
		if (remainder < 4) {
			for(int i = 0; i < remainder; i++) {
				firstBytes[i] = data[index + i];
			} 
			for(int i = remainder; i < 4; i++) {
				firstBytes[i] = 0;
			}
			for(int i = 0; i < 4; i++) {
				secondBytes[i] = 0;
			}
		} else {
			for(int i = 0; i < 4; i++) {
				firstBytes[i] = data[index + i];
			}
			for(int i = 0; i < remainder-4; i++) {
				secondBytes[i] = data[index + i + 4];
			}
			for(int i = remainder-4; i < 4; i++) {
				secondBytes[i] = 0;
			}
		}
		values[0] = toInt(firstBytes);
		values[1] = toInt(secondBytes);
		int[] temp = encryption.encrypt(values, key);
		firstBytes = toBytes(temp[0]);
		secondBytes = toBytes(temp[1]);
		for(int i = 0; i < 4; i++) {
			encryptedData[index+i] = firstBytes[i];
			encryptedData[index+i+4] = secondBytes[i];
		}
		// System.out.println(Arrays.toString(firstBytes));
		System.out.println(Arrays.toString(encryptedData));
		return encryptedData;
	}

	public byte[] decryptBytes(byte[] data) {
		int length = data.length;
		length = length / 8;
		length = length * 8;
		System.out.println(Arrays.toString(data));

		byte[] decryptedData = new byte[length];
		byte[] firstBytes = new byte[4];
		byte[] secondBytes = new byte[4];
		int[] values = new int[2];
		Decryption decryption = new Decryption();
		int[] key = {10, 20, 30, 40};
		for(int i = 0; i < length; i+= 8) {
			for(int j = 0; j < 4; j++) {
				firstBytes[j] = data[i + j];
				secondBytes[j] = data[i + j + 4];
			}

			values[0] = toInt(firstBytes);
			values[1] = toInt(secondBytes);

			int[] temp = decryption.decrypt(values, key);
			firstBytes = toBytes(temp[0]);
			secondBytes = toBytes(temp[1]);

			for(int j = 0; j < 4; j++) {
				decryptedData[j + i] = firstBytes[j];
				decryptedData[j + i + 4] = secondBytes[j];
			}


		}

		return decryptedData;
	}


	public static byte[] toBytes(int data) {
		return new byte[] {
			(byte) ((data >> 24) & 0xFF),
			(byte) ((data >> 16) & 0xFF),
			(byte) ((data >> 8) & 0xFF),
			(byte) (data & 0xFF) };

	}

	public static int toInt(byte[] bytes) {
		int value = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
		return value;
	}
}
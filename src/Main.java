
import java.io.File;

public class Main {

    public static void main(String[] args) {
        try {
            String command = args[0]; // "encode" or "decode"
            String filePath = args[1]; // "dog.png" or "output.png"
            String password = args[args.length - 1]; // Last argument is password

            if (command.equals("encode")) {
                String secretMessage = args[2];


                System.out.println("Encrypting...");
                String encryptedText = CryptoUtils.encrypt(secretMessage, password);

                assert encryptedText != null;
                String binary = stringToBinary(encryptedText);


                System.out.println("Hiding data...");
                StegoUtils.encodeImage(new File(filePath), binary);
                System.out.println("Done! Saved as output.png");

            } else if (command.equals("decode")) {

                System.out.println("Reading image...");
                String binary = StegoUtils.decodeImage(new File(filePath));


                String encryptedText = binaryToString(binary);


                String originalMessage = CryptoUtils.decrypt(encryptedText, password);
                System.out.println("Secret Message: " + originalMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static String stringToBinary(String text) {
        StringBuilder b = new StringBuilder();
        for (char c : text.toCharArray()) {
            String bin = Integer.toBinaryString(c);
            while (bin.length() < 8) bin = "0" + bin; // Pad with zeros
            b.append(bin);
        }
        return b.toString();
    }

    public static String binaryToString(String binary) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 8) {
            String byteStr = binary.substring(i, i + 8);
            text.append((char) Integer.parseInt(byteStr, 2));
        }
        return text.toString();
    }
}
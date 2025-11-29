
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.MessageDigest;
import java.util.Arrays;

public class CryptoUtils {

    //Ensure the password is exactly 16 bytes (AES requirement)
    public static SecretKeySpec getKey(String myKey) {
        try {
            byte[] key = myKey.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // Force it to 16 bytes
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String strToEncrypt, String secret) {
        try {
            SecretKeySpec secretKey = getKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // Return as Base64 string so it's safe to turn into bits later
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String secret) {
        try {
            SecretKeySpec secretKey = getKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
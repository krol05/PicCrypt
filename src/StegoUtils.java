
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;


public class StegoUtils {

    public static void encodeImage(File inputFile, String binaryMessage) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputFile);
        //Force Convert to RGB
        BufferedImage image = userSpace(originalImage);
        int width = image.getWidth();
        int height = image.getHeight();

        int messageIndex = 0;
        // Add a "Stopper" sequence (null char) so we know when to stop reading
        String msgWithStopper = binaryMessage + "00000000";

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);

                int alpha = (pixel >> 24) & 0xFF;
                int red   = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8)  & 0xFF;
                int blue  = pixel & 0xFF;

                if (messageIndex < msgWithStopper.length()) {
                    int bit = Character.getNumericValue(msgWithStopper.charAt(messageIndex));
                    blue = embedBit(blue, bit); // Helper function below
                    messageIndex++;
                }

                int newPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                image.setRGB(x, y, newPixel);

                if (messageIndex >= msgWithStopper.length()) {
                    ImageIO.write(image, "png", new File("output.png"));
                    return;
                }
            }
        }
    }

    public static String decodeImage(File inputFile) throws IOException {
        BufferedImage image = ImageIO.read(inputFile);
        //Ensure RGB mode
        image = userSpace(image);
        int width = image.getWidth();
        int height = image.getHeight();
        StringBuilder bits = new StringBuilder();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);

                // We only need use blue, since least significant to human eye
                // Blue is also the least significant byte (RG(B))
                int blue = pixel & 0xFF;

                // Extract last bit
                int lastBit = blue & 1;
                bits.append(lastBit);


                if (bits.length() >= 8 && bits.length() % 8 == 0) {
                    String lastByte = bits.substring(bits.length() - 8);
                    if (lastByte.equals("00000000")) {
                        return bits.substring(0, bits.length() - 8);
                    }
                }
            }
        }
        return "";
    }

    // Helper: Modifies the last bit
    public static int embedBit(int color, int bit) {
        return (color & 0xFE) | bit;
    }

    // Creates a fresh copy of the image in standard RGB format
    public static BufferedImage userSpace(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = newImage.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return newImage;
    }

}


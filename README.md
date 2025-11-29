# PicCrypt

I built PicCrypt to mess around with low-level data manipulation and encryption in Java. It’s a CLI tool that encrypts text and hides it inside the pixels of an image using Steganography.

With this tool, you can, for whatever reason you wish, send encrypted Messages to your friends using completely unrelated Pictures.

To anyone browsing your files, the output looks like a normal PNG. But if you run it through the decoder with the right password, you get the hidden message back.

## How it works under the hood

The cool part of this project isn't the encryption (that's standard AES), but how the data is actually hidden inside the image bits.

In Java, a pixel is usually stored as a single, massive 32-bit integer. You can think of it like a packed train of data:
`[Alpha 8-bits] [Red 8-bits] [Green 8-bits] [Blue 8-bits]`

To hide a message, I have to unpack that container, tweak one tiny bit, and repack it.

### 1. Unpacking the bits
Since the colors are mashed together into one number, I use **Bitwise Shifts** to separate them.
* `>> 16`: This slides the bits to the right. It moves the Red value down to the "ones" column so the computer can read it as a number from 0-255.
* `& 0xFF`: This acts like a filter. It ignores the other colors (Alpha/Green) and grabs just the 8 bits we want.

### 2. The LSB Hack
I hide the data specifically in the **Blue** channel. Why? Because the human eye is really bad at distinguishing shades of blue.

I use the **Least Significant Bit (LSB)** method. I take the binary value of the blue pixel (e.g., `11001000`) and flip the very last number to match the bit of my secret message. The color value changes from something like 200 to 201. It's mathematically different, but visually (to the human eye) identical.

### 3. Repacking
After modifying that one bit, I have to glue the pixel back together. I use Left Shifts (`<<`) to push the Alpha, Red, and Green values back to their original positions and then use the OR operator (`|`) to combine them with the new Blue value.

## Usage

### 1. Build
Compile the Java files from the project root.
```bash
javac -d bin src/*.java
```

### 2. Encode (Hide)
Provide the input image, your secret message, and a password.
```bash
java -cp bin Main encode dog.png "This is secret" myPassword
```
*This saves the result as `output.png`.*

### 3. Decode (Reveal)
Point the tool at the encoded image to retrieve the text.
```bash
java -cp bin Main decode output.png myPassword
```

## Limitations
* **PNG Only:** This only works with `.png` files. JPEGs use compression that messes up the pixel data, which destroys the hidden message.
* **No Transparency:** To make the math easier and safer, the tool converts images to a solid RGB format. 
* If you have suggestions how to increase the functionality and remove the limitations, feel free to share!
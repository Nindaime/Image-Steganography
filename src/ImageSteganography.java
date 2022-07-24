/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.Gson;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 * @author Peter
 */
public class ImageSteganography {

    private String ext;
    private String messageExtension;
    private File inputFile;
    private File outputFile;
    private String savedFile;
    private File messageFile;
    private String path;
    private String hiddenMessageLocation;

    public String getHiddenMessageFileLocation() {
        return hiddenMessageLocation;
    }

    public void setHiddenMessageFileLocation(String messageLocation) {
        this.hiddenMessageLocation = messageLocation;
    }

    public ImageSteganography() {
        if (!new File(Steg.stegoImagePath).exists()) {
            new File(Steg.stegoImagePath).mkdirs();
            new File(Steg.coverImagePath).mkdirs();
            new File(Steg.hiddenDataPath).mkdirs();
        }

        setPath(Steg.stegoImagePath);

        this.setSavedFile(this.path + "save.json");

    }

    private String getInputFileExtension() {
        return ext;
    }

    private void setInputFileExtension(File file) {
        this.ext = returnExtension(file);
    }

    /*
        This is the extension of the hidden message . Used during process encode

    */
    private String getHiddenMessageExtension() {
        return this.messageExtension;
    }

    private void setHiddenMessageExtension(String ext) {

        this.messageExtension = ext;
    }


    public String returnExtension(File file) {

        String extension[] = file.toString().split("\\.");

        String extensionString = extension[extension.length - 1];

        return extensionString;


    }

    private File getInputFile() {
        return inputFile;
    }

    public String getSavedFile() {
        return savedFile;
    }

    private void setSavedFile(String res) {
        this.savedFile = res;
    }

    private void setInputFile(File inputFile) {
        this.inputFile = inputFile;

    }

    public File getOutputFile() {
        return outputFile;
    }

    private void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * To get and set the Message location of the Output hidden message after decode method
     */
    public File getMessageFile() {
        return messageFile;
    }

    private void setMessageFile() {
        this.messageFile = new File(this.path + "res." + this.getHiddenMessageExtension());
    }


    private String getPath() {
        return path;
    }

    private void setPath(String path) {
        this.path = path;
    }


    public ArrayList<SaveObject> getSavedData(File file) {

        ArrayList<SaveObject> obj = new ArrayList();

        if (!file.exists()) {

        } else {
            try {

                BufferedReader reader = new BufferedReader(new FileReader(file));
                Gson gson = new Gson();

                String a;

                while ((a = reader.readLine()) != null) {

                    obj.add(gson.fromJson(a, SaveObject.class));
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return obj;
    }

    private void saveJsonFile() {

        try {
            File file = new File(savedFile);

            ArrayList<SaveObject> savedData = new ArrayList();
            savedData = getSavedData(file);

            int size_of_array = savedData.size();

            Gson gson = new Gson();

            SaveObject newObj = new SaveObject();

            newObj.setId(size_of_array + 1);
            newObj.setInput(this.getInputFile().toString());
            newObj.setOutput(this.getOutputFile().toString());

            String json = gson.toJson(newObj);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                if (size_of_array == 0) {

                    writer.write(json);
                } else {

                    for (SaveObject obj : savedData) {

                        writer.write(gson.toJson(obj));
                        writer.newLine();
                    }
                    writer.write(json);
                }
            }

        } catch (IOException ex) {

            ex.printStackTrace();
        }

    }

    public boolean encode(String file_name, String password, String message_location) throws Exception {

        this.setInputFile(new File(file_name));
        this.setInputFileExtension(this.getInputFile());
        this.setHiddenMessageFileLocation(message_location);

        this.setOutputFile(new File(this.path + "\\" + this.getInputFile().getName()));
        //String file_name = image_path(path, original, ext);
        BufferedImage image_orig = getImage(file_name);

        //user space is not necessary for Encrypting
        BufferedImage image = user_space(image_orig);

        BufferedImage msg_location = user_space(getImage(message_location));
        ////////////////////////////////////////////nremove these two lines
        byte img_msg_location[] = get_byte_data(msg_location);
        byte img_msg_[] = get_byte_data(image);


        image = add_text(image, password, msg_location);
        this.saveJsonFile();
        return (setImage(image, this.getOutputFile()));

    }

    public void decode(String outputFile, String password) {

        this.setInputFile(new File(outputFile));
        this.setInputFileExtension(this.getInputFile());
        try {
//user space is necessary for decrypting
            BufferedImage image = user_space(getImage(outputFile));


            String verifyPassword = new String(decode_text(get_byte_data(image), 0, 32));

            if (!password.equals(verifyPassword)) {

                throw new Exception("Invalid Password");
            }

            int byte_length_of_hidden_message = Integer.parseInt(new String(decode_text(get_byte_data(image), 80, 112)));
            int width = Integer.parseInt(new String(decode_text(get_byte_data(image), 176, 208)));
            int height = Integer.parseInt(new String(decode_text(get_byte_data(image), 272, 304)));


            String messageExt = new String(decode_text(get_byte_data(image), 368, 400));
            byte[] img = returnByte(get_byte_data(image), 464, byte_length_of_hidden_message);

            this.setHiddenMessageExtension(messageExt);
            this.setMessageFile();

            //System.out.println(this.getMessageFile());

            setImage(
                    createImage(img, width, height),
                    this.messageFile
            );

            //decode = decode_text(get_byte_data(image), 380, 412);
            //return (new String(decode));
        } catch (Exception e) {
            e.printStackTrace();
            //return "";
        }
    }

    private BufferedImage getImage(String f) {
        BufferedImage image = null;
        File file = new File(f);

        try {
            image = ImageIO.read(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return image;
    }

    private boolean setImage(BufferedImage image, File file) {

        try {
            file.delete(); //delete resources used by the File



            if (this.getInputFileExtension().equals("jpg")) {

                this.setMessageFile();
                ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageWriteParam writeParam = writer.getDefaultWriteParam();
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(1f);
                System.out.println("This is the message file " + this.messageFile);
                // writer.setOutput( new ImageOutputStream(this.messageFile));

                ImageOutputStream outputStream = ImageIO.createImageOutputStream(outputFile);
                writer.setOutput(outputStream);

                writer.write(null, new IIOImage(image, null, null), writeParam);


            } else {

                ImageIO.write(image, ext, file);

            }


            return true;
        } catch (Exception ex) {

            ex.printStackTrace();
            return false;
        }
    }

    private byte[] getStringByte(String value) {

        return value.getBytes();
    }

    private byte[] getStringByteLength(String value) {

        return bit_conversion(value.getBytes().length);
    }

    private BufferedImage add_text(BufferedImage image, String password, BufferedImage message) throws Exception {

//convert all items to byte arrays: image, message, message length
        byte img[] = get_byte_data(image);
        System.out.println("image byte size ----------------- "+img.length);
        if(img.length < 464){
            throw new Exception("Could not encode image");
        }

        /* img_msg_location is the byte from the message,
         b is the number of bytes it is made of
         c is the string value
         */
        byte img_msg_location[] = get_byte_data(message);
        int b = img_msg_location.length;

        try {

            img = encode_text(img, getStringByteLength(password), 0); //0 first positiong
            img = encode_text(img, getStringByte(password), 32); //4 bytes of space for length: 4bytes*8bit = 32 bits

            img = encode_text(img, getStringByteLength(String.valueOf(img_msg_location.length)), 80); // bytes for the message
            img = encode_text(img, getStringByte(String.valueOf(img_msg_location.length)), 112);

            img = encode_text(img, getStringByteLength(String.valueOf(message.getWidth())), 176); // bytes for the message
            img = encode_text(img, getStringByte(String.valueOf(message.getWidth())), 208);

            img = encode_text(img, getStringByteLength(String.valueOf(message.getHeight())), 272); // bytes for the message
            img = encode_text(img, getStringByte(String.valueOf(message.getHeight())), 304);

            //System.out.println( this.getHiddenMessageFileLocation());
            img = encode_text(img, getStringByteLength(String.valueOf(this.returnExtension(new File(this.getHiddenMessageFileLocation())))), 368); // bytes for the message
            img = encode_text(img, getStringByte(String.valueOf(this.returnExtension(new File(this.getHiddenMessageFileLocation())))), 400);


            img = encode_text(img, img_msg_location, 464); // bytes for the message

        } catch (Exception e) {
            e.printStackTrace();
        }


        return createImage(img, image.getWidth(), image.getHeight());



    }

    private static BufferedImage createImage(byte[] new_image, int width, int height) {

        BufferedImage result = null;

        DataBuffer buffer = new DataBufferByte(new_image, new_image.length);

        WritableRaster raster = Raster.createInterleavedRaster(buffer, width,
                height, width * 3, 3, new int[]{2, 1, 0}, (Point) null);

        ColorModel cm = new ComponentColorModel(ColorModel
                .getRGBdefault()
                .getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE
        );

        result = new BufferedImage(cm, raster, true, null);

        return result;
    }

    private BufferedImage user_space(BufferedImage image) {

//create new_img with the attributes of image
        BufferedImage new_img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = new_img.createGraphics();
        graphics.drawRenderedImage(image, null);
        graphics.dispose(); //release all allocated memory for this image
        return new_img;
    }

    private byte[] get_byte_data(BufferedImage image) {
        WritableRaster raster = image.getRaster();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        return buffer.getData();
    }

    private byte[] bit_conversion(int i) {
//originally integers (ints) cast into bytes
        byte byte7 = (byte) ((i & 0xFF00000000000000L) >>> 56);
        byte byte6 = (byte) ((i & 0x00FF000000000000L) >>> 48);
        byte byte5 = (byte) ((i & 0x0000FF0000000000L) >>> 40);
        byte byte4 = (byte) ((i & 0x000000FF00000000L) >>> 32);

//only using 4 bytes
        byte byte3 = (byte) ((i & 0xFF000000) >>> 24); //0
        byte byte2 = (byte) ((i & 0x00FF0000) >>> 16); //0
        byte byte1 = (byte) ((i & 0x0000FF00) >>> 8); //0
        byte byte0 = (byte) ((i & 0x000000FF));
//{0,0,0,byte0} is equivalent, since all shifts >=8 will be 0
        return (new byte[]{byte3, byte2, byte1, byte0});
    }

    private byte[] encode_text(byte[] image, byte[] addition, int offset) {
//check that the data + offset will fit in the image

        if (8 * addition.length + offset >= image.length) {

            throw new IllegalArgumentException("Cover file not large enough to cover image!");
        }
//loop through each addition byte

        for (int i = 0; i < addition.length; ++i) { //loop through the 8 bits of each byte 
            int add = addition[i];

            for (int bit = 7; bit >= 0; --bit, ++offset) {
//assign an integer to b, shifted by bit spaces AND 1
//a single bit of the current byte
                int b = (add >>> bit) & 1;

//assign the bit by taking: [(previous byte value) AND 0xfe] OR bit to add
//changes the last bit of the byte in the image to be the bit of addition

                image[offset] = (byte) ((image[offset] & 0xFE) | b);

            }
        }

        return image;
    }

    private byte[] returnByte(byte[] image, int offset, int length) {

        //byte ans [] = new byte[length];

        byte[] result = new byte[length];
//loop through each byte of text
        for (int b = 0; b < result.length; ++b) {
//loop through each bit within a byte of text
            for (int i = 0; i < 8; ++i, ++offset) {
//assign bit: [(new byte value) << 1] OR [(text byte) AND 1]

                result[b] = (byte) ((result[b] << 1) | (image[offset] & 1));
            }
        }
        return result;

    }

    private byte[] decode_text(byte[] image, int offset_start, int offset_end) {

        int length = 0;

//loop through 32 bytes of data to determine text length
        for (int i = offset_start; i < offset_start + 32; ++i) //i=24 will also work, as only the 4th byte contains real data
        {

            length = (length << 1) | (image[i] & 1);

        }

        return returnByte(image, offset_end, length);
    }

    /*
    private static String toBinary(byte b) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((b >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return (sb.toString());
    }
    */

}

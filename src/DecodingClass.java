
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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ken8
 */
public class DecodingClass {
    
    private File inputFile;
    private String path;
    
    //public DecodingClass() {

        //setPath("C:\\Users\\Ken8\\Desktop\\steganography\\");

       // this.setSavedFile(this.path + "save.json");}
    

    public File getInputFile() {
        return inputFile;
    }

    
    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    

    
    public StudentBiodata decodeR(String inputFile, String password) {
        
        
        try {
//user space is necessary for decrypting
      
            this.setInputFile( new File(inputFile));
            BufferedImage image = UtilityClass.user_space(UtilityClass.getImage(inputFile));
            
            //Retrieve the values stored in the EncodingClass

            String verifyPassword = new String(decode_text(UtilityClass.get_byte_data(image), 0, 32));

            if (!password.equals(verifyPassword)) {

                throw new Exception("Invalid Password");
            }

            int byte_length_of_hidden_message = Integer.parseInt(new String(decode_text(UtilityClass.get_byte_data(image), 80, 112)));
            
            String msg = new String(UtilityClass.returnByte(UtilityClass.get_byte_data(image), 368, byte_length_of_hidden_message));

            StudentBiodata obj ;
            Gson gson = new Gson();
            obj = gson.fromJson(msg, StudentBiodata.class);
                    
        
            return obj;
            
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }
    

    
    
    private byte[] decode_text(byte[] image, int offset_start, int offset_end) {

        int length = 0;

//loop through 32 bytes of data to determine text length
        for (int i = offset_start; i < offset_start + 32; ++i) //i=24 will also work, as only the 4th byte contains real data
        {

            length = (length << 1) | (image[i] & 1);

        }

        return UtilityClass.returnByte(image, offset_end, length);
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

    /*
    
    
    public void decode(String outputFile, String password) {
        
        
        try {
//user space is necessary for decrypting
            BufferedImage image = user_space(getImage(outputFile));
            

            String verifyPassword = new String(decode_text(get_byte_data(image), 0, 32));

            if (!password.equals(verifyPassword)) {

                throw new Exception("Invalid Password");
            }

            int byte_length_of_hidden_message = Integer.parseInt(new String(decode_text(get_byte_data(image), 80, 112)));
            int width = Integer.parseInt(new String(decode_text(get_byte_data(image), 176, 208)));
            int height = Integer.parseInt(new String(decode_text(get_byte_data(image),272 , 304)));
            

            String messageExt = new String(decode_text(get_byte_data(image), 368 ,400 ));
            
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

    
    */
    
}

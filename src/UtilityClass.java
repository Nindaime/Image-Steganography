
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import javax.imageio.ImageIO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ken8
 */
public class UtilityClass {
    
    public static BufferedImage getImage(String f) {
        BufferedImage image = null;
        File file = new File(f);

        try {
            image = ImageIO.read(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return image;
    }

    
    public static BufferedImage user_space(BufferedImage image) {

//create new_img with the attributes of image
        BufferedImage new_img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = new_img.createGraphics();
        graphics.drawRenderedImage(image, null);
        graphics.dispose(); //release all allocated memory for this image
        return new_img;
    }

    public static byte[] get_byte_data(BufferedImage image) {
        WritableRaster raster = image.getRaster();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        return buffer.getData();
    }

    public static byte[] returnByte(byte[] image, int offset, int length) {

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
    
    public static boolean setImage(BufferedImage image, File file,String ext) {

        try {
            file.delete(); //delete resources used by the File
            
            ImageIO.write(image, ext, file);
            
            
            

            return true;
        } catch (Exception ex) {

            ex.printStackTrace();
            return false;
        }
    }

    
    
    
    private static String toBinary(byte b) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((b >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return (sb.toString());
    }
    
    
    

    
}


import com.google.gson.Gson;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
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
public class EncodingClass {
    
    private String ext;
    private File inputFile;
    private File outputFile;
    private String path;
    

    //public EncodingClass() { //this.setSavedFile(this.path + "save.json")}
       
    private File getInputFile() {
        return inputFile;
    }


    private void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    private File getOutputFile() {
        return outputFile;
    }

    private void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    
    private String returnExtension(File file){
    
        String extension[] = file.toString().split("\\.");

        String extensionString = extension[extension.length - 1];
        
        return extensionString;
    
    
    }
    
    private void setInputFileExtension(File file) {

        
        this.ext = returnExtension(file);
    }


    
    public boolean encode(String file_name, String password, StudentBiodata student) throws Exception {

        /*
        
            set the  cover image e.g rose.png
            set the file extension of the input style
            set the output file location to store the stego image 
        
        */
        
        this.setInputFile(new File(file_name));
        this.setInputFileExtension(this.getInputFile());
        this.setOutputFile(new File(this.path + "stegan_" + this.getInputFile().getName()));
        
        if( ("jpg").equals(this.ext) || ("gif").equals(this.ext) ){
        
            throw new Exception("File Format not supported");
        }
        
        //this.setHiddenMessageFileLocation(message_location);

        
        //String file_name = image_path(path, original, ext);
        BufferedImage image_orig = UtilityClass.getImage(file_name);

        //user space is not necessary for Encrypting
        BufferedImage image = UtilityClass.user_space(image_orig);

        //BufferedImage msg_location = user_space(getImage(message_location));
        
        
        Gson gson = new Gson();
        String name = gson.toJson(student);
        
        
        image = add_text(image, password, name);
        //this.saveJsonFile();
        return (UtilityClass.setImage(image, this.getOutputFile() , this.ext));

    }
    
    
    
    
    private byte[] getStringByte(String value) {

        return value.getBytes();
    }

    private byte[] getStringByteLength(String value) {

        return bit_conversion(value.getBytes().length);
    }
    
    
    private BufferedImage add_text(BufferedImage image, String password, String message){
    
        //convert all items to byte arrays: image, message, message length
        
        byte img[] = UtilityClass.get_byte_data(image);

        
        
        byte img_msg[] = getStringByte(message);
        
        //Number of bytes the message is made up of
        int b = img_msg.length;
        
        try {

            /*
             the first 32 bytes stores the length of the password
             the next 48 bytes will store the actual password bytes
             the next 32 bytes will store the length of number of bytes of the message
             the next 256 bytes stores number of bytes of the message
             from byte 368 the actual message which is a json file is saved
            
            */
            
            img = encode_text(img, getStringByteLength(password), 0); 
            img = encode_text(img, getStringByte(password), 32); 

            img = encode_text(img, getStringByteLength(String.valueOf(b)), 80); 
            img = encode_text(img, getStringByte(String.valueOf(b)), 112);


            img = encode_text(img, img_msg, 368); 
            
            

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

        } catch (Exception ex) {
            ex.printStackTrace();
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

    
    private byte[] bit_conversion(int i) {
        //originally integers (ints) cast into bytes
        //byte byte7 = (byte) ((i & 0xFF00000000000000L) >>> 56);
        //byte byte6 = (byte) ((i & 0x00FF000000000000L) >>> 48);
        //byte byte5 = (byte) ((i & 0x0000FF0000000000L) >>> 40);
        //byte byte4 = (byte) ((i & 0x000000FF00000000L) >>> 32);

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

        if (addition.length + offset > image.length) {
            
            throw new IllegalArgumentException("File not long enough!");
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

    
    
    
    
    
    /**
     * Code for image steganography 
     * i.e hiding image inside image
     * Not used for this current 
     * project
    **/
    
    /*
    
    private BufferedImage add_text(BufferedImage image, String password, BufferedImage message) {

//convert all items to byte arrays: image, message, message length
        byte img[] = get_byte_data(image);

        // img_msg_location is the byte from the message,
        //b is the number of bytes it is made of
        // c is the string value
         
        byte img_msg_location[] = get_byte_data(message);
        int b = img_msg_location.length;

        try {

            img = encode_text(img, getStringByteLength(password), 0); //0 first positiong
            img = encode_text(img, getStringByte(password), 32); //4 bytes of space for length: 4bytes*8bit = 32 bits

            img = encode_text(img, getStringByteLength(String.valueOf(b)), 80); // bytes for the message
            img = encode_text(img, getStringByte(String.valueOf(b)), 112);

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

        try {

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return createImage(img, image.getWidth(), image.getHeight());
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
    
    
    private String hiddenMessageLocation;
    private String messageExtension;

    public String getHiddenMessageFileLocation() {
        return hiddenMessageLocation;
    }

    public void setHiddenMessageFileLocation(String messageLocation) {
        this.hiddenMessageLocation = messageLocation;
    }
    
    
    //This is the extension of the hidden message . Used during process encode
    
    
    private String getHiddenMessageExtension() {
        return this.messageExtension;
    }

    private void setHiddenMessageExtension(String ext) {

        

        this.messageExtension = ext;
    }
    
    // To get and set the Message location of the Output hidden message after decode method
     
    
    public File getMessageFile() {
        return messageFile;
    }

    private void setMessageFile() {
        this.messageFile = new File(this.path + "res." + this.getHiddenMessageExtension());
    }
    
    private File messageFile;
    
    private String savedFile;
    


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
    
    
    public String getSavedFile() {
        return savedFile;
    }

    private void setSavedFile(String res) {
        this.savedFile = res;
    }
    


    
    
    */

    
}

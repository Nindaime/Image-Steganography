
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ken8
 */
public class ImageSteganography {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        String file_name = "C:\\Users\\Ken8\\Desktop\\steganography\\rose.png";
        String password = "amaobi";
        String outputFile = "C:\\Users\\Ken8\\Desktop\\steganography\\stegan_rose.png";
        

        try {
            /*
            EncodingClass st = new EncodingClass();
        st.setPath("C:\\Users\\Ken8\\Desktop\\steganography\\");
        
            StudentBiodata obj = new StudentBiodata();
            obj.setFirstName("Amadi");
            obj.setLastName("Allwell");
            obj.setDepartment("Computer science");
            obj.setLevel(100);
            obj.setMealCredit(1090.00);
            obj.setMatricNumber("081629725");
            
            
        
            
            st.encode(file_name, password, obj);
            */
            EncodingClass st = new EncodingClass();
            DecodingClass dc = new DecodingClass();
            StudentBiodata rStudent = dc.decodeR(outputFile, "amaobi");
        
        System.out.println(rStudent.getMealCredit());
        
        rStudent.creditMealCredit(120);
        
        st.encode(file_name, password, rStudent);
        
        StudentBiodata rStudent1 = dc.decodeR(outputFile, "amaobi");
        
        System.out.println(rStudent1.getMatricNumber());
        
        //st.decode(outputFile, "amaobi");
        
        

        } catch (Exception ex) {
            Logger.getLogger(EncodingClass.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }
        
        
        
    }

}

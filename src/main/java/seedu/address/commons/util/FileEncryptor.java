package seedu.address.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;


// Sources: https://stackoverflow.com/questions/13673556/using-password-based-encryption-on-a-file-in-java


// TODO: Parse username as salt and pad it to make it 8bytes long at least
// TODO: Encrypt the actual data file and handle the error for sudden dissapearance

public class FileEncryptor {

    private static String extension = ".encrypted";
    private static String filename = "data/addressbook.xml";
    private static String message = "";

    private static final byte[] salt = {
            (byte) 0x43, (byte) 0x76, (byte) 0x95, (byte) 0xc7,
            (byte) 0x5b, (byte) 0xd7, (byte) 0x45, (byte) 0x17
    };

    public FileEncryptor (String password) {
        File f = new File(filename);
        File f_encrypted = new File(filename + extension);

        try {
            if (f.exists() && !f.isDirectory()) {
                encryptFile(filename, password );
                message = "File encrypted!";
                // TODO: Send a request to refresh the addressbook
            } else if (f_encrypted.exists() && !f_encrypted.isDirectory()) {
                decryptFile(filename, password);
                message = "File decrypted!";
                // TODO: Send a request to refresh the addressbook
            }

        } catch (IOException e) {
            message = e.getMessage();
        } catch (GeneralSecurityException e) {
            // message = e.getMessage();
            message = "Password mismatch!";
        }
    }



    private static Cipher makeCipher(String pass, Boolean decryptMode) throws GeneralSecurityException {

        //Use a KeyFactory to derive the corresponding key from the passphrase:
        PBEKeySpec keySpec = new PBEKeySpec(pass.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(keySpec);

        //Create parameters from the salt and an arbitrary number of iterations:
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 42);

        /*Dump the key to a file for testing: */
        // FileEncryptor.keyToFile(key);

        //Set up the cipher:
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");

        //Set the cipher mode to decryption or encryption:
        if (decryptMode) {
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeParamSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key, pbeParamSpec);
        }

        return cipher;
    }


    /**Encrypts one file to a second file using a key derived from a passphrase:**/
    public static void encryptFile(String fileName, String pass)
            throws IOException, GeneralSecurityException {
        byte[] decData;
        byte[] encData;
        File inFile = new File(fileName);
        //Generate the cipher using pass:
        Cipher cipher = FileEncryptor.makeCipher(pass, true);

        //Read in the file:
        FileInputStream inStream = new FileInputStream(inFile);

        int blockSize = 8;
        //Figure out how many bytes are padded
        int paddedCount = blockSize - ( (int) inFile.length() % blockSize );

        //Figure out full size including padding
        int padded = (int) inFile.length() + paddedCount;

        decData = new byte[padded];


        inStream.read(decData);

        inStream.close();

        //Write out padding bytes as per PKCS5 algorithm
        for ( int i = (int) inFile.length(); i < padded; ++i ) {
            decData[i] = (byte) paddedCount;
        }

        //Encrypt the file data:
        encData = cipher.doFinal(decData);


        //Write the encrypted data to a new file:
        FileOutputStream outStream = new FileOutputStream(new File(fileName + extension));
        outStream.write(encData);
        inFile.delete();
        outStream.close();
    }


    /**Decrypts one file to a second file using a key derived from a passphrase:**/
    public static void decryptFile (String fileName, String pass)
            throws GeneralSecurityException, IOException {
        byte[] encData;
        byte[] decData;
        File inFile = new File(fileName + extension);

        //Generate the cipher using pass:
        Cipher cipher = FileEncryptor.makeCipher(pass, false);

        //Read in the file:
        FileInputStream inStream = new FileInputStream(inFile);
        encData = new byte[(int) inFile.length()];
        inStream.read(encData);
        inStream.close();
        //Decrypt the file data:
        decData = cipher.doFinal(encData);

        //Figure out how much padding to remove

        int padCount = (int) decData[decData.length - 1];

        //Naive check, will fail if plaintext file actually contained
        //this at the end
        //For robust check, check that padCount bytes at the end have same value
        if( padCount >= 1 && padCount <= 8 ) {
            decData = Arrays.copyOfRange( decData , 0, decData.length - padCount);
        }

        //Write the decrypted data to a new file:

        FileOutputStream target = new FileOutputStream(new File(fileName));
        target.write(decData);
        inFile.delete();
        target.close();
    }

    public String getMessage () {
        return this.message;
    }
}

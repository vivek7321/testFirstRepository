package com.app.common;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypt {
	
	private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbxy667adfDEJ78";
    
    public String encrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(AESCrypt.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = Base64.getEncoder().encodeToString(encryptedByteValue);
        return encryptedValue64;
               
    }
    
    public String decrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(AESCrypt.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte [] decryptedValue64 = Base64.getDecoder().decode(value);
        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
        String decryptedValue = new String(decryptedByteValue,"utf-8");
        return decryptedValue;
                
    }
    
    private static Key generateKey() throws Exception 
    {
        Key key = new SecretKeySpec(AESCrypt.KEY.getBytes(),AESCrypt.ALGORITHM);
        return key;
    }

	public static void main(String[] args) {
		try {
			String password = "Erasmith@123";
            System.out.println("plain pass="+password);
            
            AESCrypt aesCrypt = new AESCrypt();
            
            String encryptedPassword = aesCrypt.encrypt(password);
            System.out.println("encrypted pass="+encryptedPassword);
            String decryptedPassword = aesCrypt.decrypt("WGvVozYmZITY/DsoO1BQrw==");    
            System.out.println("decrypted pass="+decryptedPassword);
        } catch(Exception e) { 
        	System.out.println("bug"+e.getMessage()); 
        }
	}

}

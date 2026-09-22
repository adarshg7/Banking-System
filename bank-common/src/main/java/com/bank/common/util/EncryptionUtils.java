package com.bank.common.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class EncryptionUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private EncryptionUtils(){
    }

    public static String encrypt(String plainText, String base64SecretKey){
        try{
            SecretKey key = decodeKey(base64SecretKey);
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS,iv);
            cipher.init(Cipher.ENCRYPT_MODE,key,spec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes());

            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv,0,combined,0,iv.length);
            System.arraycopy(cipherText,0,combined,iv.length,cipherText.length);

            return Base64.getEncoder().encodeToString(combined);
        }catch (Exception e){
            throw new IllegalStateException("Encryption failed",e);
        }
    }

    public static String decrypt(String encryptedText, String base64SecretKey){
        try {
            SecretKey key = decodeKey(base64SecretKey);
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            byte[] cipherText = new byte[combined.length - GCM_IV_LENGTH_BYTES];
            System.arraycopy(combined,0,iv,0,iv.length);
            System.arraycopy(combined,iv.length,cipherText,0,cipherText.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS,iv);
            cipher.init(Cipher.DECRYPT_MODE,key,spec);

            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText);
        } catch (Exception e){
            throw new IllegalStateException("Decryption failed - data may be tampered or key is wrong",e);
        }
    }

    public static String generateNewKey(){
        try{
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256,SECURE_RANDOM);
            SecretKey key = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (Exception e){
            throw new IllegalStateException("key generation failed",e);
        }
    }

    private static SecretKey decodeKey(String base64Key){
        byte[] decoded = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decoded,ALGORITHM);
    }
}

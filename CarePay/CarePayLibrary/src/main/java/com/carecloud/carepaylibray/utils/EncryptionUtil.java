package com.carecloud.carepaylibray.utils;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by lmenendez on 4/18/17.
 */

public class EncryptionUtil {
    private static final String AES_ALGORITHM = "AES";
    private static final int KEY_LENGTH = 256;
    private final static String HEX_VALUES = "0123456789ABCDEF";

    public static String encrypt(String plainText, String seed) {
        if(plainText == null){
            return null;
        }

        try {
            byte[] encryptionKey = getEncryptionKey(seed.getBytes());
            byte[] encryptionResult = encrypt(encryptionKey, plainText.getBytes());
            return getHexString(encryptionResult);
        }catch (GeneralSecurityException gse){
           gse.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encrypted, String seed){
        if(encrypted == null){
            return null;
        }

        try {
            byte[] encryptionKey = getEncryptionKey(seed.getBytes());
            byte[] encryptedBytes = getHexBytes(encrypted);
            return new String(decrypt(encryptionKey, encryptedBytes));
        }catch (GeneralSecurityException gse){
            gse.printStackTrace();
            return null;
        }
    }

    private static byte[] getEncryptionKey(byte[] seed) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        keyGen.init(KEY_LENGTH, secureRandom);
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }

    private static byte[] encrypt(byte[] encryptionKey, byte[] plainTextBytes) throws GeneralSecurityException{
        SecretKeySpec keySpec = new SecretKeySpec(encryptionKey, AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        return cipher.doFinal(plainTextBytes);
    }

    private static byte[] decrypt(byte[] encryptionKey, byte[] encrypted) throws GeneralSecurityException{
        SecretKeySpec keySpec = new SecretKeySpec(encryptionKey, AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        return cipher.doFinal(encrypted);
    }

    private static byte[] getHexBytes(String hexString){
        int length = hexString.length()/2;
        byte[] result = new byte[length];
        for(int i=0; i<length; i++){
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        }
        return result;
    }

    private static String getHexString(byte[] encrypted){
        if(encrypted == null){
            return null;
        }

        StringBuffer buffer = new StringBuffer(encrypted.length *2);
        for(int i = 0; i < buffer.length(); i++){
            appendHex(buffer, encrypted[i]);
        }
        return buffer.toString();
    }

    private static void appendHex(StringBuffer stringBuffer, byte b){
        stringBuffer.append(HEX_VALUES.charAt((b>>4)&0x0f)).append(HEX_VALUES.charAt(b&0x0f));
    }
}

package com.paysphere.utils;

import com.paysphere.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Slf4j
public class AesUtils {

    private static final String AES_KEY = "Paysphere.W.B.ML";

    public static String encrypt(String text) throws Exception {
        Key aesKey = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedText) throws Exception {
        Key aesKey = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    /**
     * cashier的token
     */
    public static String cashierToken(String text) {
        try {
            String encryptedText = encrypt(text);
            return encryptedText.replaceAll("[^a-zA-Z0-9]", "");
        } catch (Exception e) {
            log.error("Generate cashierToken error", e);
            throw new PaymentException("generate cashier token error");
        }
    }

    public static void main(String[] args) {
        try {
            String originalText = "1011718820992939245570";
            String encryptedText = encrypt(originalText);
            System.out.println("Original text: " + originalText);
            System.out.println("Encrypted text: " + encryptedText);
            String decryptedText = decrypt(encryptedText);
            System.out.println("Decrypted text: " + decryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
package com.paysphere.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


@Slf4j
public class GoogleAuthenticator {

    // taken from Google pam docs - we probably don't need to mess with these
    public static final int SECRET_SIZE = 10;

    public static final String SEED = RandomStringUtils.randomAlphanumeric(64);

    public static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";

    int window_size = 3;

    public static Boolean verifyCode(String codes, String savedSecret) {
        long code = Long.parseLong(codes);
        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        return ga.check_code(savedSecret, code, t);
    }

    public static String genURL() {
        String secret = GoogleAuthenticator.generateSecretKey();
        return GoogleAuthenticator.getQRBarcodeURL("WhooshPay", "Indonesia", secret);
    }

    public static String genURL(String secret) {
        return GoogleAuthenticator.getQRBarcodeURL("WhooshPay", "Indonesia", secret);
    }

    public static String generateSecretKey() {
        SecureRandom sr;
        try {
            sr = SecureRandom.getInstance(RANDOM_NUMBER_ALGORITHM);
            sr.setSeed(Base64.decodeBase64(SEED));
            byte[] buffer = sr.generateSeed(SECRET_SIZE);
            Base32 codec = new Base32();
            byte[] bEncodedKey = codec.encode(buffer);
            return new String(bEncodedKey);
        } catch (NoSuchAlgorithmException e) {
            // should never occur... configuration error
        }
        return null;
    }

    public static String getQRBarcodeURL(String user, String host, String secret) {
        String format = "https://quickchart.io/chart?chs=400x400&chld=M%%7C0&cht=qr&chl=otpauth://totp/%s@%s" +
                "%%3Fsecret%%3D%s";
        return String.format(format, user, host, secret);
    }

    private static int verify_code(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int) truncatedHash;
    }

    public boolean check_code(String secret, long code, long timeMsec) {
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
        long t = (timeMsec / 1000L) / 30L;
        for (int i = -window_size; i <= window_size; ++i) {
            long hash;
            try {
                hash = verify_code(decodedKey, t + i);
            } catch (Exception e) {
                log.error("Google check code exception:", e);
                return false;
            }
            if (hash == code) {
                return true;
            }
        }
        return false;
    }
}
 
 
 
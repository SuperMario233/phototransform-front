package com.binarypheasant.freestyle;

import android.util.Base64;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

final class Encrypt {
    private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbfh667adfASDFG";

    public static String encrypt(String password) {
        // 先进行一道md5加密
        String md5Encryption = Encrypt.md5Encrypt(password);
        try {
            // 再进行一次AES加密
            String aesEncryption = Encrypt.aesEncrypt(md5Encryption);
            return aesEncryption;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String aesEncrypt(String password) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(Encrypt.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(password.getBytes("utf-8"));
        String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
        return encryptedValue64;
    }

    public static String aesDecrypt(String password) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(Encrypt.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.decode(password, Base64.DEFAULT);
        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
        String decryptedValue = new String(decryptedByteValue,"utf-8");
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(Encrypt.KEY.getBytes(), Encrypt.ALGORITHM);
        return key;
    }

    private static String md5Encrypt(String password) {
        try {
            // 得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(password.getBytes());
            StringBuffer buffer = new StringBuffer();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }

            // 标准的md5加密后的结果
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}

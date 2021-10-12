package com.young.utils.func;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DHUtil {

    public static void main(String[] args) throws Exception {

        //前端生成这三个数
        BigDecimal q = new BigDecimal(7);
        BigDecimal p = new BigDecimal(100);
        BigDecimal a = new BigDecimal(77);

        //后端生成这个数
        BigDecimal b = new BigDecimal(99);

        //前端执行这个计算
        BigDecimal pa = q;
        for (int i = 1; i < a.intValue(); i++) {
            pa = pa.multiply(q);
        }
        pa = pa.remainder(p);
        //前端给后台传 p q pa


        //后端执行这个计算
        BigDecimal pb = q;
        for (int i = 1; i < b.intValue(); i++) {
            pb = pb.multiply(q);
        }
        pb = pb.remainder(p);
        //后端返给前端 pb


        //后端执行这个计算得到最终的数 sPa
        BigDecimal sPa = pa;
        for (int i = 1; i < b.intValue(); i++) {
            sPa = sPa.multiply(pa);
        }
        sPa = sPa.remainder(p);

        //前端执行这个计算得到最终数 sPb
        BigDecimal sPb = pb;
        for (int i = 1; i < a.intValue(); i++) {
            sPb = sPb.multiply(pb);
        }
        sPb = sPb.remainder(p);

        String serverKey = stringToMD5(sPb.toString());
        String clientKey = stringToMD5(sPa.toString());


        String passwordEc = encrypt("ygkj0818", serverKey);
        String passwordDc = decrypt(passwordEc, clientKey);
        System.out.println(passwordDc);
        //sPa = sPb
    }

    public static String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
    private static String Algorithm = "AES";

    private static String AlgorithmProvider = "AES/ECB/PKCS5Padding";

    public static String encrypt(String src, String k) throws Exception {
        SecretKey secretKey = new SecretKeySpec(k.getBytes("utf-8"), Algorithm);
        String s = new String(secretKey.getEncoded());
        System.out.println(s);
        Cipher cipher = Cipher.getInstance(AlgorithmProvider);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherBytes = cipher.doFinal(src.getBytes(Charset.forName("utf-8")));
        return Base64Utils.encodeToString(cipherBytes);
    }

    public static String decrypt(String src,String key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key.getBytes("utf-8"), Algorithm);
        Cipher cipher = Cipher.getInstance(AlgorithmProvider);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] hexBytes = Base64Utils.decodeFromString(src);
        byte[] plainBytes = cipher.doFinal(hexBytes);
        return new String(plainBytes, "utf-8");
    }
}

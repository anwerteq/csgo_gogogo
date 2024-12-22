package com.xiaojuzi.util.steamlogin;

import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;

/**
 * Created by Mr.W on 2017/5/22.
 * RSA加解密
 */

public class RSA {

    private final BigInteger modulus;
    private final BigInteger exponent;

    public RSA(String modHex, String expHex) {
        modulus = new BigInteger(modHex, 16);
        exponent = new BigInteger(expHex, 16);
    }

    static byte[] hexToByte(String hex) {
        if (hex.length() % 2 == 1) {
            return new byte[0];
        }

        final byte[] arr = new byte[hex.length() >> 1];
        final int l = hex.length();

        for (int i = 0; i < l >> 1; ++i) {
            arr[i] = (byte) ((getHexVal(hex.charAt(i << 1)) << 4) + getHexVal(hex.charAt((i << 1) + 1)));
        }

        return arr;
    }

    static int getHexVal(char hex) {
        final int val = hex;
        return val - (val < 58 ? 48 : 55);
    }

    public String encrypt(String password) {
        BigInteger data = pkcs1pad2(password.getBytes(), (modulus.bitLength() + 7) >> 3);
        BigInteger d2 = data.modPow(exponent, modulus);
        String dataHex = d2.toString(16);
        if ((dataHex.length() & 1) == 1) {
            dataHex = "0" + dataHex;
        }
        byte[] encrypted = hexStringToByteArray(dataHex);
        return Base64.encodeBase64String(encrypted);
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private BigInteger pkcs1pad2(byte[] data, int n) {
        byte[] bytes = new byte[n];
        int i = data.length - 1;
        while ((i >= 0) && (n > 11)) {
            bytes[--n] = data[i--];
        }
        bytes[--n] = 0;

        while (n > 2) {
            bytes[--n] = 0x01;
        }

        bytes[--n] = 0x2;
        bytes[--n] = 0;

        return new BigInteger(bytes);
    }
}

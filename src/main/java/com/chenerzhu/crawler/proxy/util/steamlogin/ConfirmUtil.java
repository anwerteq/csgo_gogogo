package com.chenerzhu.crawler.proxy.util.steamlogin;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Mr.W on 2017/6/16.
 * guard获取
 */
public class ConfirmUtil {

    private static final Logger log = LoggerFactory.getLogger(ConfirmUtil.class);

    private final static byte[] STEAM_GUARD_CODE_TRANSLATIONS = new byte[]{50, 51, 52, 53, 54, 55, 56, 57, 66, 67, 68, 70, 71, 72, 74, 75, 77, 78, 80, 81, 82, 84, 86, 87, 88, 89};

    /**
     * 获取令牌（动态码）
     *
     * @param sharedSecret share_secret
     * @return 动态码
     */
    public static String getGuard(String sharedSecret) {
        byte[] sharedSecretArray = Base64.decodeBase64(sharedSecret);
        byte[] timeArray = new byte[8];

        long time = TimeUtil.getTimeStamp();
        time /= 30L;

        for (int i = 8; i > 0; i--) {
            timeArray[i - 1] = (byte) time;
            time >>= 8;
        }

        try {
            SecretKeySpec signingKey = new SecretKeySpec(sharedSecretArray, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] hashedData = mac.doFinal(timeArray);
            byte[] codeArray = new byte[5];
            byte b = (byte) (hashedData[19] & 0xF);
            int codePoint = (hashedData[b] & 0x7F) << 24 | (hashedData[b + 1] & 0xFF) << 16 | (hashedData[b + 2] & 0xFF) << 8 | (hashedData[b + 3] & 0xFF);

            for (int i = 0; i < 5; ++i) {
                codeArray[i] = STEAM_GUARD_CODE_TRANSLATIONS[codePoint % STEAM_GUARD_CODE_TRANSLATIONS.length];
                codePoint /= STEAM_GUARD_CODE_TRANSLATIONS.length;
            }

            return new String(codeArray, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取generate_key
     *
     * @param identitySecret identity_secret
     * @param tag            - The tag which identifies what this request (and therefore key) will be for.
     *                       "conf" to load the confirmations page,
     *                       "details" to load details about a trade,
     *                       "allow" to confirm a trade,
     *                       "cancel" to cancel it.
     * @return key
     */
    public static String getKey(String identitySecret, String tag, long time) {
        byte[] decode = Base64.decodeBase64(identitySecret);
        int n2 = 8;
        if (tag != null) {
            if (tag.length() > 32) {
                n2 = 8 + 32;
            } else {
                n2 = 8 + tag.length();
            }
        }
        byte[] array = new byte[n2];
        int n3 = 8;
        while (true) {
            int n4 = n3 - 1;
            if (n3 <= 0) {
                break;
            }
            array[n4] = (byte) time;
            time >>= 8;
            n3 = n4;
        }
        if (tag != null) {
            System.arraycopy(tag.getBytes(StandardCharsets.UTF_8), 0, array, 8, n2 - 8);
        }

        try {
            SecretKeySpec signingKey = new SecretKeySpec(decode, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] hashedData = mac.doFinal(array);

            String encodedData = Base64.encodeBase64String(hashedData);
            //String hash = URLEncoder.encode(encodedData, "UTF8");
            return encodedData.replace("+", "%2B").replace("=", "%3D");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取deviceId
     *
     * @param steamId steamId
     * @return
     */
    public static String getDeviceID(String steamId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(steamId.getBytes());
            byte[] bytes = digest.digest();
            // 转换为16进制
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xFF) + 0x100, 16).substring(1));
            }
            String conId = sb.toString();
            String realConId = conId.substring(0, 8) + "-" + conId.substring(8, 12) + "-" +
                    conId.substring(12, 16) + "-" + conId.substring(16, 20) + "-" + conId.substring(20, 32);
            String deviceId = "android%3";
            deviceId += realConId;
            return deviceId;
        } catch (NoSuchAlgorithmException e) {
            log.error("sha1初始化失败");
            e.printStackTrace();
            return "";
        }
    }
}

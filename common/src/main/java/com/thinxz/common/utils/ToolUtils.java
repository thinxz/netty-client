package com.thinxz.common.utils;

import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 消息工具类
 *
 * @author thinxz 2019-03-01
 */
public class ToolUtils {
    /**
     * 获取校验和
     */
    public static byte checkXor(byte[] data, int pos, int len) {
        byte A = 0;
        for (int i = pos; i < len; i++) {
            A ^= data[i];
        }
        return A;
    }

    /**
     * @param hex      消息的字节数组
     * @param onlineNo 要替换成的卡号
     */
    public static void convertOnlineNo(byte[] hex, String onlineNo) {
        // 替换上线号
        byte[] temp = encodeBcd(onlineNo);
        int pos = 5;
        for (int i = 0; i < 6; i++) {
            hex[pos + i] = temp[i];
        }

        // 重新计算校验和
        hex[hex.length - 2] = checkXor(hex, 1, hex.length - 2);
    }

    /**
     * 替换位置消息发送时间解析 [GMT+8]
     *
     * @param now
     * @return
     */
    public static void convertDate(byte[] msgData, Date now) {
        int pos = 1 + 12 + 22;
        byte[] d = encodeDateToBcd(now);

        for (int i = 0; i < 6; i++) {
            msgData[pos + i] = d[i];
        }

        // 重新计算校验和
        msgData[msgData.length - 2] = checkXor(msgData, 1, msgData.length - 2);
    }

    /**
     * 对时间BCD编码 => BCD[6]
     */
    public static byte[] encodeDateToBcd(Date now) {
        return encodeBcd(new SimpleDateFormat("yyMMddHHmmss").format(now));
    }

    /**
     * BCD编码
     */
    public static byte[] encodeBcd(String str) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        char[] cs = str.toCharArray();
        for (int i = 0; i < cs.length; i += 2) {
            int high = cs[i] - 48;
            int low = cs[i + 1] - 48;
            baos.write(high << 4 | low);
        }
        return baos.toByteArray();
    }

    /**
     * 字节序转化为十六进制字符串（大写）
     */
    public static String encodeHexString(byte[] bytes) {
        return Hex.encodeHexString(bytes).toUpperCase();
    }
}

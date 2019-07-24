package com.thinxz.common.handler.decoder;

/**
 * 消息过滤器
 *
 * @author thinxz 2018-02-28
 */
public class Filter {

    /**
     * 判断是否过滤消息
     *
     * @param msgBytes 消息字节序
     * @return true 过滤 、false 不过滤
     */
    public static boolean isFilterMsg(byte[] msgBytes) {
        return false;
    }

}

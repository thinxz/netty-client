package com.thinxz.client.client;

import com.sinoxx.sserver.parser.util.ToolBuff;
import com.thinxz.common.utils.ToolUtils;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

/**
 * 客户端实体类
 *
 * @author thinxz 2018-03-01
 */
@Log4j
@Data
@Component
public class NettyClient {

    /**
     * 连接重试次数
     */
    private int retry = 2;
    /**
     * 服务端定义
     */
    private String host = "127.0.0.1";
    /**
     * 服务端监听端口
     */
    private int port = 8888;
    /**
     * 会话
     */
    private Channel channel;
    /**
     * 连接
     */
    private NettyClientServer nettyClientServer;

    public NettyClient() {
        this("47.99.112.121", 8888);
    }

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;

        // 创建客户端并连接
        this.nettyClientServer = new NettyClientServer(this);
    }

    public void send(byte[] msgBytes) {
        log.info(String.format("发送消息 => [%s]", ToolUtils.encodeHexString(msgBytes)));

        channel.writeAndFlush(msgBytes);
    }

    /**
     * 发送十六进制字符串
     *
     * @param hexStr 原始数据十六进制字符串
     */
    public void send(String hexStr) {
        byte[] bytes = ToolBuff.decodeHexString(hexStr);

//        // 转换时间
//        ToolUtils.convertDate(bytes, new Date());
//        // 转换上线号
        ToolUtils.convertOnlineNo(bytes, "017302522194");

        log.info(String.format("发送消息 => [%s]", ToolUtils.encodeHexString(bytes)));

        channel.writeAndFlush(bytes);
    }

    public void close() {
        try {
            System.out.println("关闭客户端");
            channel.close();
            nettyClientServer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

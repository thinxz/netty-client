package com.thinxz.client.client;

import com.thinxz.client.handler.ClientProcessorHandler;
import com.thinxz.common.handler.decoder.MsgDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import lombok.extern.log4j.Log4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Netty 客户端 创建会话连接到服务器
 *
 * @author thinxz 2018-02-28
 */
@Log4j
public class NettyClientServer {

    /**
     * 重试连接最大次数
     */
    private int MAX_RETRY = 5;
    /**
     * 启动线程
     */
    private Bootstrap bootstrap;
    /**
     * 工作线程
     */
    private NioEventLoopGroup workerGroup;

    public NettyClientServer(NettyClient msgClient) {
        if (msgClient != null) {
            // 连接并设置会话
            msgClient.setChannel(connect(msgClient, new ByteArrayEncoder(), new MsgDecoder(), new ClientProcessorHandler(msgClient)).channel());
        }
    }

    /**
     * 建立客户端连接
     *
     * @param msgClient 客户端定义
     * @return 会话
     */
    public ChannelFuture connect(NettyClient msgClient, ChannelHandler encoder, ChannelHandler decoder, ChannelHandler processorHandler) {
        // 初始化连接
        bootstrap = new Bootstrap();
        workerGroup = new NioEventLoopGroup();
        bootstrap
                // 指定线程模型
                .group(workerGroup)
                // 指定 IO 类型为 NIO
                .channel(NioSocketChannel.class)
                // IO 处理逻辑
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        // 消息编解码
                        ch.pipeline().addLast("encoder", encoder);
                        ch.pipeline().addLast("decoder", decoder);
                        // // 指定连接数据读写逻辑
                        ch.pipeline().addLast(processorHandler);
                    }
                });

        return connect(bootstrap, msgClient.getHost(), msgClient.getPort(), msgClient.getRetry());
    }

    private ChannelFuture connect(Bootstrap bootstrap, String host, int port, int retry) {
        ChannelFuture channelFuture =
                bootstrap.connect(host, port).addListener(future -> {
                    if (future.isSuccess()) {
                        log.info(String.format("%s:%s , 连接成功 ...", host, port));
                    } else if (retry == 0) {
                        log.error(String.format("%s:%s , 重试次数已用完，放弃连接 ...", host, port));
                    } else {
                        // 第几次重连
                        int order = (MAX_RETRY - retry) + 1;
                        // 本次重连的间隔
                        int delay = 1 << order;
                        log.warn(String.format("%s: 连接失败，%s:%s  -> 第[%s]次重连 ...", new Date(), host, port, order));
                        bootstrap
                                .config()
                                .group()
                                .schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
                    }
                });
        return channelFuture;
    }

    public void close() {
        this.workerGroup.shutdownGracefully();

        log.info(String.format("workerGroup [%s] -> 关闭 ... ", workerGroup.toString()));
    }

}

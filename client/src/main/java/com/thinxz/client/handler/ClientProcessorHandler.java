package com.thinxz.client.handler;

import com.thinxz.client.client.NettyClient;
import com.thinxz.common.module.entity.T808Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j;

/**
 * 业务读写处理
 *
 * @author thinxz 2018-03-01
 */
@Log4j
public class ClientProcessorHandler extends SimpleChannelInboundHandler<T808Message> {

    /**
     * 关联的客户端对象
     */
    private NettyClient nettyClient;

    public ClientProcessorHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    /**
     * 读取数据 ， 客户端向服务端发来数据，每次都会回调此方法，表示有数据可读
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T808Message msg) {
        // 刷新会话连接
        nettyClient.setChannel(ctx.channel());

        log.info(String.format("收到服务端消息 : %s - %s", msg, msg.getHexMsg()));
    }

    /**
     * Netty TCP 连接异常 , 及连接的释放流程 : exceptionCaught -> channelInactive -> channelUnregistered
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

    }

    /**
     * handlerAdded() -> channelRegistered() -> channelActive() -> channelRead() -> channelReadComplete()
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {

    }

    /**
     * TCP 的建立
     * channel 的所有的业务逻辑链准备完毕（也就是说 channel 的 pipeline 中已经添加完所有的 handler）以及绑定好一个 NIO 线程之后，这条连接算是真正激活了，接下来就会回调到此方法。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    /**
     * TCP的释放，表明这条连接已经被关闭了，这条连接在 TCP 层面已经不再是 ESTABLISH 状态了
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

    }

    /**
     * 表明与这条连接对应的 NIO 线程移除掉对这条连接的处理
     * channelInactive() -> channelUnregistered() -> handlerRemoved()
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {

    }

}

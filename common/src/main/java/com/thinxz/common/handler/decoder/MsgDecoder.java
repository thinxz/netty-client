package com.thinxz.common.handler.decoder;

import com.sinoxx.sserver.parser.service.ParserFactory;
import com.thinxz.common.module.entity.T808Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 消息协议解析器
 *
 * @author thinxz 2018-02-28
 */
public class MsgDecoder extends ByteToMessageDecoder {

    private byte TYPE_TAG = 0x7E;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in == null) {
            return;
        }
        in.markReaderIndex();
        while (in.isReadable()) {
            // 粘包
            byte[] bytes = stickyBag(in);
            if (bytes != null) {
                // 解码消息 并 触发接收Message的事件
                out.add(ParserFactory.parser(bytes, T808Message.class));
            }
        }
        return;
    }

    /**
     * 消息粘包
     *
     * @param in ByteBuf
     * @return 消息字节序
     */
    private byte[] stickyBag(ByteBuf in) {
        byte[] result = null;
        in.markReaderIndex();
        int packetBeginIndex = in.readerIndex();
        byte tag = in.readByte();
        // 搜索包的开始位置
        if (tag == TYPE_TAG && in.isReadable()) {
            tag = in.readByte();
            // 防止是两个0x7E,取后面的为包的开始位置
            // 寻找包的结束
            while (tag != TYPE_TAG) {
                if (in.isReadable() == false) {
                    // 没有找到结束包，等待下一次包
                    in.resetReaderIndex();
                    return null;
                }
                tag = in.readByte();
            }
            int pos = in.readerIndex();
            int packetLength = pos - packetBeginIndex;
            if (packetLength > 1) {
                byte[] tmp = new byte[packetLength];
                in.resetReaderIndex();
                in.readBytes(tmp);
                // 过滤黑名单消息
                if (Filter.isFilterMsg(tmp)) {
                    return null;
                }

                result = tmp;
            } else {
                // 说明是两个0x7E
                in.resetReaderIndex();
                // 两个7E说明前面是包尾，后面是包头
                in.readByte();
            }
        }
        return result;
    }

}

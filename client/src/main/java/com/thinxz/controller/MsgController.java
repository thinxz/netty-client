package com.thinxz.controller;

import com.thinxz.client.client.NettyClient;
import lombok.extern.log4j.Log4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Log4j
@RestController
@RequestMapping("/msg")
public class MsgController {

    @Autowired
    private NettyClient nettyClient;

    /**
     * 发送心跳包 , 7E000200000173025221940F22B87E
     */
    @RequestMapping(value = "/send/heartbeat", method = {RequestMethod.GET})
    public String send() {
        if (nettyClient == null || nettyClient.getChannel() == null) {
            return "please input real msg, or server is fail";
        }

        new Thread() {
            @Override
            public void run() {
                // 测试心跳
                int a = 0;
                do {
                    a++;
                    try {
                        new Thread().run();
                        Thread.sleep(5 * 1000L);
                        nettyClient.send("7E000200000173025221940F22B87E");
                        Thread.sleep(1 * 1000L);
                    } catch (Exception e) {
                        log.error(String.format("定时发送心跳错误"), e);
                    }
                } while (a < 3);
            }
        }.run();

        return "success";
    }

    /**
     * 发送消息
     *
     * @param msgHex 十六进制原始消息
     */
    @RequestMapping(value = "/send/msg", method = {RequestMethod.GET})
    public String sendHex(@RequestParam(value = "msgHex", required = false) String msgHex) {
        if (Strings.isBlank(msgHex) || msgHex.length() < 12 || nettyClient == null || nettyClient.getChannel() == null) {
            return "please input real msg, or server is fail";
        }

        nettyClient.send(msgHex);

        return "success";
    }

}

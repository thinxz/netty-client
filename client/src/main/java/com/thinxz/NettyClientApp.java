package com.thinxz;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Netty 的客户端
 *
 * @author thinxz 2019-02-28
 */
@SpringBootApplication
public class NettyClientApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = new SpringApplicationBuilder(NettyClientApp.class)
                // 禁用解析命令行参数, 启动时命令参数失效
                //.addCommandLineProperties(false)
                // 输出启动程序PID
                //.listeners(new ApplicationPidFileWriter("./app.pid"))
                .run(args);
    }
}

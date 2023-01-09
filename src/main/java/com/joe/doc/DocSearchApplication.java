package com.joe.doc;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

/**
 * @author JoeBlackZ
 */
@Slf4j
@SpringBootApplication
public class DocSearchApplication {

    @SneakyThrows
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DocSearchApplication.class, args);
        Environment env = context.getEnvironment();
        String applicationName = env.getProperty("spring.application.name");
        String port = env.getProperty("server.port");
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        log.info("""
                        \r
                        ----------------------------------------------------------
                        \tApplication '{}' is running! Access URLs:
                        \tLocal: \t\thttp://localhost:{}
                        \tExternal: \thttp://{}:{}
                        \tDoc: \t\thttp://{}:{}/doc.html
                        ----------------------------------------------------------""",
                applicationName, port, hostAddress, port, hostAddress, port);
    }

}

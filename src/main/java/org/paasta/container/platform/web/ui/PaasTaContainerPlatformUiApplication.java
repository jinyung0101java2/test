package org.paasta.container.platform.web.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class PaasTaContainerPlatformUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaasTaContainerPlatformUiApplication.class, args);
    }

}

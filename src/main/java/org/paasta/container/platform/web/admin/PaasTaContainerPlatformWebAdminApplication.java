package org.paasta.container.platform.web.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class PaasTaContainerPlatformWebAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaasTaContainerPlatformWebAdminApplication.class, args);
    }

}

package dev.t3hw.fstest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@ComponentScan(basePackages = { "dev.t3hw" })
public class FsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FsApplication.class, args);
	}
}

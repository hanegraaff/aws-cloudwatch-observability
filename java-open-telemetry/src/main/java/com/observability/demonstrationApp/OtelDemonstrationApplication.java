package com.observability.demonstrationApp;

import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;
import java.util.Scanner;


@SpringBootApplication
public class OtelDemonstrationApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(OtelDemonstrationApplication.class, args);
	}

}

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

		// run tests
		//downloadWebData("http://localhost:8080/gotoall");


	}

	private static String downloadWebData(String webAddress)
			throws Exception {

		URL url = new URL(webAddress);
		Scanner sc = new Scanner(url.openStream());

		StringBuffer sb = new StringBuffer();
		while (sc.hasNext()) {
			sb.append(sc.next());
		}
		return sb.toString();
	}

}

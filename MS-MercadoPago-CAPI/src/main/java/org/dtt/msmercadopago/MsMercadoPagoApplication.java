package org.dtt.msmercadopago;

import org.dtt.msmercadopago.config.BackUrlsPath;
import org.dtt.msmercadopago.config.MercadoPagoCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({MercadoPagoCredentials.class, BackUrlsPath.class})
public class MsMercadoPagoApplication implements CommandLineRunner {

	@Value("${auth.service-name}")
	String serviceName;
	@Value("${auth.api-key}")
	private String apiKey;



	public static void main(String[] args) {
		SpringApplication.run(MsMercadoPagoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Aplicación iniciada 🚀");
		System.out.println("Service Name: " + serviceName);
		System.out.println("API Key: " + apiKey);
	}

}

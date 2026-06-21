package org.dtt.msmercadopago.config;

import com.mercadopago.client.merchantorder.MerchantOrderClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MercadoPagoConfig {

    private final BackUrlsPath backUrls;

    @Bean
    public PreferenceBackUrlsRequest preferenceBackUrls() {
        log.info("BackUrls - success={}, failure={}, pending={}",
                backUrls.success(),
                backUrls.failure(),
                backUrls.pending());
        
        return PreferenceBackUrlsRequest.builder()
                .success(backUrls.success())
                .failure(backUrls.failure())
                .pending(backUrls.pending())
                .build();
    }

    @Bean
    public PreferenceClient preferenceClient() {
        return new PreferenceClient();
    }

    @Bean
    public MerchantOrderClient merchantOrderClient() {
        return new MerchantOrderClient();
    }
}
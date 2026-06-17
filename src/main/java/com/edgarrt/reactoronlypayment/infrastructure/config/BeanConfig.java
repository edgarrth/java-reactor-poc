package com.edgarrt.reactoronlypayment.infrastructure.config;
import com.edgarrt.reactoronlypayment.domain.service.PaymentAuthorizationPolicy;
import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration;
@Configuration public class BeanConfig { @Bean PaymentAuthorizationPolicy paymentAuthorizationPolicy(){ return new PaymentAuthorizationPolicy(); } }

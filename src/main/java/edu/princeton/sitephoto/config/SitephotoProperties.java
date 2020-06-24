package edu.princeton.sitephoto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties
@PropertySource("classpath:sitephotoltiprovider.properties")
@Getter
@Setter
public class SitephotoProperties {
    @Value("${key}")
    private String key;
    @Value("${secret}")
    private String secret;

}
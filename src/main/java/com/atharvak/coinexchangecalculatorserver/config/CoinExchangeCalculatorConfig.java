package com.atharvak.coinexchangecalculatorserver.config;

import com.atharvak.coinexchangecalculatorserver.utils.ChangeMaker;
import com.atharvak.coinexchangecalculatorserver.utils.CoinExchanger;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoinExchangeCalculatorConfig {

    @Bean
    public CoinExchanger coinExchanger(final ChangeMaker changeMaker) {
        return new CoinExchanger(changeMaker);
    }

    @Bean
    public ChangeMaker changeMaker() {
        return new ChangeMaker();
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                connector.setProperty("relaxedQueryChars", "|{}[]");
            }
        });
        return factory;
    }
}

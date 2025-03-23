package donts.ai;

import donts.ai.weather.WeatherApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(WeatherApiProperties.class)
public class DontsAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DontsAiApplication.class, args);
    }

}

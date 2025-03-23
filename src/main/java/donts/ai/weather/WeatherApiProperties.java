package donts.ai.weather;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather.api")
public record WeatherApiProperties(
        /*
          天气API的api key
         */
        String apiKey
) {
}

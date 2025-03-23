package donts.ai;

import donts.ai.demo.calculator.CalculatorMcpServer;
import donts.ai.demo.weather.WeatherMcpServer;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfig {
    @Bean
    public ToolCallbackProvider weatherTools(WeatherMcpServer weatherMcpServer) {
        return MethodToolCallbackProvider.builder().toolObjects(weatherMcpServer).build();
    }

    @Bean
    public ToolCallbackProvider calculatorTools(CalculatorMcpServer calculatorMcpServer) {
        return MethodToolCallbackProvider.builder().toolObjects(calculatorMcpServer).build();
    }

}

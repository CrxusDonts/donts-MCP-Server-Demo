package donts.ai;

import donts.ai.demo.calculator.CalculatorMcpServer;
import donts.ai.demo.weather.WeatherMcpServer;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfig {
    /**
     * 所有的工具
     * @param weatherMcpServer 查询天气
     * @param calculatorMcpServer 计算
     * @return ToolProvider
     */
    @Bean
    public ToolCallbackProvider allDemoToolsProvider(WeatherMcpServer weatherMcpServer
            , CalculatorMcpServer calculatorMcpServer) {
        return MethodToolCallbackProvider.builder().toolObjects(weatherMcpServer
                , calculatorMcpServer).build();
    }


}

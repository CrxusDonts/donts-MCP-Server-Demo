package donts.ai.demo.weather;

import org.springframework.ai.tool.annotation.ToolParam;

public record WeatherFunctionRequest(
        @ToolParam(description = "城市名称，可以是城市拼音，也可以是城市中文名")
        String city
) {
}

package donts.ai.demo.calculator;


import org.springframework.ai.tool.annotation.ToolParam;


public record CalculatorFunctionRequest(
        @ToolParam(description = "数学表达式")
        String expression
) {
}

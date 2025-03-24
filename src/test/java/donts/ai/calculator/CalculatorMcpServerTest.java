package donts.ai.calculator;

import donts.ai.demo.tools.calculator.CalculatorFunctionRequest;
import donts.ai.demo.tools.calculator.CalculatorMcpServer;
import org.junit.jupiter.api.Test;

class CalculatorMcpServerTest {

    @Test
    void calculate() {
        CalculatorFunctionRequest request = new CalculatorFunctionRequest(
                "((15.75 * 4.2 + 36.8) / (2.5 - 1.3)) * ((87.6 / 4 - 12.9) + (56.2 * 3.1 - 45.6)) / (18.4 + 7.6 * 2.3 - 5.8)"
        );
        CalculatorMcpServer calculatorMcpServer = new CalculatorMcpServer();
        String result = calculatorMcpServer.calculate(request);
        System.out.println(result);
    }
}
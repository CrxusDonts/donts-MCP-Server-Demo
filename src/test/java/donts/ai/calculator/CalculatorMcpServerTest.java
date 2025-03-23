package donts.ai.calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorMcpServerTest {

    @Test
    void calculate() {
        CalculatorFunctionRequest request = new CalculatorFunctionRequest(
                "(2.5 * T(Math).pow(3, 2) + T(Math).sqrt(144)) / (T(Math).log10(100) + T(Math).sin(T(Math).PI/6)) - T(Math).abs(-7) * (15 % 4) + 5.5"
        );
        CalculatorMcpServer calculatorMcpServer = new CalculatorMcpServer();
        String result = calculatorMcpServer.calculate(request);
        System.out.println(result);
    }
}
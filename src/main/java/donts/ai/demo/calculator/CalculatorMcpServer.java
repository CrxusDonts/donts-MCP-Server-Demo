package donts.ai.demo.calculator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CalculatorMcpServer {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Tool(description = "计算数学表达式的结果")
    public String calculate(@ToolParam(description = "参数对象") CalculatorFunctionRequest request) {
        String expression = request.expression();
        log.info("开始计算数学表达式：{}", expression);
        
        try {
            // 预处理表达式，确保整数除法结果为浮点数
            String processedExpression = preprocessExpression(expression);
            log.info("预处理后的表达式：{}", processedExpression);
            
            // 使用Spring Expression Language (SpEL)计算表达式
            Expression exp = parser.parseExpression(processedExpression);
            Object result = exp.getValue();
            log.info("计算结果：{}", result);
            
            return "表达式：" + expression + "\n计算结果：" + result;
        } catch (Exception e) {
            log.error("计算过程中发生错误", e);
            return "计算出错：" + e.getMessage();
        }
    }
    
    /**
     * 预处理表达式，确保整数除法结果为浮点数，防止按照java的规则，3/5=0 
     * 
     * @param expression 原始表达式
     * @return 处理后的表达式
     */
    private String preprocessExpression(String expression) {
        // 将整数转换为浮点数，确保除法运算结果为浮点数
        // 简单的方法是将所有整数数字后面添加.0
        StringBuilder result = new StringBuilder();
        StringBuilder number = new StringBuilder();
        boolean inNumber = false;
        boolean hasDecimal = false;
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            if (Character.isDigit(c)) {
                number.append(c);
                inNumber = true;
            } else if (c == '.') {
                number.append(c);
                hasDecimal = true;
                inNumber = true;
            } else {
                if (inNumber) {
                    // 如果是一个整数，添加.0使其成为浮点数
                    if (!hasDecimal && number.length() > 0) {
                        number.append(".0");
                    }
                    result.append(number);
                    number.setLength(0);
                    inNumber = false;
                    hasDecimal = false;
                }
                result.append(c);
            }
        }
        
        // 处理表达式末尾的数字
        if (inNumber) {
            if (!hasDecimal && !number.isEmpty()) {
                number.append(".0");
            }
            result.append(number);
        }
        
        return result.toString();
    }
}

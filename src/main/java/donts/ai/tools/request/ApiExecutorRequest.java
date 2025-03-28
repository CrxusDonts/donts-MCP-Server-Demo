package donts.ai.tools.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * API执行工具的请求类
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApiExecutorRequest extends BaseRequest {
    
    /**
     * API接口ID
     */
    private String apiId;
    
    /**
     * 用户输入的参数映射（参数名 -> 参数值）
     */
    private Map<String, Object> parameters;
    
    /**
     * 是否验证参数
     */
    private boolean validateParameters;
    
    /**
     * 是否返回详细响应
     */
    private boolean detailedResponse;
}

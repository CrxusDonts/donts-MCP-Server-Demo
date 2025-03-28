package donts.ai.tools.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * API执行工具的响应类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiExecutorResponse {
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 错误消息（如果有）
     */
    private String errorMessage;
    
    /**
     * API接口ID
     */
    private String apiId;
    
    /**
     * 响应数据
     */
    private Object responseData;
    
    /**
     * 响应元数据
     */
    private Map<String, Object> metadata;
    
    /**
     * 执行时间（毫秒）
     */
    private long executionTime;
}

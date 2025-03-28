package donts.ai.tools.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 工具请求基类
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseRequest {
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 请求时间戳
     */
    private long timestamp;
}

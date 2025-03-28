package donts.ai.tools.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据模型查找工具的请求类
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ModelFinderRequest extends BaseRequest {
    
    /**
     * 用户描述，用于查找匹配的数据模型
     */
    private String description;
    
    /**
     * 最大返回结果数量
     */
    private int maxResults;
    
    /**
     * 是否包含详细信息
     */
    private boolean includeDetails;
}

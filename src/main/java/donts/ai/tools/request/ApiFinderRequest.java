package donts.ai.tools.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * API接口查找工具的请求类
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApiFinderRequest extends BaseRequest {
    
    /**
     * 数据模型ID
     */
    private String modelId;
    
    /**
     * 最大返回结果数量
     */
    private int maxResults;
    
    /**
     * 是否包含详细信息
     */
    private boolean includeDetails;
    
    /**
     * 操作类型过滤（如：查询、创建、更新、删除）
     */
    private String operationType;
}

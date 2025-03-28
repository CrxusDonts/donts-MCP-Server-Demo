package donts.ai.tools.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API参数类，表示API接口的一个参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiParameter {
    
    /**
     * 参数名称
     */
    private String name;
    
    /**
     * 参数类型
     */
    private String type;
    
    /**
     * 参数描述
     */
    private String description;
    
    /**
     * 是否必填
     */
    private boolean required;
    
    /**
     * 参数位置（query, path, body, header等）
     */
    private String location;
    
    /**
     * 默认值
     */
    private String defaultValue;
    
    /**
     * 示例值
     */
    private String exampleValue;
    
    /**
     * 对应的数据模型字段（如果有）
     */
    private String modelFieldName;
}

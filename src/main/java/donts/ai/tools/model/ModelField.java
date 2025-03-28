package donts.ai.tools.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据模型字段类，表示数据模型中的一个字段
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelField {
    
    /**
     * 字段名称
     */
    private String name;
    
    /**
     * 字段类型
     */
    private String type;
    
    /**
     * 字段描述
     */
    private String description;
    
    /**
     * 是否必填
     */
    private boolean required;
    
    /**
     * 默认值
     */
    private String defaultValue;
    
    /**
     * 示例值
     */
    private String exampleValue;
}

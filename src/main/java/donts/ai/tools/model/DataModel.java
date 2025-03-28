package donts.ai.tools.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据模型类，表示系统中的一个数据实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataModel {
    
    /**
     * 模型ID
     */
    private String id;
    
    /**
     * 模型名称
     */
    private String name;
    
    /**
     * 模型描述
     */
    private String description;
    
    /**
     * 模型字段列表
     */
    private List<ModelField> fields;
    
    /**
     * 相关的API接口列表
     */
    private List<String> relatedApiIds;
    
    /**
     * 模型的关键词，用于搜索匹配
     */
    private List<String> keywords;
}

package donts.ai.tools.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * API接口类，表示系统中的一个API接口
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiInterface {
    
    /**
     * 接口ID
     */
    private String id;
    
    /**
     * 接口名称
     */
    private String name;
    
    /**
     * 接口描述
     */
    private String description;
    
    /**
     * 接口路径
     */
    private String path;
    
    /**
     * 请求方法（GET, POST, PUT, DELETE等）
     */
    private String method;
    
    /**
     * 请求参数列表
     */
    private List<ApiParameter> parameters;
    
    /**
     * 响应类型
     */
    private String responseType;
    
    /**
     * 相关的数据模型ID
     */
    private String relatedModelId;
    
    /**
     * 接口的关键词，用于搜索匹配
     */
    private List<String> keywords;
}

package donts.ai.tools.response;

import donts.ai.tools.model.DataModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据模型查找工具的响应类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelFinderResponse {
    
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
     * 匹配的数据模型列表
     */
    private List<DataModel> models;
    
    /**
     * 匹配度分数（0-100）
     */
    private List<Integer> matchScores;
    
    /**
     * 总结果数
     */
    private int totalResults;
}

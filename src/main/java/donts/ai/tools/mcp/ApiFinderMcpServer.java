package donts.ai.tools.mcp;

import donts.ai.tools.request.ApiFinderRequest;
import donts.ai.tools.response.ApiFinderResponse;
import donts.ai.tools.service.ApiFinderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * API接口查找工具的MCP服务器实现
 */
@Slf4j
@Service
public class ApiFinderMcpServer {

    @Autowired
    private ApiFinderService apiFinderService;
    
    /**
     * 根据数据模型ID查找API接口
     * 
     * @param request 请求参数
     * @return 查找结果
     */
    @Tool(description = "根据数据模型找到对应的接口并暴露参数")
    public String findApi(@ToolParam(description = "参数对象") ApiFinderToolRequest request) {
        log.info("接收到API接口查找请求: {}", request);
        
        // 构建服务请求
        ApiFinderRequest serviceRequest = ApiFinderRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .userId("system")
                .timestamp(System.currentTimeMillis())
                .modelId(request.modelId())
                .operationType(request.operationType())
                .maxResults(request.maxResults())
                .includeDetails(request.includeDetails())
                .build();
        
        // 调用服务
        ApiFinderResponse response = apiFinderService.findApisByModelId(serviceRequest);
        
        // 格式化响应
        StringBuilder result = new StringBuilder();
        result.append("API接口查找结果：\n\n");
        
        if (!response.isSuccess()) {
            result.append("查找失败: ").append(response.getErrorMessage());
            return result.toString();
        }
        
        if (response.getApis() == null || response.getApis().isEmpty()) {
            result.append("未找到匹配的API接口。");
            return result.toString();
        }
        
        result.append("找到 ").append(response.getTotalResults()).append(" 个匹配的API接口，显示前 ")
              .append(response.getApis().size()).append(" 个结果：\n\n");
        
        for (int i = 0; i < response.getApis().size(); i++) {
            var api = response.getApis().get(i);
            int score = response.getMatchScores().get(i);
            
            result.append("接口 ").append(i + 1).append("：\n");
            result.append("- ID: ").append(api.getId()).append("\n");
            result.append("- 名称: ").append(api.getName()).append("\n");
            result.append("- 描述: ").append(api.getDescription()).append("\n");
            result.append("- 路径: ").append(api.getPath()).append("\n");
            result.append("- 方法: ").append(api.getMethod()).append("\n");
            result.append("- 匹配度: ").append(score).append("%\n");
            
            if (request.includeDetails() && api.getParameters() != null) {
                result.append("- 参数列表:\n");
                for (var param : api.getParameters()) {
                    result.append("  * ").append(param.getName())
                          .append(" (").append(param.getType()).append(")")
                          .append(param.isRequired() ? " [必填]" : "")
                          .append(": ").append(param.getDescription());
                    
                    if (param.getDefaultValue() != null && !param.getDefaultValue().isEmpty()) {
                        result.append(" [默认值: ").append(param.getDefaultValue()).append("]");
                    }
                    
                    result.append("\n");
                }
            }
            
            result.append("\n");
        }
        
        return result.toString();
    }
    
    /**
     * API接口查找工具的请求记录类
     */
    public record ApiFinderToolRequest(
            @ToolParam(description = "数据模型ID")
            String modelId,
            @ToolParam(description = "操作类型")
            String operationType,
            @ToolParam(description = "最大结果数量")
            int maxResults,
            @ToolParam(description = "是否包含详细参数信息")
            boolean includeDetails
    ) {}
}

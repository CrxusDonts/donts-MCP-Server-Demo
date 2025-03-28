package donts.ai.tools.mcp;

import donts.ai.tools.request.ModelFinderRequest;
import donts.ai.tools.response.ModelFinderResponse;
import donts.ai.tools.service.ModelFinderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 数据模型查找工具的MCP服务器实现
 */
@Slf4j
@Service
public class ModelFinderMcpServer {

    @Autowired
    private ModelFinderService modelFinderService;
    
    /**
     * 根据用户描述查找数据模型
     * 
     * @param request 请求参数
     * @return 查找结果
     */
    @Tool(description = "根据用户的描述找到数据模型")
    public String findModel(@ToolParam(description = "参数对象") ModelFinderToolRequest request) {
        log.info("接收到数据模型查找请求: {}", request);
        
        // 构建服务请求
        ModelFinderRequest serviceRequest = ModelFinderRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .userId("system")
                .timestamp(System.currentTimeMillis())
                .description(request.description())
                .maxResults(request.maxResults())
                .includeDetails(request.includeDetails())
                .build();
        
        // 调用服务
        ModelFinderResponse response = modelFinderService.findModelsByDescription(serviceRequest);
        
        // 格式化响应
        StringBuilder result = new StringBuilder();
        result.append("数据模型查找结果：\n\n");
        
        if (!response.isSuccess()) {
            result.append("查找失败: ").append(response.getErrorMessage());
            return result.toString();
        }
        
        if (response.getModels() == null || response.getModels().isEmpty()) {
            result.append("未找到匹配的数据模型。");
            return result.toString();
        }
        
        result.append("找到 ").append(response.getTotalResults()).append(" 个匹配的数据模型，显示前 ")
              .append(response.getModels().size()).append(" 个结果：\n\n");
        
        for (int i = 0; i < response.getModels().size(); i++) {
            var model = response.getModels().get(i);
            int score = response.getMatchScores().get(i);
            
            result.append("模型 ").append(i + 1).append("：\n");
            result.append("- ID: ").append(model.getId()).append("\n");
            result.append("- 名称: ").append(model.getName()).append("\n");
            result.append("- 描述: ").append(model.getDescription()).append("\n");
            result.append("- 匹配度: ").append(score).append("%\n");
            
            if (request.includeDetails() && model.getFields() != null) {
                result.append("- 字段列表:\n");
                for (var field : model.getFields()) {
                    result.append("  * ").append(field.getName())
                          .append(" (").append(field.getType()).append(")")
                          .append(field.isRequired() ? " [必填]" : "")
                          .append(": ").append(field.getDescription()).append("\n");
                }
            }
            
            result.append("\n");
        }
        
        return result.toString();
    }
    
    /**
     * 数据模型查找工具的请求记录类
     */
    public record ModelFinderToolRequest(
            @ToolParam(description = "用户描述")
            String description,
            @ToolParam(description = "最大返回结果数量")
            int maxResults,
            @ToolParam(description = "是否包含详细信息")
            boolean includeDetails
    ) {}
}

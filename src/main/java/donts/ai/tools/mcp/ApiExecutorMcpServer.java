package donts.ai.tools.mcp;

import donts.ai.tools.request.ApiExecutorRequest;
import donts.ai.tools.response.ApiExecutorResponse;
import donts.ai.tools.service.ApiExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * API执行工具的MCP服务器实现
 */
@Slf4j
@Service
public class ApiExecutorMcpServer {

    @Autowired
    private ApiExecutorService apiExecutorService;
    
    /**
     * 执行API接口调用
     * 
     * @param request 请求参数
     * @return 执行结果
     */
    @Tool(description = "根据用户输入构造参数并调用实际接口")
    public String executeApi(@ToolParam(description = "参数对象") ApiExecutorToolRequest request) {
        log.info("接收到API执行请求: {}", request);
        
        // 构建服务请求
        ApiExecutorRequest serviceRequest = ApiExecutorRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .userId("system")
                .timestamp(System.currentTimeMillis())
                .apiId(request.apiId())
                .parameters(request.parameters())
                .validateParameters(request.validateParameters())
                .detailedResponse(request.detailedResponse())
                .build();
        
        // 调用服务
        ApiExecutorResponse response = apiExecutorService.executeApi(serviceRequest);
        
        // 格式化响应
        StringBuilder result = new StringBuilder();
        result.append("API接口执行结果：\n\n");
        
        if (!response.isSuccess()) {
            result.append("执行失败: ").append(response.getErrorMessage());
            return result.toString();
        }
        
        result.append("执行成功！\n");
        result.append("- API ID: ").append(response.getApiId()).append("\n");
        result.append("- 执行时间: ").append(response.getExecutionTime()).append("ms\n\n");
        
        if (response.getMetadata() != null && !response.getMetadata().isEmpty()) {
            result.append("元数据信息：\n");
            for (Map.Entry<String, Object> entry : response.getMetadata().entrySet()) {
                // 跳过复杂对象的详细输出
                if (entry.getValue() instanceof Map || entry.getValue() instanceof Iterable) {
                    result.append("- ").append(entry.getKey()).append(": [复杂对象]\n");
                } else {
                    result.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
            }
            result.append("\n");
        }
        
        result.append("响应数据：\n");
        if (response.getResponseData() instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = (Map<String, Object>) response.getResponseData();
            formatMapResponse(result, responseMap, 0);
        } else if (response.getResponseData() instanceof Iterable) {
            formatIterableResponse(result, (Iterable<?>) response.getResponseData(), 0);
        } else {
            result.append(response.getResponseData());
        }
        
        return result.toString();
    }
    
    /**
     * 格式化Map类型的响应数据
     */
    private void formatMapResponse(StringBuilder builder, Map<String, Object> map, int indent) {
        String indentStr = "  ".repeat(indent);
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.append(indentStr).append("- ").append(entry.getKey()).append(": ");
            
            if (entry.getValue() instanceof Map<?, ?>) {
                builder.append("\n");
                @SuppressWarnings("unchecked")
                Map<String, Object> valueMap = (Map<String, Object>) entry.getValue();
                formatMapResponse(builder, valueMap, indent + 1);
            } else if (entry.getValue() instanceof Iterable) {
                builder.append("\n");
                formatIterableResponse(builder, (Iterable<?>) entry.getValue(), indent + 1);
            } else {
                builder.append(entry.getValue()).append("\n");
            }
        }
    }
    
    /**
     * 格式化Iterable类型的响应数据
     */
    private void formatIterableResponse(StringBuilder builder, Iterable<?> iterable, int indent) {
        String indentStr = "  ".repeat(indent);
        int index = 0;
        
        for (Object item : iterable) {
            builder.append(indentStr).append("* 项目 ").append(index++).append(": ");
            
            if (item instanceof Map<?, ?>) {
                builder.append("\n");
                @SuppressWarnings("unchecked")
                Map<String, Object> itemMap = (Map<String, Object>) item;
                formatMapResponse(builder, itemMap, indent + 1);
            } else if (item instanceof Iterable) {
                builder.append("\n");
                formatIterableResponse(builder, (Iterable<?>) item, indent + 1);
            } else {
                builder.append(item).append("\n");
            }
        }
    }
    
    /**
     * API执行工具的请求记录类
     */
    public record ApiExecutorToolRequest(
            @ToolParam(description = "API接口ID")
            String apiId,
            @ToolParam(description = "参数对象")
            Map<String, Object> parameters,
            @ToolParam(description = "是否验证参数")
            boolean validateParameters,
            @ToolParam(description = "是否返回详细响应")
            boolean detailedResponse
    ) {}
}

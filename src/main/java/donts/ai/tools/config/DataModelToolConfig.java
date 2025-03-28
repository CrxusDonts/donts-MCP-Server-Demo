package donts.ai.tools.config;

import donts.ai.tools.mcp.ApiExecutorMcpServer;
import donts.ai.tools.mcp.ApiFinderMcpServer;
import donts.ai.tools.mcp.ModelFinderMcpServer;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 工具配置类，用于注册MCP工具
 */
@Configuration
public class DataModelToolConfig {
    
    /**
     * 注册所有数据模型和API工具
     * 
     * @param modelFinderMcpServer 数据模型查找工具
     * @param apiFinderMcpServer API接口查找工具
     * @param apiExecutorMcpServer API执行工具
     * @return ToolCallbackProvider
     */
    @Bean
    public ToolCallbackProvider allToolsProvider(
            ModelFinderMcpServer modelFinderMcpServer,
            ApiFinderMcpServer apiFinderMcpServer,
            ApiExecutorMcpServer apiExecutorMcpServer) {
        
        return MethodToolCallbackProvider.builder()
                .toolObjects(
                        modelFinderMcpServer,
                        apiFinderMcpServer,
                        apiExecutorMcpServer
                )
                .build();
    }
}

package donts.ai.demo.mcp_resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.Map;

public class SystemResource {


    public static McpServerFeatures.SyncResourceRegistration systemInfoResourceRegistration() {

        // 一个可以系统信息的资源
        var systemInfoResource = new McpSchema.Resource( // @formatter:off
                "system://info",
                "系统信息",
                "提供系统基础信息，包括 Java 运行时版本、操作系统信息、处理器数量等",
                "application/json", null
        );

        return new McpServerFeatures.SyncResourceRegistration(systemInfoResource, (request) -> {
            try {
                Map<String, Object> systemInfo = Map.of(
                        "javaVersion", System.getProperty("java.version"),
                        "osName", System.getProperty("os.name"),
                        "osVersion", System.getProperty("os.version"),
                        "osArch", System.getProperty("os.arch"),
                        "processors", Runtime.getRuntime().availableProcessors(),
                        "timestamp", System.currentTimeMillis());

                String jsonContent = new ObjectMapper().writeValueAsString(systemInfo);

                return new McpSchema.ReadResourceResult(
                        List.of(new McpSchema.TextResourceContents(request.uri(), "application/json", jsonContent)));
            }
            catch (Exception e) {
                throw new RuntimeException("无法获取系统信息", e);
            }
        });
    }
}

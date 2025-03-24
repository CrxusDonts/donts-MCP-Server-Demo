package donts.ai.demo.mcp_resources;

import io.modelcontextprotocol.server.McpServerFeatures;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ResourceConfig {
    @Bean
    public List<McpServerFeatures.SyncResourceRegistration> demoResources() {
        var systemInfoResourceRegistration = SystemResource.systemInfoResourceRegistration();
        return List.of(systemInfoResourceRegistration);
    }
}

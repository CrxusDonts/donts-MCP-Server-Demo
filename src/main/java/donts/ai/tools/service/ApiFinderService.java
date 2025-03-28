package donts.ai.tools.service;

import donts.ai.tools.model.ApiInterface;
import donts.ai.tools.model.DataModel;
import donts.ai.tools.repository.MockDataRepository;
import donts.ai.tools.request.ApiFinderRequest;
import donts.ai.tools.response.ApiFinderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API接口查找服务，根据数据模型查找匹配的API接口
 */
@Slf4j
@Service
public class ApiFinderService {

    @Autowired
    private MockDataRepository dataRepository;
    
    /**
     * 根据数据模型ID查找相关的API接口
     * 
     * @param request 查找请求
     * @return 查找响应
     */
    public ApiFinderResponse findApisByModelId(ApiFinderRequest request) {
        log.info("根据数据模型ID查找API接口: {}", request.getModelId());
        
        try {
            // 检查数据模型是否存在
            DataModel model = dataRepository.getDataModelById(request.getModelId());
            if (model == null) {
                return ApiFinderResponse.builder()
                        .requestId(request.getRequestId())
                        .success(false)
                        .errorMessage("未找到指定的数据模型: " + request.getModelId())
                        .build();
            }
            
            // 获取与数据模型相关的API接口
            List<ApiInterface> relatedApis = dataRepository.getApiInterfacesByModelId(request.getModelId());
            
            // 如果指定了操作类型，进行过滤
            if (request.getOperationType() != null && !request.getOperationType().isEmpty()) {
                relatedApis = filterApisByOperationType(relatedApis, request.getOperationType());
            }
            
            // 计算匹配度分数
            List<Integer> matchScores = calculateMatchScores(relatedApis, model);
            
            // 根据匹配度排序
            List<ApiInterface> sortedApis = sortApisByScore(relatedApis, matchScores);
            
            // 限制返回结果数量
            int maxResults = request.getMaxResults() > 0 ? request.getMaxResults() : 10;
            List<ApiInterface> resultApis = sortedApis.stream()
                    .limit(maxResults)
                    .collect(Collectors.toList());
            
            // 如果不需要详细信息，则简化API接口信息
            if (!request.isIncludeDetails()) {
                resultApis = resultApis.stream()
                        .map(this::simplifyApi)
                        .collect(Collectors.toList());
            }
            
            // 计算对应的分数
            List<Integer> resultScores = matchScores.subList(0, Math.min(matchScores.size(), maxResults));
            
            return ApiFinderResponse.builder()
                    .requestId(request.getRequestId())
                    .success(true)
                    .apis(resultApis)
                    .matchScores(resultScores)
                    .totalResults(relatedApis.size())
                    .modelId(request.getModelId())
                    .build();
            
        } catch (Exception e) {
            log.error("查找API接口时发生错误", e);
            return ApiFinderResponse.builder()
                    .requestId(request.getRequestId())
                    .success(false)
                    .errorMessage("查找API接口时发生错误: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 根据操作类型过滤API接口
     * 
     * @param apis API接口列表
     * @param operationType 操作类型
     * @return 过滤后的API接口列表
     */
    private List<ApiInterface> filterApisByOperationType(List<ApiInterface> apis, String operationType) {
        String opType = operationType.toLowerCase();
        
        return apis.stream()
                .filter(api -> {
                    // 根据API名称和关键词判断操作类型
                    String apiName = api.getName().toLowerCase();
                    List<String> keywords = api.getKeywords();
                    
                    // 查询操作
                    if (opType.contains("查询") || opType.contains("获取") || 
                        opType.contains("query") || opType.contains("get")) {
                        return apiName.contains("获取") || apiName.contains("查询") || 
                               apiName.contains("get") || apiName.contains("query") || 
                               api.getMethod().equalsIgnoreCase("GET") ||
                               keywords.stream().anyMatch(k -> k.contains("获取") || k.contains("查询") || 
                                                             k.contains("get") || k.contains("query"));
                    }
                    
                    // 创建操作
                    if (opType.contains("创建") || opType.contains("新建") || 
                        opType.contains("create") || opType.contains("add")) {
                        return apiName.contains("创建") || apiName.contains("新建") || 
                               apiName.contains("添加") || apiName.contains("create") || 
                               apiName.contains("add") || api.getMethod().equalsIgnoreCase("POST") ||
                               keywords.stream().anyMatch(k -> k.contains("创建") || k.contains("新建") || 
                                                             k.contains("添加") || k.contains("create") || 
                                                             k.contains("add"));
                    }
                    
                    // 更新操作
                    if (opType.contains("更新") || opType.contains("修改") || 
                        opType.contains("update") || opType.contains("edit")) {
                        return apiName.contains("更新") || apiName.contains("修改") || 
                               apiName.contains("编辑") || apiName.contains("update") || 
                               apiName.contains("edit") || api.getMethod().equalsIgnoreCase("PUT") ||
                               keywords.stream().anyMatch(k -> k.contains("更新") || k.contains("修改") || 
                                                             k.contains("编辑") || k.contains("update") || 
                                                             k.contains("edit"));
                    }
                    
                    // 删除操作
                    if (opType.contains("删除") || opType.contains("移除") || 
                        opType.contains("delete") || opType.contains("remove")) {
                        return apiName.contains("删除") || apiName.contains("移除") || 
                               apiName.contains("delete") || apiName.contains("remove") || 
                               api.getMethod().equalsIgnoreCase("DELETE") ||
                               keywords.stream().anyMatch(k -> k.contains("删除") || k.contains("移除") || 
                                                             k.contains("delete") || k.contains("remove"));
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 计算API接口与数据模型的匹配度分数
     * 
     * @param apis API接口列表
     * @param model 数据模型
     * @return 匹配度分数列表
     */
    private List<Integer> calculateMatchScores(List<ApiInterface> apis, DataModel model) {
        List<Integer> scores = new ArrayList<>();
        
        for (ApiInterface api : apis) {
            int score = 60;  // 基础分数
            
            // 检查API名称是否包含模型名称
            if (api.getName().toLowerCase().contains(model.getName().toLowerCase())) {
                score += 10;
            }
            
            // 检查API描述是否包含模型名称
            if (api.getDescription().toLowerCase().contains(model.getName().toLowerCase())) {
                score += 10;
            }
            
            // 根据HTTP方法调整分数
            switch (api.getMethod().toUpperCase()) {
                case "GET":
                    score += 5;  // 查询接口略微提高权重
                    break;
                case "POST":
                    score += 5;  // 创建接口略微提高权重
                    break;
                case "PUT":
                case "PATCH":
                    score += 0;  // 更新接口保持原权重
                    break;
                case "DELETE":
                    score -= 5;  // 删除接口略微降低权重
                    break;
                default:
                    break;
            }
            
            // 限制分数上限为100
            scores.add(Math.min(score, 100));
        }
        
        return scores;
    }
    
    /**
     * 根据匹配度分数对API接口进行排序
     * 
     * @param apis API接口列表
     * @param scores 匹配度分数列表
     * @return 排序后的API接口列表
     */
    private List<ApiInterface> sortApisByScore(List<ApiInterface> apis, List<Integer> scores) {
        // 创建API和分数的配对
        List<ApiScorePair> pairs = new ArrayList<>();
        for (int i = 0; i < apis.size(); i++) {
            pairs.add(new ApiScorePair(apis.get(i), scores.get(i)));
        }
        
        // 根据分数排序
        pairs.sort(Comparator.comparingInt(ApiScorePair::getScore).reversed());
        
        // 提取排序后的API
        return pairs.stream()
                .map(ApiScorePair::getApi)
                .collect(Collectors.toList());
    }
    
    /**
     * 简化API接口，移除详细参数信息
     * 
     * @param api 原始API接口
     * @return 简化后的API接口
     */
    private ApiInterface simplifyApi(ApiInterface api) {
        return ApiInterface.builder()
                .id(api.getId())
                .name(api.getName())
                .description(api.getDescription())
                .path(api.getPath())
                .method(api.getMethod())
                .responseType(api.getResponseType())
                .relatedModelId(api.getRelatedModelId())
                .build();
    }
    
    /**
     * API接口和匹配度分数的配对类
     */
    private static class ApiScorePair {
        private final ApiInterface api;
        private final int score;
        
        public ApiScorePair(ApiInterface api, int score) {
            this.api = api;
            this.score = score;
        }
        
        public ApiInterface getApi() {
            return api;
        }
        
        public int getScore() {
            return score;
        }
    }
}

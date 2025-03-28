package donts.ai.tools.service;

import donts.ai.tools.model.DataModel;
import donts.ai.tools.repository.MockDataRepository;
import donts.ai.tools.request.ModelFinderRequest;
import donts.ai.tools.response.ModelFinderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据模型查找服务，根据用户描述查找匹配的数据模型
 */
@Slf4j
@Service
public class ModelFinderService {

    @Autowired
    private MockDataRepository dataRepository;
    
    /**
     * 根据用户描述查找匹配的数据模型
     * 
     * @param request 查找请求
     * @return 查找响应
     */
    public ModelFinderResponse findModelsByDescription(ModelFinderRequest request) {
        log.info("根据描述查找数据模型: {}", request.getDescription());
        
        try {
            // 根据用户描述搜索匹配的数据模型
            List<DataModel> matchedModels = dataRepository.searchDataModelsByKeywords(request.getDescription());
            
            // 计算匹配度分数
            List<Integer> matchScores = calculateMatchScores(matchedModels, request.getDescription());
            
            // 根据匹配度排序
            List<DataModel> sortedModels = sortModelsByScore(matchedModels, matchScores);
            
            // 限制返回结果数量
            int maxResults = request.getMaxResults() > 0 ? request.getMaxResults() : 10;
            List<DataModel> resultModels = sortedModels.stream()
                    .limit(maxResults)
                    .collect(Collectors.toList());
            
            // 如果不需要详细信息，则清除字段信息
            if (!request.isIncludeDetails()) {
                resultModels = resultModels.stream()
                        .map(this::simplifyModel)
                        .collect(Collectors.toList());
            }
            
            // 计算对应的分数
            List<Integer> resultScores = matchScores.subList(0, Math.min(matchScores.size(), maxResults));
            
            return ModelFinderResponse.builder()
                    .requestId(request.getRequestId())
                    .success(true)
                    .models(resultModels)
                    .matchScores(resultScores)
                    .totalResults(matchedModels.size())
                    .build();
            
        } catch (Exception e) {
            log.error("查找数据模型时发生错误", e);
            return ModelFinderResponse.builder()
                    .requestId(request.getRequestId())
                    .success(false)
                    .errorMessage("查找数据模型时发生错误: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 计算数据模型与用户描述的匹配度分数
     * 
     * @param models 数据模型列表
     * @param description 用户描述
     * @return 匹配度分数列表
     */
    private List<Integer> calculateMatchScores(List<DataModel> models, String description) {
        List<Integer> scores = new ArrayList<>();
        String[] keywords = description.toLowerCase().split("\\s+");
        
        for (DataModel model : models) {
            int score = 0;
            
            // 检查模型名称匹配
            for (String keyword : keywords) {
                if (model.getName().toLowerCase().contains(keyword)) {
                    score += 30;  // 名称匹配权重高
                }
            }
            
            // 检查模型描述匹配
            for (String keyword : keywords) {
                if (model.getDescription().toLowerCase().contains(keyword)) {
                    score += 20;  // 描述匹配权重中等
                }
            }
            
            // 检查模型关键词匹配
            for (String modelKeyword : model.getKeywords()) {
                for (String keyword : keywords) {
                    if (modelKeyword.toLowerCase().contains(keyword) || 
                        keyword.contains(modelKeyword.toLowerCase())) {
                        score += 25;  // 关键词匹配权重较高
                    }
                }
            }
            
            // 限制分数上限为100
            scores.add(Math.min(score, 100));
        }
        
        return scores;
    }
    
    /**
     * 根据匹配度分数对数据模型进行排序
     * 
     * @param models 数据模型列表
     * @param scores 匹配度分数列表
     * @return 排序后的数据模型列表
     */
    private List<DataModel> sortModelsByScore(List<DataModel> models, List<Integer> scores) {
        // 创建模型和分数的配对
        List<ModelScorePair> pairs = new ArrayList<>();
        for (int i = 0; i < models.size(); i++) {
            pairs.add(new ModelScorePair(models.get(i), scores.get(i)));
        }
        
        // 根据分数排序
        pairs.sort(Comparator.comparingInt(ModelScorePair::getScore).reversed());
        
        // 提取排序后的模型
        return pairs.stream()
                .map(ModelScorePair::getModel)
                .collect(Collectors.toList());
    }
    
    /**
     * 简化数据模型，移除详细字段信息
     * 
     * @param model 原始数据模型
     * @return 简化后的数据模型
     */
    private DataModel simplifyModel(DataModel model) {
        return DataModel.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .keywords(model.getKeywords())
                .relatedApiIds(model.getRelatedApiIds())
                .build();
    }
    
    /**
     * 数据模型和匹配度分数的配对类
     */
    private static class ModelScorePair {
        private final DataModel model;
        private final int score;
        
        public ModelScorePair(DataModel model, int score) {
            this.model = model;
            this.score = score;
        }
        
        public DataModel getModel() {
            return model;
        }
        
        public int getScore() {
            return score;
        }
    }
}

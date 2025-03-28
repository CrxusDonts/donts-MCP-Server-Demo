package donts.ai.tools.service;

import donts.ai.tools.model.ApiInterface;
import donts.ai.tools.model.ApiParameter;
import donts.ai.tools.repository.MockDataRepository;
import donts.ai.tools.request.ApiExecutorRequest;
import donts.ai.tools.response.ApiExecutorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * API执行服务，根据用户输入构造参数并调用实际接口
 */
@Slf4j
@Service
public class ApiExecutorService {

    @Autowired
    private MockDataRepository dataRepository;
    
    /**
     * 执行API接口调用
     * 
     * @param request 执行请求
     * @return 执行响应
     */
    public ApiExecutorResponse executeApi(ApiExecutorRequest request) {
        log.info("执行API接口调用: {}", request.getApiId());

        long startTime = System.currentTimeMillis();

        try {
            // 检查API接口是否存在
            ApiInterface api = dataRepository.getApiInterfaceById(request.getApiId());
            if (api == null) {
                return ApiExecutorResponse.builder()
                        .requestId(request.getRequestId())
                        .success(false)
                        .errorMessage("未找到指定的API接口: " + request.getApiId())
                        .build();
            }

            // 验证参数
            if (request.isValidateParameters()) {
                String validationError = validateParameters(api, request.getParameters());
                if (validationError != null) {
                    return ApiExecutorResponse.builder()
                            .requestId(request.getRequestId())
                            .success(false)
                            .errorMessage(validationError)
                            .apiId(request.getApiId())
                            .executionTime(System.currentTimeMillis() - startTime)
                            .build();
                }
            }

            // 构造API调用参数
            Map<String, Object> apiParams = constructApiParameters(api, request.getParameters());

            // 模拟API调用
            Object responseData = mockApiCall(api, apiParams);

            // 构造元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("apiName", api.getName());
            metadata.put("apiPath", api.getPath());
            metadata.put("apiMethod", api.getMethod());
            metadata.put("timestamp", System.currentTimeMillis());
            metadata.put("responseType", api.getResponseType());

            // 如果需要详细响应，添加更多元数据
            if (request.isDetailedResponse()) {
                metadata.put("requestParams", apiParams);
                metadata.put("relatedModelId", api.getRelatedModelId());
            }

            return ApiExecutorResponse.builder()
                    .requestId(request.getRequestId())
                    .success(true)
                    .apiId(request.getApiId())
                    .responseData(responseData)
                    .metadata(metadata)
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception e) {
            log.error("执行API接口时发生错误", e);
            return ApiExecutorResponse.builder()
                    .requestId(request.getRequestId())
                    .success(false)
                    .errorMessage("执行API接口时发生错误: " + e.getMessage())
                    .apiId(request.getApiId())
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }
    }
    
    /**
     * 验证用户提供的参数是否符合API接口要求
     * 
     * @param api API接口
     * @param parameters 用户提供的参数
     * @return 验证错误信息，如果验证通过则返回null
     */
    private String validateParameters(ApiInterface api, Map<String, Object> parameters) {
        // 检查必填参数
        for (ApiParameter param : api.getParameters()) {
            if (param.isRequired()) {
                if (!parameters.containsKey(param.getName()) || parameters.get(param.getName()) == null) {
                    return "缺少必填参数: " + param.getName();
                }
            }
        }
        
        // 检查参数类型
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String paramName = entry.getKey();
            Object paramValue = entry.getValue();
            
            // 查找对应的API参数定义
            ApiParameter paramDef = api.getParameters().stream()
                    .filter(p -> p.getName().equals(paramName))
                    .findFirst()
                    .orElse(null);
            
            // 如果参数不在API定义中，跳过类型检查
            if (paramDef == null) {
                continue;
            }
            
            // 检查参数类型
            if (paramValue != null) {
                String paramType = paramDef.getType();
                boolean typeValid = checkParameterType(paramValue, paramType);
                if (!typeValid) {
                    return "参数类型错误: " + paramName + " 应为 " + paramType + " 类型";
                }
            }
        }
        
        return null;
    }
    
    /**
     * 检查参数值是否符合指定类型
     * 
     * @param value 参数值
     * @param type 参数类型
     * @return 是否符合类型
     */
    private boolean checkParameterType(Object value, String type) {
        switch (type.toLowerCase()) {
            case "string":
                return value instanceof String;
            case "integer":
                return value instanceof Integer || 
                       (value instanceof String && ((String) value).matches("-?\\d+"));
            case "double":
            case "float":
                return value instanceof Double || value instanceof Float || 
                       (value instanceof String && ((String) value).matches("-?\\d+(\\.\\d+)?"));
            case "boolean":
                return value instanceof Boolean || 
                       (value instanceof String && ("true".equalsIgnoreCase((String) value) || 
                                                   "false".equalsIgnoreCase((String) value)));
            case "list":
            case "array":
                return value instanceof Iterable;
            case "map":
            case "object":
                return value instanceof Map;
            default:
                return true;  // 对于未知类型，默认认为有效
        }
    }
    
    /**
     * 构造API调用参数
     * 
     * @param api API接口
     * @param userParams 用户提供的参数
     * @return 构造后的API参数
     */
    private Map<String, Object> constructApiParameters(ApiInterface api, Map<String, Object> userParams) {
        Map<String, Object> apiParams = new HashMap<>();
        
        // 处理每个API参数
        for (ApiParameter paramDef : api.getParameters()) {
            String paramName = paramDef.getName();
            
            // 如果用户提供了参数值，使用用户提供的值
            if (userParams.containsKey(paramName) && userParams.get(paramName) != null) {
                Object paramValue = userParams.get(paramName);
                
                // 转换参数类型
                Object convertedValue = convertParameterValue(paramValue, paramDef.getType());
                apiParams.put(paramName, convertedValue);
            } 
            // 如果用户未提供参数值，但参数有默认值，使用默认值
            else if (paramDef.getDefaultValue() != null && !paramDef.getDefaultValue().isEmpty()) {
                Object defaultValue = convertParameterValue(paramDef.getDefaultValue(), paramDef.getType());
                apiParams.put(paramName, defaultValue);
            }
            // 如果是必填参数但没有值，使用示例值
            else if (paramDef.isRequired() && paramDef.getExampleValue() != null) {
                Object exampleValue = convertParameterValue(paramDef.getExampleValue(), paramDef.getType());
                apiParams.put(paramName, exampleValue);
            }
        }
        
        return apiParams;
    }
    
    /**
     * 转换参数值为指定类型
     * 
     * @param value 参数值
     * @param type 参数类型
     * @return 转换后的参数值
     */
    private Object convertParameterValue(Object value, String type) {
        if (value == null) {
            return null;
        }
        
        try {
            switch (type.toLowerCase()) {
                case "string":
                    return value.toString();
                case "integer":
                    if (value instanceof Integer) {
                        return value;
                    } else if (value instanceof String) {
                        return Integer.parseInt((String) value);
                    }
                    break;
                case "double":
                case "float":
                    if (value instanceof Double) {
                        return value;
                    } else if (value instanceof Float) {
                        return ((Float) value).doubleValue();
                    } else if (value instanceof String) {
                        return Double.parseDouble((String) value);
                    }
                    break;
                case "boolean":
                    if (value instanceof Boolean) {
                        return value;
                    } else if (value instanceof String) {
                        return Boolean.parseBoolean((String) value);
                    }
                    break;
                default:
                    return value;  // 对于其他类型，保持原值
            }
        } catch (Exception e) {
            log.warn("转换参数值类型时发生错误: {} -> {}", value, type, e);
        }
        
        return value;  // 如果转换失败，返回原值
    }
    
    /**
     * 模拟API调用
     * 
     * @param api API接口
     * @param parameters API参数
     * @return 模拟的响应数据
     */
    private Object mockApiCall(ApiInterface api, Map<String, Object> parameters) {
        log.info("模拟调用API: {}, 参数: {}", api.getName(), parameters);
        
        // 根据API类型生成不同的模拟响应
        switch (api.getMethod().toUpperCase()) {
            case "GET":
                return mockGetResponse(api, parameters);
            case "POST":
                return mockPostResponse(api, parameters);
            case "PUT":
                return mockPutResponse(api, parameters);
            case "DELETE":
                return mockDeleteResponse(api, parameters);
            default:
                return Map.of("message", "不支持的HTTP方法: " + api.getMethod());
        }
    }
    
    /**
     * 模拟GET请求响应
     */
    private Object mockGetResponse(ApiInterface api, Map<String, Object> parameters) {
        // 根据API路径判断响应类型
        if (api.getPath().contains("/users/")) {
            return mockUserResponse(parameters);
        } else if (api.getPath().contains("/orders/")) {
            return mockOrderResponse(parameters);
        } else if (api.getPath().contains("/products/")) {
            if (!api.getPath().endsWith("/products")) {
                return mockProductResponse(parameters);
            } else {
                return mockProductListResponse(parameters);
            }
        }
        
        // 默认响应
        return Map.of(
            "id", UUID.randomUUID().toString(),
            "name", "模拟数据",
            "description", "这是一个模拟的GET响应",
            "timestamp", System.currentTimeMillis()
        );
    }
    
    /**
     * 模拟POST请求响应
     */
    private Object mockPostResponse(ApiInterface api, Map<String, Object> parameters) {
        // 生成一个新ID
        String newId = UUID.randomUUID().toString().substring(0, 8);
        
        // 构造响应，包含请求参数和新ID
        Map<String, Object> response = new HashMap<>(parameters);
        response.put("id", newId);
        response.put("createdAt", System.currentTimeMillis());
        response.put("success", true);
        response.put("message", "创建成功");
        
        return response;
    }
    
    /**
     * 模拟PUT请求响应
     */
    private Object mockPutResponse(ApiInterface api, Map<String, Object> parameters) {
        // 提取ID参数
        String id = null;
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            if (entry.getKey().toLowerCase().endsWith("id")) {
                id = entry.getValue().toString();
                break;
            }
        }
        
        if (id == null) {
            id = "unknown";
        }
        
        // 构造响应，包含请求参数和更新信息
        Map<String, Object> response = new HashMap<>(parameters);
        response.put("id", id);
        response.put("updatedAt", System.currentTimeMillis());
        response.put("success", true);
        response.put("message", "更新成功");
        
        return response;
    }
    
    /**
     * 模拟DELETE请求响应
     */
    private Object mockDeleteResponse(ApiInterface api, Map<String, Object> parameters) {
        return Map.of(
            "success", true,
            "message", "删除成功",
            "timestamp", System.currentTimeMillis()
        );
    }
    
    /**
     * 模拟用户响应
     */
    private Object mockUserResponse(Map<String, Object> parameters) {
        String userId = parameters.getOrDefault("userId", "u123456").toString();
        
        return Map.of(
            "id", userId,
            "username", "user_" + userId.substring(Math.max(0, userId.length() - 4)),
            "email", "user_" + userId.substring(Math.max(0, userId.length() - 4)) + "@example.com",
            "phone", "138" + (System.currentTimeMillis() % 100000000),
            "age", 20 + (System.currentTimeMillis() % 50),
            "createTime", System.currentTimeMillis() - 86400000L * (System.currentTimeMillis() % 100)
        );
    }
    
    /**
     * 模拟订单响应
     */
    private Object mockOrderResponse(Map<String, Object> parameters) {
        String orderId = parameters.getOrDefault("orderId", "o987654").toString();
        
        return Map.of(
            "id", orderId,
            "userId", "u" + (System.currentTimeMillis() % 1000000),
            "totalAmount", 100.0 + (System.currentTimeMillis() % 10000) / 100.0,
            "status", "已支付",
            "createTime", System.currentTimeMillis() - 3600000L,
            "payTime", System.currentTimeMillis() - 1800000L,
            "products", List.of(
                Map.of("productId", "p123", "name", "智能手机", "price", 3999.0, "quantity", 1),
                Map.of("productId", "p456", "name", "无线耳机", "price", 999.0, "quantity", 2)
            )
        );
    }
    
    /**
     * 模拟商品响应
     */
    private Object mockProductResponse(Map<String, Object> parameters) {
        String productId = parameters.getOrDefault("productId", "p123456").toString();
        
        return Map.of(
            "id", productId,
            "name", "商品_" + productId.substring(Math.max(0, productId.length() - 4)),
            "price", 1000.0 + (System.currentTimeMillis() % 10000) / 10.0,
            "stock", 50 + (System.currentTimeMillis() % 200),
            "category", "电子产品",
            "description", "这是一个高品质的商品，物美价廉。"
        );
    }
    
    /**
     * 模拟商品列表响应
     */
    private Object mockProductListResponse(Map<String, Object> parameters) {
        int page = 1;
        int size = 10;
        
        if (parameters.containsKey("page")) {
            try {
                page = Integer.parseInt(parameters.get("page").toString());
            } catch (NumberFormatException e) {
                // 忽略解析错误
            }
        }
        
        if (parameters.containsKey("size")) {
            try {
                size = Integer.parseInt(parameters.get("size").toString());
            } catch (NumberFormatException e) {
                // 忽略解析错误
            }
        }
        
        String category = parameters.getOrDefault("category", "").toString();
        
        // 生成模拟商品列表
        List<Map<String, Object>> products = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            String productId = "p" + (page * size + i);
            Map<String, Object> product = new HashMap<>();
            product.put("id", productId);
            product.put("name", "商品_" + productId.substring(1));
            product.put("price", 1000.0 + (i * 100) + (System.currentTimeMillis() % 1000) / 10.0);
            product.put("stock", 50 + (i * 5) + (System.currentTimeMillis() % 50));
            
            if (!category.isEmpty()) {
                product.put("category", category);
            } else {
                String[] categories = {"电子产品", "家居用品", "服装", "食品", "图书"};
                product.put("category", categories[i % categories.length]);
            }
            
            product.put("description", "这是一个高品质的商品，物美价廉。");
            
            products.add(product);
        }
        
        return Map.of(
            "content", products,
            "page", page,
            "size", size,
            "totalElements", 100 + (System.currentTimeMillis() % 100),
            "totalPages", 10 + (System.currentTimeMillis() % 10)
        );
    }
}

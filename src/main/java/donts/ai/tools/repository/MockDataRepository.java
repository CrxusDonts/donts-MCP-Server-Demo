package donts.ai.tools.repository;

import donts.ai.tools.model.ApiInterface;
import donts.ai.tools.model.ApiParameter;
import donts.ai.tools.model.DataModel;
import donts.ai.tools.model.ModelField;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模拟数据仓库，用于存储和检索数据模型和API接口
 */
@Repository
public class MockDataRepository {
    
    private final Map<String, DataModel> dataModels = new HashMap<>();
    private final Map<String, ApiInterface> apiInterfaces = new HashMap<>();
    
    @PostConstruct
    public void init() {
        // 初始化一些模拟数据
        initializeUserModel();
        initializeOrderModel();
        initializeProductModel();
    }
    
    private void initializeUserModel() {
        // 创建用户模型
        List<ModelField> userFields = new ArrayList<>();
        userFields.add(new ModelField("id", "String", "用户ID", true, null, "u123456"));
        userFields.add(new ModelField("username", "String", "用户名", true, null, "zhangsan"));
        userFields.add(new ModelField("email", "String", "电子邮箱", true, null, "zhangsan@example.com"));
        userFields.add(new ModelField("phone", "String", "手机号码", false, null, "13800138000"));
        userFields.add(new ModelField("age", "Integer", "年龄", false, null, "28"));
        userFields.add(new ModelField("createTime", "Date", "创建时间", false, "当前时间", "2025-03-28T10:00:00"));
        
        DataModel userModel = new DataModel(
                "model_user",
                "用户",
                "系统用户数据模型，包含用户基本信息",
                userFields,
                List.of("api_user_get", "api_user_create", "api_user_update", "api_user_delete"),
                List.of("用户", "客户", "会员", "账号", "user", "customer", "account")
        );
        
        dataModels.put(userModel.getId(), userModel);
        
        // 创建用户相关API
        // 1. 获取用户API
        List<ApiParameter> getUserParams = new ArrayList<>();
        getUserParams.add(new ApiParameter("userId", "String", "用户ID", true, "path", null, "u123456", "id"));
        
        ApiInterface getUserApi = new ApiInterface(
                "api_user_get",
                "获取用户信息",
                "根据用户ID获取用户详细信息",
                "/api/users/{userId}",
                "GET",
                getUserParams,
                "UserDTO",
                "model_user",
                List.of("获取", "查询", "用户", "get", "query", "user")
        );
        
        apiInterfaces.put(getUserApi.getId(), getUserApi);
        
        // 2. 创建用户API
        List<ApiParameter> createUserParams = new ArrayList<>();
        createUserParams.add(new ApiParameter("username", "String", "用户名", true, "body", null, "zhangsan", "username"));
        createUserParams.add(new ApiParameter("email", "String", "电子邮箱", true, "body", null, "zhangsan@example.com", "email"));
        createUserParams.add(new ApiParameter("phone", "String", "手机号码", false, "body", null, "13800138000", "phone"));
        createUserParams.add(new ApiParameter("age", "Integer", "年龄", false, "body", null, "28", "age"));
        
        ApiInterface createUserApi = new ApiInterface(
                "api_user_create",
                "创建用户",
                "创建新用户",
                "/api/users",
                "POST",
                createUserParams,
                "UserDTO",
                "model_user",
                List.of("创建", "新建", "添加", "用户", "create", "add", "user")
        );
        
        apiInterfaces.put(createUserApi.getId(), createUserApi);
        
        // 3. 更新用户API
        List<ApiParameter> updateUserParams = new ArrayList<>();
        updateUserParams.add(new ApiParameter("userId", "String", "用户ID", true, "path", null, "u123456", "id"));
        updateUserParams.add(new ApiParameter("email", "String", "电子邮箱", false, "body", null, "zhangsan@example.com", "email"));
        updateUserParams.add(new ApiParameter("phone", "String", "手机号码", false, "body", null, "13800138000", "phone"));
        updateUserParams.add(new ApiParameter("age", "Integer", "年龄", false, "body", null, "28", "age"));
        
        ApiInterface updateUserApi = new ApiInterface(
                "api_user_update",
                "更新用户信息",
                "根据用户ID更新用户信息",
                "/api/users/{userId}",
                "PUT",
                updateUserParams,
                "UserDTO",
                "model_user",
                List.of("更新", "修改", "编辑", "用户", "update", "edit", "user")
        );
        
        apiInterfaces.put(updateUserApi.getId(), updateUserApi);
        
        // 4. 删除用户API
        List<ApiParameter> deleteUserParams = new ArrayList<>();
        deleteUserParams.add(new ApiParameter("userId", "String", "用户ID", true, "path", null, "u123456", "id"));
        
        ApiInterface deleteUserApi = new ApiInterface(
                "api_user_delete",
                "删除用户",
                "根据用户ID删除用户",
                "/api/users/{userId}",
                "DELETE",
                deleteUserParams,
                "Boolean",
                "model_user",
                List.of("删除", "移除", "用户", "delete", "remove", "user")
        );
        
        apiInterfaces.put(deleteUserApi.getId(), deleteUserApi);
    }
    
    private void initializeOrderModel() {
        // 创建订单模型
        List<ModelField> orderFields = new ArrayList<>();
        orderFields.add(new ModelField("id", "String", "订单ID", true, null, "o987654"));
        orderFields.add(new ModelField("userId", "String", "用户ID", true, null, "u123456"));
        orderFields.add(new ModelField("totalAmount", "Double", "订单总金额", true, null, "299.99"));
        orderFields.add(new ModelField("status", "String", "订单状态", true, "待支付", "待支付"));
        orderFields.add(new ModelField("createTime", "Date", "创建时间", false, "当前时间", "2025-03-28T10:00:00"));
        orderFields.add(new ModelField("payTime", "Date", "支付时间", false, null, "2025-03-28T10:15:00"));
        
        DataModel orderModel = new DataModel(
                "model_order",
                "订单",
                "用户订单数据模型，包含订单基本信息",
                orderFields,
                List.of("api_order_get", "api_order_create", "api_order_update", "api_order_cancel"),
                List.of("订单", "购买", "交易", "order", "purchase", "transaction")
        );
        
        dataModels.put(orderModel.getId(), orderModel);
        
        // 创建订单相关API
        // 1. 获取订单API
        List<ApiParameter> getOrderParams = new ArrayList<>();
        getOrderParams.add(new ApiParameter("orderId", "String", "订单ID", true, "path", null, "o987654", "id"));
        
        ApiInterface getOrderApi = new ApiInterface(
                "api_order_get",
                "获取订单信息",
                "根据订单ID获取订单详细信息",
                "/api/orders/{orderId}",
                "GET",
                getOrderParams,
                "OrderDTO",
                "model_order",
                List.of("获取", "查询", "订单", "get", "query", "order")
        );
        
        apiInterfaces.put(getOrderApi.getId(), getOrderApi);
        
        // 2. 创建订单API
        List<ApiParameter> createOrderParams = new ArrayList<>();
        createOrderParams.add(new ApiParameter("userId", "String", "用户ID", true, "body", null, "u123456", "userId"));
        createOrderParams.add(new ApiParameter("products", "List", "商品列表", true, "body", null, "[{\"productId\":\"p123\",\"quantity\":2}]", null));
        
        ApiInterface createOrderApi = new ApiInterface(
                "api_order_create",
                "创建订单",
                "创建新订单",
                "/api/orders",
                "POST",
                createOrderParams,
                "OrderDTO",
                "model_order",
                List.of("创建", "新建", "添加", "订单", "create", "add", "order")
        );
        
        apiInterfaces.put(createOrderApi.getId(), createOrderApi);
        
        // 3. 更新订单API
        List<ApiParameter> updateOrderParams = new ArrayList<>();
        updateOrderParams.add(new ApiParameter("orderId", "String", "订单ID", true, "path", null, "o987654", "id"));
        updateOrderParams.add(new ApiParameter("status", "String", "订单状态", false, "body", null, "已支付", "status"));
        
        ApiInterface updateOrderApi = new ApiInterface(
                "api_order_update",
                "更新订单信息",
                "根据订单ID更新订单信息",
                "/api/orders/{orderId}",
                "PUT",
                updateOrderParams,
                "OrderDTO",
                "model_order",
                List.of("更新", "修改", "编辑", "订单", "update", "edit", "order")
        );
        
        apiInterfaces.put(updateOrderApi.getId(), updateOrderApi);
        
        // 4. 取消订单API
        List<ApiParameter> cancelOrderParams = new ArrayList<>();
        cancelOrderParams.add(new ApiParameter("orderId", "String", "订单ID", true, "path", null, "o987654", "id"));
        cancelOrderParams.add(new ApiParameter("reason", "String", "取消原因", false, "query", null, "商品缺货", null));
        
        ApiInterface cancelOrderApi = new ApiInterface(
                "api_order_cancel",
                "取消订单",
                "根据订单ID取消订单",
                "/api/orders/{orderId}/cancel",
                "POST",
                cancelOrderParams,
                "Boolean",
                "model_order",
                List.of("取消", "撤销", "订单", "cancel", "revoke", "order")
        );
        
        apiInterfaces.put(cancelOrderApi.getId(), cancelOrderApi);
    }
    
    private void initializeProductModel() {
        // 创建商品模型
        List<ModelField> productFields = new ArrayList<>();
        productFields.add(new ModelField("id", "String", "商品ID", true, null, "p123456"));
        productFields.add(new ModelField("name", "String", "商品名称", true, null, "智能手机"));
        productFields.add(new ModelField("price", "Double", "商品价格", true, null, "3999.00"));
        productFields.add(new ModelField("stock", "Integer", "库存数量", true, "0", "100"));
        productFields.add(new ModelField("category", "String", "商品类别", false, null, "电子产品"));
        productFields.add(new ModelField("description", "String", "商品描述", false, null, "最新款智能手机，性能强劲"));
        
        DataModel productModel = new DataModel(
                "model_product",
                "商品",
                "商品数据模型，包含商品基本信息",
                productFields,
                List.of("api_product_get", "api_product_list", "api_product_create", "api_product_update"),
                List.of("商品", "产品", "货物", "product", "goods", "item")
        );
        
        dataModels.put(productModel.getId(), productModel);
        
        // 创建商品相关API
        // 1. 获取商品API
        List<ApiParameter> getProductParams = new ArrayList<>();
        getProductParams.add(new ApiParameter("productId", "String", "商品ID", true, "path", null, "p123456", "id"));
        
        ApiInterface getProductApi = new ApiInterface(
                "api_product_get",
                "获取商品信息",
                "根据商品ID获取商品详细信息",
                "/api/products/{productId}",
                "GET",
                getProductParams,
                "ProductDTO",
                "model_product",
                List.of("获取", "查询", "商品", "get", "query", "product")
        );
        
        apiInterfaces.put(getProductApi.getId(), getProductApi);
        
        // 2. 商品列表API
        List<ApiParameter> listProductParams = new ArrayList<>();
        listProductParams.add(new ApiParameter("category", "String", "商品类别", false, "query", null, "电子产品", "category"));
        listProductParams.add(new ApiParameter("page", "Integer", "页码", false, "query", "1", "1", null));
        listProductParams.add(new ApiParameter("size", "Integer", "每页数量", false, "query", "10", "10", null));
        
        ApiInterface listProductApi = new ApiInterface(
                "api_product_list",
                "获取商品列表",
                "获取商品列表，支持分页和类别过滤",
                "/api/products",
                "GET",
                listProductParams,
                "Page<ProductDTO>",
                "model_product",
                List.of("列表", "查询", "商品", "list", "query", "product")
        );
        
        apiInterfaces.put(listProductApi.getId(), listProductApi);
        
        // 3. 创建商品API
        List<ApiParameter> createProductParams = new ArrayList<>();
        createProductParams.add(new ApiParameter("name", "String", "商品名称", true, "body", null, "智能手机", "name"));
        createProductParams.add(new ApiParameter("price", "Double", "商品价格", true, "body", null, "3999.00", "price"));
        createProductParams.add(new ApiParameter("stock", "Integer", "库存数量", true, "body", "0", "100", "stock"));
        createProductParams.add(new ApiParameter("category", "String", "商品类别", false, "body", null, "电子产品", "category"));
        createProductParams.add(new ApiParameter("description", "String", "商品描述", false, "body", null, "最新款智能手机，性能强劲", "description"));
        
        ApiInterface createProductApi = new ApiInterface(
                "api_product_create",
                "创建商品",
                "创建新商品",
                "/api/products",
                "POST",
                createProductParams,
                "ProductDTO",
                "model_product",
                List.of("创建", "新建", "添加", "商品", "create", "add", "product")
        );
        
        apiInterfaces.put(createProductApi.getId(), createProductApi);
        
        // 4. 更新商品API
        List<ApiParameter> updateProductParams = new ArrayList<>();
        updateProductParams.add(new ApiParameter("productId", "String", "商品ID", true, "path", null, "p123456", "id"));
        updateProductParams.add(new ApiParameter("name", "String", "商品名称", false, "body", null, "智能手机", "name"));
        updateProductParams.add(new ApiParameter("price", "Double", "商品价格", false, "body", null, "3999.00", "price"));
        updateProductParams.add(new ApiParameter("stock", "Integer", "库存数量", false, "body", null, "100", "stock"));
        updateProductParams.add(new ApiParameter("category", "String", "商品类别", false, "body", null, "电子产品", "category"));
        updateProductParams.add(new ApiParameter("description", "String", "商品描述", false, "body", null, "最新款智能手机，性能强劲", "description"));
        
        ApiInterface updateProductApi = new ApiInterface(
                "api_product_update",
                "更新商品信息",
                "根据商品ID更新商品信息",
                "/api/products/{productId}",
                "PUT",
                updateProductParams,
                "ProductDTO",
                "model_product",
                List.of("更新", "修改", "编辑", "商品", "update", "edit", "product")
        );
        
        apiInterfaces.put(updateProductApi.getId(), updateProductApi);
    }
    
    /**
     * 根据ID获取数据模型
     */
    public DataModel getDataModelById(String id) {
        return dataModels.get(id);
    }
    
    /**
     * 获取所有数据模型
     */
    public List<DataModel> getAllDataModels() {
        return new ArrayList<>(dataModels.values());
    }
    
    /**
     * 根据关键词搜索数据模型
     */
    public List<DataModel> searchDataModelsByKeywords(String keywords) {
        String[] keywordArray = keywords.toLowerCase().split("\\s+");
        
        return dataModels.values().stream()
                .filter(model -> {
                    // 检查模型名称、描述和关键词是否匹配搜索关键词
                    String modelText = model.getName().toLowerCase() + " " + 
                                      model.getDescription().toLowerCase() + " " + 
                                      String.join(" ", model.getKeywords()).toLowerCase();
                    
                    for (String keyword : keywordArray) {
                        if (modelText.contains(keyword)) {
                            return true;
                        }
                    }
                    
                    return false;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取API接口
     */
    public ApiInterface getApiInterfaceById(String id) {
        return apiInterfaces.get(id);
    }
    
    /**
     * 获取所有API接口
     */
    public List<ApiInterface> getAllApiInterfaces() {
        return new ArrayList<>(apiInterfaces.values());
    }
    
    /**
     * 根据数据模型ID获取相关的API接口
     */
    public List<ApiInterface> getApiInterfacesByModelId(String modelId) {
        DataModel model = dataModels.get(modelId);
        if (model == null || model.getRelatedApiIds() == null) {
            return new ArrayList<>();
        }
        
        return model.getRelatedApiIds().stream()
                .map(apiInterfaces::get)
                .filter(api -> api != null)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据关键词搜索API接口
     */
    public List<ApiInterface> searchApiInterfacesByKeywords(String keywords) {
        String[] keywordArray = keywords.toLowerCase().split("\\s+");
        
        return apiInterfaces.values().stream()
                .filter(api -> {
                    // 检查API名称、描述和关键词是否匹配搜索关键词
                    String apiText = api.getName().toLowerCase() + " " + 
                                    api.getDescription().toLowerCase() + " " + 
                                    String.join(" ", api.getKeywords()).toLowerCase();
                    
                    for (String keyword : keywordArray) {
                        if (apiText.contains(keyword)) {
                            return true;
                        }
                    }
                    
                    return false;
                })
                .collect(Collectors.toList());
    }
}

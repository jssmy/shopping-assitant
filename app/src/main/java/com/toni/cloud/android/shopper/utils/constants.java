package com.toni.cloud.android.shopper.utils;


public class constants {


    public static final String FAILED_LOGIN="FAILED_LOGIN";
    public static final String SERVICE_BASE_URL="https://e-commerce-u201319421.c9users.io/";
    public static final String SERVICE_AUTHENTICATION = SERVICE_BASE_URL+"api/auth/login";
    public static final String SERVICE_PRODUCTS =SERVICE_BASE_URL+"products";
    public static final String SERVICE_PRODUCT_DETAIL =SERVICE_BASE_URL+"product-detail/";
    public static final String SERVICE_ADD_ITEM_CART=SERVICE_BASE_URL+"api/shopp/add-cart-item";
    public static final String SERVICE_LIST_ITEM_CART= SERVICE_BASE_URL+"api/shopp/cart-item/";
    public static final String SERVICE_DELETE_ITEM_CART= SERVICE_BASE_URL+"api/shopp/delete-cart-item/";
    public static final String PREF_NAME = "UserSession";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_FULL_NAME = "full_name";
    public static final String KEY_EMAIL="email";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_EMPTY = "";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_STATUS = "status";
    public static final String KEY_MESSAGE = "message";

    public static final LanguageConfig[] languages = new LanguageConfig[]{
            new LanguageConfig("en", "a11ea1d839e3446d84e402cb97cdadfb"),
            new LanguageConfig("ru", "c8acebfbeeaa42ccb986e30573509055"),
            new LanguageConfig("de", "ae2afb2dfd3f4a02bb0da9dd32b78ff6"),
            new LanguageConfig("pt", "b27372e24ee44db48df4dccbd57ea021"),
            new LanguageConfig("pt-BR", "a4e08b5bc87a41098237e3f23a5e1351"),
            new LanguageConfig("es", "49be4c10b6a543dfb41d49d88731bd49"),
            new LanguageConfig("fr", "62161233bc094a75b3acfe16aeeed203"),
            new LanguageConfig("it", "57f80c9c9a2b4e0eae1739349a46e342"),
            new LanguageConfig("ja", "b92617a3f82e4b52b3db44436d2d4b8b"),
            new LanguageConfig("ko", "447a595234d74561a76b669a88ab3d99"),
            new LanguageConfig("zh-CN", "52d2b2bd992749409fc3a7d0605c3db4"),
            new LanguageConfig("zh-HK", "760c7a5efe5d43b9a90d62f73251de6a"),
            new LanguageConfig("zh-TW", "9cadea114425436cbaeaa504ea56555b"),
    };

    //https://e-commerce-u201319421.c9users.io/api/auth/login


}



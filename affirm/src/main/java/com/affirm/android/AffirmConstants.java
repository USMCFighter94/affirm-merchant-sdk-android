package com.affirm.android;

public final class AffirmConstants {

    private AffirmConstants() {
    }

    static final String SDK_NAME = "Affirm";

    static final String SANDBOX_URL = "sandbox.affirm.com";
    static final String SANDBOX_JS_URL = "cdn1-sandbox.affirm.com";
    static final String TRACKER_URL = "tracker.affirm.com";
    static final String PRODUCTION_URL = "api.affirm.com";
    static final String PRODUCTION_JS_URL = "cdn1.affirm.com";
    static final String CHECKOUT_PATH = "/api/v2/checkout/";
    static final String TRACKER_PATH = "/collect";
    static final String PROMO_PATH = "/api/promos/v2/%s?is_sdk=true&field=ala&amount=%d"
            + "&show_cta=%s";
    static final String PREQUAL_PATH = "/apps/prequal?public_api_key=%s&unit_price=%s"
            + "&promo_external_id=%s&isSDK=true&use_promo=True&referring_url=%s";
    static final String AFFIRM_CHECKOUT_CONFIRMATION_URL = "affirm://checkout/confirmed";
    static final String AFFIRM_CHECKOUT_CANCELLATION_URL = "affirm://checkout/cancelled";
    static final String REFERRING_URL = "https://androidsdk/";
    static final String JS_PATH = "/js/v2/affirm.js";

    static final String TAG_GET_NEW_PROMO = "GET_NEW_PROMO";
    static final String TAG_CHECKOUT = "CHECKOUT";
    static final String TAG_VCN_CHECKOUT = "VCN_CHECKOUT";
    static final String TAG_TRACKER = "TAG_TRACKER";

    static final String HTTPS_PROTOCOL = "https://";
    static final String HTTP_PROTOCOL = "http://";
    static final String HTTP = "http";
    static final String X_AFFIRM_REQUEST_ID = "X-Affirm-Request-Id";
    static final String CONTENT_TYPE = "application/json; charset=utf-8";
    static final String TEXT_HTML = "text/html";
    static final String UTF_8 = "utf-8";

    static final String AMOUNT = "AMOUNT";
    static final String PROMO_ID = "PROMO_ID";
    static final String PAGE_TYPE = "PAGE_TYPE";
    static final String MAP_EXTRA = "MAP_EXTRA";
    static final String TYPE_EXTRA = "TYPE_EXTRA";

    static final String API_KEY = "API_KEY";
    static final String JAVASCRIPT = "JAVASCRIPT";
    static final String CANCEL_URL = "CANCEL_URL";
    static final String MODAL_ID = "MODAL_ID";

    static final String LOGO_PLACEHOLDER = "{affirm_logo}";
    static final String PLACEHOLDER_START = "{{";
    static final String PLACEHOLDER_END = "}}";

    static final String CHECKOUT_TOKEN = "checkout_token";
    static final String CHECKOUT_ERROR = "checkout_error";
    static final String CHECKOUT_EXTRA = "checkout_extra";
    static final String CREDIT_DETAILS = "credit_details";
    static final String VCN_REASON = "vcn_reason";

    static final String PREQUAL_ERROR = "prequal_error";

    static final String URL = "URL";
    static final String URL2 = "URL2";
    static final String CONFIRM_CB_URL = "CONFIRM_CB_URL";
    static final String CANCELLED_CB_URL = "CANCELLED_CB_URL";

    static final String TRACK_ORDER_OBJECT = "TRACK_ORDER_OBJECT";
    static final String TRACK_PRODUCT_OBJECT = "TRACK_PRODUCT_OBJECT";

    static final String HTML_FRAGMENT = "HTML_FRAGMENT";
    static final String REMOTE_CSS_URL = "REMOTE_CSS_URL";
}

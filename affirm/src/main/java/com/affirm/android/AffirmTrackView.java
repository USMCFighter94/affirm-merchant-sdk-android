package com.affirm.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;

import com.affirm.android.exception.ConnectionException;
import com.affirm.android.model.AffirmTrack;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import androidx.annotation.NonNull;

import static com.affirm.android.AffirmConstants.API_KEY;
import static com.affirm.android.AffirmConstants.HTTPS_PROTOCOL;
import static com.affirm.android.AffirmConstants.JAVASCRIPT;
import static com.affirm.android.AffirmConstants.JS_PATH;
import static com.affirm.android.AffirmConstants.TEXT_HTML;
import static com.affirm.android.AffirmConstants.TRACK_ORDER_OBJECT;
import static com.affirm.android.AffirmConstants.TRACK_PRODUCT_OBJECT;
import static com.affirm.android.AffirmConstants.UTF_8;

@SuppressLint("ViewConstructor")
public class AffirmTrackView extends FrameLayout
        implements AffirmWebViewClient.WebViewClientCallbacks, AffirmWebChromeClient.Callbacks {

    interface AffirmTrackCallback {

        void onSuccess(AffirmTrackView affirmTrackView);

        void onFailed(AffirmTrackView affirmTrackView, String reason);
    }

    private AffirmWebView mWebView;

    private AffirmTrack mAffirmTrack;
    private AffirmTrackCallback mAffirmTrackCallback;

    private static final int DELAY_FINISH = 10 * 1000;
    private Handler mHandler = new Handler();

    public AffirmTrackView(@NonNull Context context, @NonNull AffirmTrack affirmTrack,
                           @NonNull AffirmTrackCallback affirmTrackCallback) {
        super(context, null, 0);

        this.mAffirmTrack = affirmTrack;
        this.mAffirmTrackCallback = affirmTrackCallback;

        setVisibility(View.GONE);

        initViews(context);
    }

    private void initViews(Context context) {
        mWebView = new AffirmWebView(context, null);
        addView(mWebView);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        AffirmUtils.debuggableWebView(getContext());
        mWebView.setWebViewClient(new TrackWebViewClient(this));
        mWebView.setWebChromeClient(new AffirmWebChromeClient(this));

        final String html = initialHtml();
        mWebView.loadData(html, TEXT_HTML, UTF_8);
        // Since there is no callback, the screen will be removed after 10 seconds timeout.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAffirmTrackCallback.onSuccess(AffirmTrackView.this);
            }
        }, DELAY_FINISH);
    }

    private String initialHtml() {
        String html;
        try {
            final InputStream ins =
                    getResources().openRawResource(R.raw.affirm_track_order_confirmed);
            html = AffirmUtils.readInputStream(ins);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final String fullPath = HTTPS_PROTOCOL + AffirmPlugins.get().baseUrl() + JS_PATH;

        final HashMap<String, String> map = new HashMap<>();

        map.put(API_KEY, AffirmPlugins.get().publicKey());
        map.put(JAVASCRIPT, fullPath);
        map.put(TRACK_ORDER_OBJECT, buildOrderObject().toString());
        map.put(TRACK_PRODUCT_OBJECT, buildProductObject().toString());
        return AffirmUtils.replacePlaceholders(html, map);
    }

    private JsonObject buildOrderObject() {
        final JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(AffirmPlugins.get().gson()
                .toJson(mAffirmTrack.affirmTrackOrder())).getAsJsonObject();
    }

    private JsonArray buildProductObject() {
        final JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(AffirmPlugins.get().gson()
                .toJson(mAffirmTrack.affirmTrackProducts())).getAsJsonArray();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CookiesUtil.clearCookies(getContext());
        mWebView.removeAllViews();
        mWebView.destroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onWebViewError(@NonNull ConnectionException error) {
        mAffirmTrackCallback.onFailed(this, error.toString());
    }

    @Override
    public void chromeLoadCompleted() {
        AffirmLog.v("ChromeLoadCompleted on AffirmTrackView");
    }
}
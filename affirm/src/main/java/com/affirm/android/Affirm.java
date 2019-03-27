package com.affirm.android;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.affirm.android.exception.AffirmException;
import com.affirm.android.model.CardDetails;
import com.affirm.android.model.Checkout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.affirm.android.Constants.CHECKOUT_ERROR;
import static com.affirm.android.Constants.CHECKOUT_TOKEN;
import static com.affirm.android.Constants.CREDIT_DETAILS;
import static com.affirm.android.Constants.PRODUCTION_URL;
import static com.affirm.android.Constants.SANDBOX_URL;
import static com.affirm.android.Constants.TRACKER_URL;
import static com.affirm.android.ModalActivity.ModalType.PRODUCT;
import static com.affirm.android.ModalActivity.ModalType.SITE;

public final class Affirm {

    private Affirm() {
    }

    public static final int LOG_LEVEL_VERBOSE = Log.VERBOSE;
    public static final int LOG_LEVEL_DEBUG = Log.DEBUG;
    public static final int LOG_LEVEL_INFO = Log.INFO;
    public static final int LOG_LEVEL_WARNING = Log.WARN;
    public static final int LOG_LEVEL_ERROR = Log.ERROR;
    public static final int LOG_LEVEL_NONE = Integer.MAX_VALUE;

    private static final int CHECKOUT_REQUEST = 8076;
    private static final int VCN_CHECKOUT_REQUEST = 8077;
    private static final int PREQUAL_REQUEST = 8078;
    static final int RESULT_ERROR = -8575;

    public interface PrequalCallbacks {
        void onAffirmPrequalError(@Nullable String message);

        void onAffirmPrequalCancelled();
    }

    public interface CheckoutCallbacks {
        void onAffirmCheckoutError(@Nullable String message);

        void onAffirmCheckoutCancelled();

        void onAffirmCheckoutSuccess(@NonNull String token);
    }

    public interface VcnCheckoutCallbacks {
        void onAffirmVcnCheckoutError(@Nullable String message);

        void onAffirmVcnCheckoutCancelled();

        void onAffirmVcnCheckoutSuccess(@NonNull CardDetails cardDetails);
    }

    public enum Environment {
        SANDBOX(SANDBOX_URL, TRACKER_URL),
        PRODUCTION(PRODUCTION_URL, TRACKER_URL);

        final String baseUrl;
        final String trackerBaseUrl;

        Environment(String baseUrl, String trackerBaseUrl) {
            this.baseUrl = baseUrl;
            this.trackerBaseUrl = trackerBaseUrl;
        }

        @Override
        public String toString() {
            return "Environment{" + baseUrl + ", " + trackerBaseUrl + '}';
        }
    }

    public static final class Configuration {
        final String publicKey;
        final Environment environment;
        final String merchantName;

        Configuration(Builder builder) {
            this.publicKey = builder.publicKey;
            this.environment = builder.environment;
            this.merchantName = builder.merchantName;
        }

        public static final class Builder {
            private final String publicKey;
            private final Environment environment;
            private String merchantName;

            /**
             * @param publicKey   Set the public key to be used by Affirm.
             * @param environment Set the environment to be used by Affirm.
             */
            public Builder(@NonNull String publicKey, @NonNull Environment environment) {
                this.publicKey = publicKey;
                this.environment = environment;
            }

            /**
             * Set the level of logging to display. The default level is {@link #LOG_LEVEL_NONE}.
             * <p>
             * Please ensure this is set to {@link #LOG_LEVEL_ERROR} or {@link #LOG_LEVEL_NONE}
             * before deploying your app.
             * <p>
             * The levels are:
             * <ul>
             * <li>{@link #LOG_LEVEL_VERBOSE}</li>
             * <li>{@link #LOG_LEVEL_DEBUG}</li>
             * <li>{@link #LOG_LEVEL_INFO}</li>
             * <li>{@link #LOG_LEVEL_WARNING}</li>
             * <li>{@link #LOG_LEVEL_ERROR}</li>
             * <li>{@link #LOG_LEVEL_NONE}</li>
             * </ul>
             *
             * @param logLevel The level of logcat logging that Affirm should do.
             * @return The same builder, for easy chaining.
             */
            public Builder setLogLevel(int logLevel) {
                AffirmLog.setLogLevel(logLevel);
                return this;
            }

            /**
             * Set your proud partnership name, it's optional
             *
             * @param merchantName your proud partnership name
             * @return The same builder, for easy chaining.
             */
            public Builder setMerchantName(@Nullable String merchantName) {
                this.merchantName = merchantName;
                return this;
            }

            /**
             * Construct this builder into a concrete {@code Configuration} instance.
             *
             * @return A constructed {@code Configuration} object.
             */
            public Configuration build() {
                AffirmUtils.requireNonNull(publicKey, "public key cannot be null");
                AffirmUtils.requireNonNull(environment, "environment cannot be null");
                return new Configuration(this);
            }
        }
    }

    /**
     * Returns the level of logging that will be displayed.
     */
    public static int getLogLevel() {
        return AffirmLog.getLogLevel();
    }

    public static void initialize(@NonNull Configuration configuration) {
        AffirmUtils.requireNonNull(configuration, "configuration cannot be null");

        if (isInitialized()) {
            AffirmLog.w("Affirm is already initialized");
            return;
        }
        AffirmPlugins.initialize(configuration);
    }

    private static boolean isInitialized() {
        return AffirmPlugins.get() != null;
    }

    /**
     * Start checkout flow/ vcn checkout flow. Don't forget to call onActivityResult
     * to get the processed result
     *
     * @param activity activity {@link Activity}
     * @param checkout checkout object that contains address & shipping info & others...
     * @param useVcn   Start VCN checkout or not
     */
    public static void startCheckout(@NonNull Activity activity, @NonNull Checkout checkout,
                                     boolean useVcn) {
        AffirmUtils.requireNonNull(activity, "activity cannot be null");
        AffirmUtils.requireNonNull(checkout, "checkout cannot be null");
        if (useVcn) {
            VcnCheckoutActivity.startActivity(activity, VCN_CHECKOUT_REQUEST, checkout);
        } else {
            CheckoutActivity.startActivity(activity, CHECKOUT_REQUEST, checkout);
        }
    }

    /**
     * Start site modal
     *
     * @param activity activity {@link Activity}
     * @param modalId  the client's modal id
     */
    public static void showSiteModal(@NonNull Activity activity, @Nullable String modalId) {
        AffirmUtils.requireNonNull(activity, "activity cannot be null");
        ModalActivity.startActivity(activity, 0, 0f, SITE, modalId);
    }

    /**
     * Start product modal
     *
     * @param activity activity {@link Activity}
     * @param amount   (Float) eg 112.02 as $112 and ¢2
     * @param modalId  the client's modal id
     */
    public static void showProductModal(@NonNull Activity activity, float amount,
                                        @Nullable String modalId) {
        AffirmUtils.requireNonNull(activity, "activity cannot be null");
        ModalActivity.startActivity(activity, 0, amount, PRODUCT, modalId);
    }

    /**
     * Write the as low as span (text and logo) on a AffirmPromoLabel
     *
     * @param promotionLabel AffirmPromotionLabel to show the promo message
     * @param promoId        the client's modal id
     * @param amount         (Float) eg 112.02 as $112 and ¢2
     * @param showCta        whether need to show cta
     */
    public static void configureWithAmount(@NonNull final AffirmPromotionLabel promotionLabel,
                                           @Nullable final String promoId,
                                           final float amount,
                                           final boolean showCta) {
        AffirmUtils.requireNonNull(promotionLabel, "AffirmPromotionLabel cannot be null");
        final SpannablePromoCallback callback = new SpannablePromoCallback() {
            @Override
            public void onPromoWritten(@NonNull final String promo, final boolean showPrequal) {
                promotionLabel.setTag(showPrequal);
                promotionLabel.setLabel(promo);
            }

            @Override
            public void onFailure(@NonNull AffirmException exception) {
                AffirmLog.e(exception.toString());
            }
        };

        final PromoRequest affirmPromoRequest =
                new PromoRequest(promoId, amount, showCta, callback);
        promotionLabel.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                affirmPromoRequest.create();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                affirmPromoRequest.cancel();
                promotionLabel.removeOnAttachStateChangeListener(this);
            }
        });

        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) v.getContext();
                if (activity == null || TextUtils.isEmpty(promotionLabel.getText())) {
                    return;
                }
                boolean showPrequal = (boolean) v.getTag();
                if (showPrequal) {
                    PrequalActivity.startActivity(activity,
                            PREQUAL_REQUEST, amount, promoId);
                } else {
                    ModalActivity.startActivity(activity,
                            PREQUAL_REQUEST, amount, PRODUCT, null);
                }
            }
        };
        promotionLabel.setOnClickListener(onClickListener);
    }

    /**
     * Helper method to get the Result from prequal
     */
    public static boolean handlePrequalData(@NonNull PrequalCallbacks callbacks,
                                            int requestCode,
                                            int resultCode,
                                            @Nullable Intent data) {
        AffirmUtils.requireNonNull(callbacks, "PrequalCallbacks cannot be null");

        if (requestCode == PREQUAL_REQUEST) {
            switch (resultCode) {
                case RESULT_CANCELED:
                    callbacks.onAffirmPrequalCancelled();
                    break;
                case RESULT_ERROR:
                    AffirmUtils.requireNonNull(data);
                    callbacks.onAffirmPrequalError(data.getStringExtra(CHECKOUT_ERROR));
                    break;
                default:
                    break;
            }

            return true;
        }

        return false;
    }

    /**
     * Helper method to get the Result from the `startCheckout`
     */
    public static boolean handleCheckoutData(@NonNull CheckoutCallbacks callbacks,
                                             int requestCode,
                                             int resultCode,
                                             @Nullable Intent data) {
        AffirmUtils.requireNonNull(callbacks, "CheckoutCallbacks cannot be null");

        if (requestCode == CHECKOUT_REQUEST) {
            switch (resultCode) {
                case RESULT_OK:
                    AffirmUtils.requireNonNull(data);
                    callbacks.onAffirmCheckoutSuccess(data.getStringExtra(CHECKOUT_TOKEN));
                    break;
                case RESULT_CANCELED:
                    callbacks.onAffirmCheckoutCancelled();
                    break;
                case RESULT_ERROR:
                    AffirmUtils.requireNonNull(data);
                    callbacks.onAffirmCheckoutError(data.getStringExtra(CHECKOUT_ERROR));
                    break;
                default:
                    break;
            }

            return true;
        }

        return false;
    }

    /**
     * Helper method to get the Result from the `startVcnCheckout`
     */
    public static boolean handleVcnCheckoutData(@NonNull VcnCheckoutCallbacks callbacks,
                                                int requestCode,
                                                int resultCode,
                                                @Nullable Intent data) {
        AffirmUtils.requireNonNull(callbacks, "VcnCheckoutCallbacks cannot be null");

        if (requestCode == VCN_CHECKOUT_REQUEST) {
            switch (resultCode) {
                case RESULT_OK:
                    AffirmUtils.requireNonNull(data);
                    callbacks.onAffirmVcnCheckoutSuccess(
                            (CardDetails) data.getParcelableExtra(CREDIT_DETAILS));
                    break;
                case RESULT_CANCELED:
                    callbacks.onAffirmVcnCheckoutCancelled();
                    break;
                case RESULT_ERROR:
                    AffirmUtils.requireNonNull(data);
                    callbacks.onAffirmVcnCheckoutError(data.getStringExtra(CHECKOUT_ERROR));
                    break;
                default:
                    break;
            }

            return true;
        }

        return false;
    }


}

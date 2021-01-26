/*
 * Copyright (c) 2020 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by josephj on 4/12/2020.
 */

package com.adyen.checkout.blik;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.adyen.checkout.base.PaymentComponentState;
import com.adyen.checkout.base.model.payments.request.BlikPaymentMethod;
import com.adyen.checkout.base.ui.view.AdyenLinearLayout;
import com.adyen.checkout.base.ui.view.AdyenTextInputEditText;
import com.adyen.checkout.blik.ui.R;
import com.adyen.checkout.core.code.Lint;
import com.adyen.checkout.core.exception.CheckoutException;
import com.adyen.checkout.core.log.LogUtil;
import com.adyen.checkout.core.log.Logger;

public class BlikView
        extends AdyenLinearLayout<BlikOutputData, BlikConfiguration, PaymentComponentState<BlikPaymentMethod>, BlikComponent>
        implements Observer<BlikOutputData> {
    private static final String TAG = LogUtil.getTag();

    @SuppressLint(Lint.SYNTHETIC)
    BlikInputData mBlikInputData = new BlikInputData();

    @SuppressLint(Lint.SYNTHETIC)
    TextInputLayout mBlikCodeInput;

    @SuppressLint(Lint.SYNTHETIC)
    AdyenTextInputEditText mBlikCodeEditText;

    public BlikView(@NonNull Context context) {
        this(context, null);
    }

    public BlikView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    // Regular View constructor
    @SuppressWarnings("JavadocMethod")
    public BlikView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater.from(getContext()).inflate(R.layout.blik_view, this, true);

        final int padding = (int) getResources().getDimension(R.dimen.standard_margin);
        setPadding(padding, padding, padding, 0);
    }

    @Override
    protected void initLocalizedStrings(@NonNull Context localizedContext) {
        final int[] myAttrs = {android.R.attr.hint};
        final TypedArray typedArray = localizedContext.obtainStyledAttributes(R.style.AdyenCheckout_Blik_BlikCodeInput, myAttrs);
        mBlikCodeInput.setHint(typedArray.getString(0));
        typedArray.recycle();
    }

    @Override
    public void initView() {
        mBlikCodeInput = findViewById(R.id.textInputLayout_blikCode);
        mBlikCodeEditText = (AdyenTextInputEditText) mBlikCodeInput.getEditText();

        if (mBlikCodeEditText == null) {
            throw new CheckoutException("Could not find views inside layout.");
        }

        mBlikCodeEditText.setOnChangeListener(new AdyenTextInputEditText.Listener() {
            @Override
            public void onTextChanged(@NonNull Editable editable) {
                mBlikInputData.setBlikCode(mBlikCodeEditText.getRawValue());
                notifyInputDataChanged();
                mBlikCodeInput.setError(null);
            }
        });

        mBlikCodeEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                final BlikOutputData outputData = getComponent().getOutputData();
                if (hasFocus) {
                    mBlikCodeInput.setError(null);
                } else if (outputData != null && !outputData.getBlikCodeField().isValid()) {
                    mBlikCodeInput.setError(mLocalizedContext.getString(R.string.checkout_blik_code_not_valid));
                }
            }
        });
    }

    @Override
    public void onChanged(@Nullable BlikOutputData blikOutputData) {
        Logger.v(TAG, "blikOutputData changed");
    }

    @Override
    public void onComponentAttached() {
        // nothing to impl
    }

    @Override
    protected void observeComponentChanges(@NonNull LifecycleOwner lifecycleOwner) {
        getComponent().observeOutputData(lifecycleOwner, this);
    }

    @Override
    public boolean isConfirmationRequired() {
        return true;
    }

    @Override
    public void highlightValidationErrors() {
        Logger.d(TAG, "highlightValidationErrors");

        final BlikOutputData outputData;
        if (getComponent().getOutputData() != null) {
            outputData = getComponent().getOutputData();
        } else {
            return;
        }

        if (!outputData.getBlikCodeField().isValid()) {
            mBlikCodeInput.requestFocus();
            mBlikCodeInput.setError(mLocalizedContext.getString(R.string.checkout_blik_code_not_valid));
        }
    }

    @SuppressLint(Lint.SYNTHETIC)
    void notifyInputDataChanged() {
        getComponent().inputDataChanged(mBlikInputData);
    }
}
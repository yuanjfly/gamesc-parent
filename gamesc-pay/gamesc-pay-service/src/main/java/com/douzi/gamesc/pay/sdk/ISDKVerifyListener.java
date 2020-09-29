package com.douzi.gamesc.pay.sdk;

/**
 * Created by ant on 2015/4/7.
 */
public interface ISDKVerifyListener {

    public void onSuccess(SDKVerifyResult result);

    public void onFailed(String errorMsg);

}

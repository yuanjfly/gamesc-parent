package com.douzi.gamesc.pay.sdk;

/**
 *
 * Created by ant on 2015/3/26.
 */
public interface ISDKOrderListener {

    public void onSuccess(String jsonStr);

    public void onFailed(String err);

}

package com.expelabs.social.webclient;

/**
 * Created with IntelliJ IDEA.
 * User: Вадим
 * Date: 24.05.13
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public interface AuthListener {

    void onSuccess(String url);
    void onError();
}

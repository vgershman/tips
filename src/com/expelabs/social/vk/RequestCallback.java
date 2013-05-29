package com.expelabs.social.vk;

import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Вадим
 * Date: 24.05.13
 * Time: 17:14
 * To change this template use File | Settings | File Templates.
 */
public interface RequestCallback {
    void onSuccess(JSONObject response);
    void onFailure();
}

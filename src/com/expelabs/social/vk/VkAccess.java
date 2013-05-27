package com.expelabs.social.vk;

import android.widget.Toast;

import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: Вадим
 * Date: 24.05.13
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
public class VkAccess {

    private final static String VK_METHOD_BASE = "https://api.vk.com/method/";

    public static void post(String content, String uid, String token, RequestCallback requestCallback) {
        String requestUrl = VK_METHOD_BASE + "wall.post?uid=<uid>&access_token=<ac>&message=<message>"
                .replace("<uid>", uid).replace("<ac>", token).replace("<message>", URLEncoder.encode(content));
        new RequestTask(requestUrl, requestCallback).execute();
    }
}

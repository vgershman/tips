package com.expelabs.social.view;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.expelabs.social.webclient.AuthClientInterface;

/**
 * Created with IntelliJ IDEA.
 * User: Вадим
 * Date: 24.05.13
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class OAuthDialog extends DialogFragment {

    private WebView webView;
    private AuthClientInterface webClient;

    public OAuthDialog(Context context, AuthClientInterface client) {
        webView = new WebView(context);
        webClient = client;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return webView;
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        webView.loadUrl(webClient.loginUrl());
        return super.show(transaction, tag);
    }
}




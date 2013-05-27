package com.expelabs.social.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.expelabs.social.webclient.AuthClientInterface;

/**
 * Created with IntelliJ IDEA.
 * User: Вадим
 * Date: 24.05.13
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class OAuthDialog extends Dialog {

    private WebView webView;
    private AuthClientInterface webClient;

    public OAuthDialog(Context context, AuthClientInterface client) {
        super(context);
        webView = new WebView(context);
        webClient = client;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(webView);
    }

    @Override
    public void show() {
        super.show();
        webView.loadUrl(webClient.loginUrl());
    }
}




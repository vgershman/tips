package com.expelabs.tips.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.expelabs.social.vk.RequestCallback;
import com.expelabs.social.vk.VkAccess;
import com.expelabs.tips.R;
import com.expelabs.tips.app.DailyTipsApp;
import com.expelabs.tips.delegate.NavigationDelegate;
import com.expelabs.tips.dto.Share;
import com.expelabs.tips.dto.Tip;
import com.expelabs.tips.util.ImageUtils;
import com.facebook.*;
import com.facebook.android.Facebook;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vadimgershman
 * Date: 15.05.13
 * Time: 0:20
 * To change this template use File | Settings | File Templates.
 */
public class TipFragment extends Fragment {

    private Tip tip;
    private ImageView socialButton;
    private static NavigationDelegate navigationDelegate;



    public static TipFragment newInstance(Tip tip, NavigationDelegate navigationDelegateParam) {
        navigationDelegate = navigationDelegateParam;
        TipFragment pageFragment = new TipFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable("tip", tip);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tip = (Tip) getArguments().get("tip");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tip_item, null, false);
        ((TextView) view.findViewById(R.id.tip_text)).setText(tip.getText());
        ((TextView) view.findViewById(R.id.tip_text_ital)).setText(tip.getTextItalic());
        view.findViewById(R.id.left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationDelegate.onLeft();
            }
        });
        view.findViewById(R.id.right_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationDelegate.onRight();
            }
        });
        socialButton = (ImageView) view.findViewById(R.id.social_button);
        final int share_id = getActivity().getSharedPreferences(DailyTipsApp.PREFERENCES_NAME, Context.MODE_PRIVATE).getInt("share", 0);
        switch (share_id) {
            case Share.VK:
                socialButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share_vk));
                break;
            case Share.FACEBOOK:
                socialButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share_fb));
                break;
            case Share.TWITTER:
                socialButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share_twit));
                break;
            case Share.EMAIL:
                socialButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share_email));
                break;
        }
        socialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (share_id) {
                    case Share.VK:
                        shareVK();
                        break;
                    case Share.FACEBOOK:
                        shareFB();
                        break;
                    case Share.TWITTER:
                        shareTwitter();
                        break;
                    case Share.EMAIL:
                        shareEmail();
                        break;
                }
            }
        });
        try {
            InputStream is = getActivity().getAssets().open("tipsImages/" + tip.getCategoryName() + "/" + tip.getId() + ".jpg");
            ((ImageView) view.findViewById(R.id.tip_image)).setImageBitmap(BitmapFactory.decodeStream(is));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return view;
    }

    private void shareEmail() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/image");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "See Nice Tip");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, tip.getText() + "\n" + tip.getTextItalic());
        try {
            emailIntent.putExtra(Intent.EXTRA_STREAM,
                    Uri.fromFile(new File(ImageUtils.writeImageToFile(BitmapFactory.decodeStream(getActivity().getAssets()
                            .open("tipsImages/" + tip.getCategoryName() + "/" + tip.getId() + ".jpg")), "temp"))));
        } catch (IOException e) {
            return;
        }
        startActivity(Intent.createChooser(emailIntent, "Share"));
    }

    private void shareTwitter() {
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("image/jpeg");
        List<ResolveInfo> resInfo = getActivity().getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                Intent targetedShare = new Intent(android.content.Intent.ACTION_SEND);
                targetedShare.setType("image/jpeg");
                if (info.activityInfo.packageName.toLowerCase().contains("twi") ||
                        info.activityInfo.name.toLowerCase().contains("twi")) {
                    targetedShare.putExtra(Intent.EXTRA_TEXT, tip.getText()+"\n"+tip.getTextItalic());
                    try {
                        targetedShare.putExtra(Intent.EXTRA_STREAM,
                                Uri.fromFile(new File(ImageUtils.writeImageToFile(BitmapFactory.decodeStream(getActivity().getAssets()
                                        .open("tipsImages/" + tip.getCategoryName() + "/" + tip.getId() + ".jpg")), "temp"))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    targetedShare.setPackage(info.activityInfo.packageName);
                    targetedShareIntents.add(targetedShare);
                }
            }
        }
        if(targetedShareIntents.size()==0){return;}
        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
        startActivity(chooserIntent);
    }


        private void shareFB() {
            Session session = Session.openActiveSession(getActivity(), true, new Session.StatusCallback() {
                @Override
                public void call(Session session, SessionState state, Exception exception) {

                }
            });
            final List<String> PERMISSIONS = Arrays.asList("publish_actions");

            if (session != null){


                // Check for publish permissions
                List<String> permissions = session.getPermissions();


                if(!permissions.contains(PERMISSIONS.get(0))){

                    Session.NewPermissionsRequest newPermissionsRequest = new Session
                            .NewPermissionsRequest(this, PERMISSIONS);
                    session.requestNewPublishPermissions(newPermissionsRequest);
                    return;
                }


                Bundle postParams = new Bundle();
                postParams.putString("name", "Facebook SDK for Android");
                postParams.putString("caption", "Build great social apps and get more installs.");
                postParams.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
                postParams.putString("link", "https://developers.facebook.com/android");
                postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

                Request.Callback callback= new Request.Callback() {
                    public void onCompleted(Response response) {
                        JSONObject graphResponse = response
                                .getGraphObject()
                               .getInnerJSONObject();
                        String postId = null;
                        try {
                            postId = graphResponse.getString("id");
                        } catch (JSONException e) {
                            Log.i("ES",
                                    "JSON error " + e.getMessage());
                        }
                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            Toast.makeText(getActivity()
                                    .getApplicationContext(),
                                    error.getErrorMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity()
                                    .getApplicationContext(),
                                    postId,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                };

                Request request = new Request(session, "me/feed", postParams,
                        HttpMethod.POST, callback);

                RequestAsyncTask task = new RequestAsyncTask(request);
                task.execute();
            }


    }

    private void shareVK() {
        String uid = getActivity().getSharedPreferences(DailyTipsApp.PREFERENCES_NAME,Context.MODE_PRIVATE).getString("VkUserId", "");
        String accessToken = getActivity().getSharedPreferences(DailyTipsApp.PREFERENCES_NAME,Context.MODE_PRIVATE).getString("VkAccessToken","");
        if(!uid.equals("")&& !accessToken.equals("")){
            VkAccess.post(tip.getText(),uid,accessToken,new RequestCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getActivity(),"Success",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public Tip getTip() {
        return tip;
    }
}

package com.expelabs.tips.fragment;

import android.support.v4.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.expelabs.tips.R;
import com.expelabs.tips.app.DailyTipsApp;
import com.expelabs.tips.dto.Tip;

/**
 * Created with IntelliJ IDEA.
 * User: vadimgershman
 * Date: 15.05.13
 * Time: 0:20
 * To change this template use File | Settings | File Templates.
 */
public class TipFragment extends Fragment {

    private Tip tip;

    public static TipFragment newInstance(Tip tip) {
        TipFragment pageFragment = new TipFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable("tip",tip);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tip = (Tip)getArguments().get("tip");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tip_item, null,false);
        ((TextView)view.findViewById(R.id.tip_text)).setText(tip.getText());
        ((TextView)view.findViewById(R.id.tip_text_ital)).setText(tip.getTextItalic());
        ((ImageView)view.findViewById(R.id.tip_image)).setImageBitmap(BitmapFactory.decodeFile(DailyTipsApp.IMAGES_PATH + tip.getCategoryName() + "/" + tip.getId()+".jpg"));
        return view;
    }
}

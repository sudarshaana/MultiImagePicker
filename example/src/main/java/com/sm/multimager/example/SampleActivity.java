package com.sm.multimager.example;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.sm.multimager.activities.BaseActivity;
import com.sm.multimager.activities.GalleryActivity;
import com.sm.multimager.activities.MultiCameraActivity;
import com.sm.multimager.adapters.GalleryImagesAdapter;
import com.sm.multimager.utils.Constants;
import com.sm.multimager.utils.Image;
import com.sm.multimager.utils.Params;
import com.sm.multimager.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vansikrishna on 08/06/2016.
 */
public class SampleActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.parentLayout)
    LinearLayout parentLayout;
    int selectedColor;
    int darkenedColor;
    @BindView(R.id.multiCaptureButton)
    Button multiCaptureButton;
    @BindView(R.id.multiPickerButton)
    Button multiPickerButton;
    @BindView(R.id.customThemeButton)
    Button customThemeButton;
    @BindView(R.id.call)
    ImageView callImageView;
    @BindView(R.id.message)
    ImageView messageImageView;
    @BindView(R.id.contact_us)
    TextView contactUsTextView;
    @BindView(R.id.app_name)
    TextView appNameTextView;
    @BindView(R.id.github)
    TextView github;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        ButterKnife.bind(this);
        selectedColor = fetchAccentColor();
        darkenedColor = Utils.getDarkColor(selectedColor);
        Utils.setViewsColorStateList(selectedColor, darkenedColor,
                customThemeButton,
                multiCaptureButton,
                multiPickerButton,
                callImageView,
                messageImageView,
                contactUsTextView,
                appNameTextView);

        multiCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.hasCameraHardware(SampleActivity.this))
                    initiateMultiCapture();
                else
                    Utils.showLongSnack(parentLayout, "Sorry. Your device does not have a camera.");
            }
        });

        multiPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateMultiPicker();
            }
        });

        customThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCustomTheme();
            }
        });

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToUrl();
            }
        });
    }

    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.TYPE_MULTI_CAPTURE:
            case Constants.TYPE_MULTI_PICKER:
                handleResponseIntent(intent);
                break;
        }
    }

    private int getColumnCount() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float thumbnailDpWidth = getResources().getDimension(R.dimen.thumbnail_width) / displayMetrics.density;
        return (int) (dpWidth / thumbnailDpWidth);
    }

    private void handleResponseIntent(Intent intent) {
        ArrayList<Image> imagesList = intent.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(getColumnCount(), GridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(mLayoutManager);
        GalleryImagesAdapter imageAdapter = new GalleryImagesAdapter(this, imagesList, getColumnCount(), new Params());
        recyclerView.setAdapter(imageAdapter);
    }

    private void navigateToUrl() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/vansikrishna/Multimager.git")));
    }

    private void setCustomTheme() {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(selectedColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton("OK", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        changeColor(selectedColor);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    private void changeColor(int selectedColor) {
        this.selectedColor = selectedColor;
        this.darkenedColor = Utils.getDarkColor(selectedColor);
        Utils.showShortSnack(parentLayout, "New color selected" + Integer.toHexString(selectedColor));
//        Utils.setViewsColorStateList(customThemeButton, selectedColor, darkenedColor);
//        Utils.setViewsColorStateList(multiCaptureButton, selectedColor, darkenedColor);
//        Utils.setViewsColorStateList(multiPickerButton, selectedColor, darkenedColor);

        Utils.setViewsColorStateList(selectedColor, darkenedColor,
                customThemeButton,
                multiCaptureButton,
                multiPickerButton,
                callImageView,
                messageImageView,
                contactUsTextView,
                appNameTextView);
    }

    private void initiateMultiCapture() {
        Intent intent = new Intent(this, MultiCameraActivity.class);
        Params params = new Params();
        params.setCaptureLimit(10);
        params.setToolbarColor(selectedColor);
        params.setActionButtonColor(selectedColor);
        params.setButtonTextColor(selectedColor);
        intent.putExtra(Constants.KEY_PARAMS, params);
        startActivityForResult(intent, Constants.TYPE_MULTI_CAPTURE);
    }

    private void initiateMultiPicker() {
        Intent intent = new Intent(this, GalleryActivity.class);
        Params params = new Params();
        params.setCaptureLimit(10);
        params.setPickerLimit(10);
        params.setToolbarColor(selectedColor);
        params.setActionButtonColor(selectedColor);
        params.setButtonTextColor(selectedColor);
        intent.putExtra(Constants.KEY_PARAMS, params);
        startActivityForResult(intent, Constants.TYPE_MULTI_PICKER);
    }


}

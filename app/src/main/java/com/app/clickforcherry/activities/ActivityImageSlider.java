package com.app.clickforcherry.activities;



import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.app.clickforcherry.R;
import com.app.clickforcherry.adapter.AdapterImageSlider;
import com.app.clickforcherry.callbacks.CallbackPostDetail;
import com.app.clickforcherry.config.AppConfig;
import com.app.clickforcherry.models.Images;
import com.app.clickforcherry.rests.RestAdapter;
import com.app.clickforcherry.utils.NetworkCheck;
import com.app.clickforcherry.utils.RtlViewPager;
import com.app.clickforcherry.utils.SharedPref;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityImageSlider extends AppCompatActivity {

    private Call<CallbackPostDetail> callbackCall = null;
    ImageButton lyt_close, lyt_save;
    TextView txt_number;
    ViewPager viewPager;
    RtlViewPager viewPagerRTL;
    Long nid;
    int position;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        setTheme(R.style.AppDarkTheme);

        if (AppConfig.ENABLE_RTL_MODE) {
            setContentView(R.layout.activity_image_slider_rtl);
        } else {
            setContentView(R.layout.activity_image_slider);
        }

        if (AppConfig.ENABLE_RTL_MODE) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        lyt_close = findViewById(R.id.lyt_close);
        lyt_save = findViewById(R.id.lyt_save);
        txt_number = findViewById(R.id.txt_number);

        nid = getIntent().getLongExtra("nid", 0);
        position = getIntent().getIntExtra("position", 0);
        //post = (News) getIntent().getSerializableExtra(EXTRA_OBJC);
        requestAction();

        initToolbar();

    }

    private void requestAction() {
        showFailedView(false, "");
        requestPostData();
    }

    private void requestPostData() {
        this.callbackCall = RestAdapter.createAPI().getNewsDetail(nid);
        this.callbackCall.enqueue(new Callback<CallbackPostDetail>() {
            public void onResponse(Call<CallbackPostDetail> call, Response<CallbackPostDetail> response) {
                CallbackPostDetail responseHome = response.body();
                if (responseHome == null || !responseHome.status.equals("ok")) {
                    onFailRequest();
                    return;
                }
                displayAllData(responseHome);
            }

            public void onFailure(Call<CallbackPostDetail> call, Throwable th) {
                Log.e("onFailure", th.getMessage());
                if (!call.isCanceled()) {
                    onFailRequest();
                }
            }
        });
    }

    private void onFailRequest() {
        if (NetworkCheck.isConnect(ActivityImageSlider.this)) {
            showFailedView(true, getString(R.string.msg_no_network));
        } else {
            showFailedView(true, getString(R.string.msg_offline));
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = findViewById(R.id.lyt_failed_home);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            lyt_failed.setVisibility(View.GONE);
        }
        findViewById(R.id.failed_retry).setOnClickListener(view -> requestAction());
    }

    private void displayAllData(CallbackPostDetail responseHome) {
        displayImages(responseHome.images);
    }

    private void displayImages(final List<Images> list) {
        final AdapterImageSlider adapter = new AdapterImageSlider(ActivityImageSlider.this, list);
        if (AppConfig.ENABLE_RTL_MODE) {
            viewPagerRTL = findViewById(R.id.view_pager_image_rtl);
            viewPagerRTL.setAdapter(adapter);
            viewPagerRTL.setOffscreenPageLimit(list.size());
            viewPagerRTL.setCurrentItem(position);
            viewPagerRTL.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                public void onPageSelected(final int position) {
                    super.onPageSelected(position);
                    txt_number.setText((position + 1) + " of " + list.size());

                }
            });
        } else {
            viewPager = findViewById(R.id.view_pager_image);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(list.size());
            viewPager.setCurrentItem(position);
            viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                public void onPageSelected(final int position) {
                    super.onPageSelected(position);
                    txt_number.setText((position + 1) + " of " + list.size());

                }
            });
        }

        txt_number.setText((position + 1) + " of " + list.size());

        lyt_close.setOnClickListener(view -> finish());

    }

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }





    public void onDestroy() {
        if (!(callbackCall == null || callbackCall.isCanceled())) {
            this.callbackCall.cancel();
        }
        super.onDestroy();
    }


}

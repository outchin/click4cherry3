package com.app.clickforcherry.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.clickforcherry.R;
import com.app.clickforcherry.activities.ActivityPostDetail;
import com.app.clickforcherry.activities.ActivityPostDetailOffline;
import com.app.clickforcherry.activities.MainActivity;
import com.app.clickforcherry.adapter.AdapterNews;
import com.app.clickforcherry.models.News;
import com.app.clickforcherry.utils.Constant;
import com.app.clickforcherry.utils.DbHandler;
import com.app.clickforcherry.utils.NetworkCheck;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavorite extends Fragment {

    private List<News> data = new ArrayList<News>();
    private View root_view, parent_view;
    private RecyclerView recyclerView;
    private AdapterNews mAdapter;
    private MainActivity mainActivity;
    LinearLayout lyt_root;
    DbHandler databaseHandler;

    SharedPreferences mySharedPref;
    public static final String MyPREFERENCES = "read_to_donate" ;
    public static final String TotalMoney = "total_money";

    public FragmentFavorite() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_favorite, null);
        parent_view = getActivity().findViewById(R.id.main_content);
        lyt_root = root_view.findViewById(R.id.root_layout);

        recyclerView = root_view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        loadDataFromDatabase();

        return root_view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        loadDataFromDatabase();
    }

    public void loadDataFromDatabase() {
        databaseHandler = new DbHandler(getActivity());
        data = databaseHandler.getAllData();

        //set data and list adapter
        mAdapter = new AdapterNews(getActivity(), recyclerView, data);
        recyclerView.setAdapter(mAdapter);

        if (data.size() == 0) {
            showNoItemView(true);
        } else {
            showNoItemView(false);
        }

        // on item list clicked
        mAdapter.setOnItemClickListener((v, obj, position) -> {
            if (NetworkCheck.isConnect(getActivity())) {
                Intent intent = new Intent(getActivity(), ActivityPostDetail.class);
                intent.putExtra(Constant.EXTRA_OBJC, obj);
                startActivity(intent);

                if (getActivity() != null) {
                    mySharedPref = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mySharedPref.edit();
                    int previousMoney =  mySharedPref.getInt(TotalMoney,0);
                    int newMoney = previousMoney + 5;
                    editor.putInt(TotalMoney, newMoney);
                    editor.commit();


                    ((MainActivity) getActivity()).showInterstitialAd();
                }
            } else {
                Intent intent = new Intent(getActivity(), ActivityPostDetailOffline.class);
                intent.putExtra(Constant.EXTRA_OBJC, obj);
                startActivity(intent);
            }
        });
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = root_view.findViewById(R.id.lyt_no_item_later);
        ((TextView) root_view.findViewById(R.id.no_item_message)).setText(R.string.no_favorite_found);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }
}

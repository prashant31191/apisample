package com.azapps.recycler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azapps.App;
import com.azapps.R;
import com.azapps.recycler.newsapi.ArticlesModel;
import com.azapps.recycler.newsapi.NewsChannelsResponse;
import com.azapps.utils.AdsUtils;
import com.azapps.utils.AppFlags;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ActNewsChannelsListing extends AppCompatActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.materialRefreshLayout)
    MaterialRefreshLayout materialRefreshLayout;


    @BindView(R.id.llNodata)
    LinearLayout llNodata;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.tvNodata)
    TextView tvNodata;

    DataListAdapter dataListAdapter;
    ArrayList<ArticlesModel> arrayListArticlesModel = new ArrayList<>();


    String strFrom = "", strData = "", category_id = "";
    int page = 1;
    FlurryAdInterstitial mFlurryAdInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.act_news_listing);
            ButterKnife.bind(this);
            getIntentData();
            initialization();
            asyncGetNewsList();



        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        FlurryAgent.onStartSession(this, App.FlurryApiKey);
        // fetch and prepare ad for this ad space. won’t render one yet
        mFlurryAdInterstitial = new FlurryAdInterstitial(this, AdsUtils.mAdSpaceName);
        // allow us to get callbacks for ad events
        mFlurryAdInterstitial.setListener(AdsUtils.interstitialAdListener);
        mFlurryAdInterstitial.fetchAd();
        super.onStart();
    }


    @Override
    protected void onStop() {
        FlurryAgent.onEndSession(this);
        //do NOT call mFlurryAdInterstitial.destroy() here.
        //it will destroy the object prematurely and prevent certain listener callbacks form fireing
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        mFlurryAdInterstitial.destroy();
        super.onDestroy();
    }


    private void getIntentData() {
        Bundle bundle;
        if (getIntent() != null && getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
            if (bundle.getString(AppFlags.tagFrom) != null) {
                strFrom = bundle.getString(AppFlags.tagFrom);
            }


            if (bundle.getString(AppFlags.tagData) != null) {
                strData = bundle.getString(AppFlags.tagData);
            }

            if (bundle.getString(AppFlags.tagCatId) != null) {
                category_id = bundle.getString(AppFlags.tagCatId);
            }
        }

        App.showLog("====strFrom===" + strFrom);
        App.showLog("===strData====" + strData);
        App.showLog("===category_id====" + category_id);

    }

    private void initialization() {
        try {


            materialRefreshLayout.setIsOverLay(true);
            materialRefreshLayout.setWaveShow(true);
            materialRefreshLayout.setWaveColor(0x55ffffff);
            materialRefreshLayout.setLoadMore(true);

            materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                    try {
                        arrayListArticlesModel = new ArrayList<>();
                        asyncGetNewsList();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                    try {
                        materialRefreshLayout.setLoadMore(false);
                        App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            //GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(linearLayoutManager);
            //recyclerView.setHasFixedSize(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void asyncGetNewsList() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            OkHttpClient httpClient = new OkHttpClient();
            String url = "https://newsapi.org/v2/sources?apiKey=462f5f3ede2841408e9ef575919befe5&page="+page;
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    App.showLog("error in getting response using async okhttp call");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                progressBar.setVisibility(View.GONE);
                                App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                                App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);
                            }
                        }
                    });
                }

                @Override
                public void onResponse(Call call,final Response response) throws IOException {
                    final ResponseBody responseBody = response.body();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!response.isSuccessful()) {
                                    throw new IOException("Error response " + response);
                                }

                                String result = responseBody.string();
                                App.showLog("=result==" + result);
                                progressBar.setVisibility(View.GONE);
                                if (result != null) {
                                    App.showLog("==result==" + result.toString());

                                    Gson gson = new GsonBuilder().create();
                                    NewsChannelsResponse newsHeadlinesResponse = gson.fromJson(result.toString(), NewsChannelsResponse.class);
                                    App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);
                                    if (newsHeadlinesResponse != null && newsHeadlinesResponse.arrayListArticlesModel != null) {
                                        //arrayListArticlesModel = newsHeadlinesResponse.arrayListArticlesModel;
                                        if(page == 1){
                                            arrayListArticlesModel = newsHeadlinesResponse.arrayListArticlesModel;
                                        }
                                        else
                                        {
                                            arrayListArticlesModel.addAll(newsHeadlinesResponse.arrayListArticlesModel);
                                        }
                                        page = page + 1;
                                        setStaticData();
                                    }
                                    else
                                    {
                                        materialRefreshLayout.setLoadMore(false);
                                    }
                                }

                            } catch (Exception e1) {
                                progressBar.setVisibility(View.GONE);
                                App.setStopLoadingMaterialRefreshLayout(materialRefreshLayout);
                                e1.printStackTrace();
                            }
                        }
                    });


                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setStaticData() {
        try {


            App.showLog("=======setStaticData===");

            if (arrayListArticlesModel != null && arrayListArticlesModel.size() > 0) {

                llNodata.setVisibility(View.GONE);
                App.showLog("======set adapter=DataListAdapter===");

                if (dataListAdapter == null) {
                    dataListAdapter = new DataListAdapter(ActNewsChannelsListing.this, arrayListArticlesModel);
                    recyclerView.setAdapter(dataListAdapter);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                } else {
                    dataListAdapter.notifyDataSetChanged();
                }


            } else {
                llNodata.setVisibility(View.VISIBLE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class DataListAdapter extends RecyclerView.Adapter<DataListAdapter.VersionViewHolder> {
        ArrayList<ArticlesModel> mArrListmPEArticleModel;
        Context mContext;


        public DataListAdapter(Context context, ArrayList<ArticlesModel> arrList) {
            mArrListmPEArticleModel = arrList;
            mContext = context;
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_articles_list, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
            try {
                ArticlesModel mPEArticleModel = mArrListmPEArticleModel.get(i);

                versionViewHolder.tvTitle.setText(mPEArticleModel.name);
                versionViewHolder.tvDate.setText(mPEArticleModel.country);
                versionViewHolder.tvTime.setText(mPEArticleModel.language);

                versionViewHolder.tvDetail.setText(mPEArticleModel.id + "\n "+mPEArticleModel.category);

                if (mPEArticleModel.urlToImage != null && mPEArticleModel.urlToImage.length() > 1) {
                    versionViewHolder.ivPhoto.setVisibility(View.VISIBLE);

                    Picasso.with(mContext)
                            .load(mPEArticleModel.urlToImage)
                            .placeholder(R.color.clr_divider)
                            .error(R.color.white)
                            .fit()
                            .centerCrop()
                            .into(versionViewHolder.ivPhoto);

                } else {
                    versionViewHolder.ivPhoto.setVisibility(View.GONE);
                }
                if (mPEArticleModel.name.equalsIgnoreCase("1")) {
                    versionViewHolder.ivFavourite.setSelected(true);
                } else {
                    versionViewHolder.ivFavourite.setSelected(false);
                }


                versionViewHolder.ivFavourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mArrListmPEArticleModel.get(i).name.equalsIgnoreCase("1")) {
                            mArrListmPEArticleModel.get(i).name = "0";
                            if (mArrListmPEArticleModel.get(i) != null && mArrListmPEArticleModel.get(i).name != null) {

                            }
                        } else {
                            mArrListmPEArticleModel.get(i).name = "1";
                        }

                        dataListAdapter.notifyDataSetChanged();
                    }
                });

                versionViewHolder.rlMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(ActNewsChannelsListing.this,ActNewsDetail.class);
                        intent.putExtra(AppFlags.tagArticlesModel,mArrListmPEArticleModel.get(i));
                        startActivity(intent);
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mArrListmPEArticleModel.size();
        }


        public void removeItem(int position) {


        }


        class VersionViewHolder extends RecyclerView.ViewHolder {


            TextView tvTitle, tvDate, tvTime, tvDetail;
            ImageView ivPhoto, ivFavourite;
            RelativeLayout rlMain;


            public VersionViewHolder(View itemView) {
                super(itemView);


                rlMain = (RelativeLayout) itemView.findViewById(R.id.rlMain);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvDate = (TextView) itemView.findViewById(R.id.tvDate);
                tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                tvDetail = (TextView) itemView.findViewById(R.id.tvDetail);

                ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
                ivFavourite = (ImageView) itemView.findViewById(R.id.ivFavourite);
            }

        }
    }


}

package com.ossul.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.adapters.CommentRecyclerAdapter;
import com.ossul.appconstant.AppConstants;
import com.ossul.dialog.CustomDialogFragment;
import com.ossul.dialog.DialogManager;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.listener.EndlessRecyclerOnScrollListener;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.model.response.BaseResponse;
import com.ossul.network.model.response.Comment;
import com.ossul.network.model.response.CommentList;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.Validator;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.util.ArrayList;
import java.util.Collections;


public class CommentsActivity extends BaseActivity implements View.OnClickListener, INetworkEvent, SwipeRefreshLayout.OnRefreshListener {
    public RecyclerView recyclerView;
    private String API_GET_COMMENTS = "";
    private ArrayList<Comment> listAll;
    private SwipeRefreshLayout swipeContainer;
    private CommentRecyclerAdapter adapter;
    private EndlessRecyclerOnScrollListener recyclerOnScrollListener;
    private TextView mHeaderTitleTV;
    private String TAG = CommentsActivity.class.getSimpleName();
    private DialogManager mProgressDialog;
    private String mOfferId = "";
    private TextView tvPost;
    private EditText mCommentText;
    private ImageView mCloseIV;
    private String API_POST_COMMENT = "";
    private View noComments;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        initData();

    }

    protected void initData() {
        initViews();
        initVariables();
    }

    @Override
    protected void initViews() {
        mHeaderTitleTV = (TextView) findViewById(R.id.tv_header_title);
        mCloseIV = (ImageView) findViewById(R.id.iv_close);
        recyclerView = (RecyclerView) findViewById(R.id.itemsRecyclerView);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        noComments = (View) findViewById(R.id.no_comments);

        mCommentText = (EditText) findViewById(R.id.et_comment_text);
        tvPost = (TextView) findViewById(R.id.tvPost);

        mCloseIV.setOnClickListener(this);
        tvPost.setOnClickListener(this);
        showNoComments(true);
    }

    @Override
    protected void initVariables() {
        mProgressDialog = new DialogManager(this);
        if (getIntent() != null && !Validator.isEmptyString(getIntent().getStringExtra("offer_id"))) {
            mOfferId = getIntent().getStringExtra("offer_id");
            getComments(mOfferId);
        }

        LinearLayoutManager layout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout);
        recyclerView.setHasFixedSize(true);

        swipeContainer.setOnRefreshListener(this);

        recyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layout) {
            @Override
            public void onLoadMore(int current_page) {
//                getComments(mOfferId);
            }
        };
//        recyclerView.addOnScrollListener(recyclerOnScrollListener);

    }

    void showNoComments(boolean b) {
        if (b) {
            recyclerView.setVisibility(View.GONE);
            noComments.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noComments.setVisibility(View.GONE);
        }
    }

    public void setData(ArrayList<Comment> listComments) {
        if (listComments == null || listComments.isEmpty()) {
            showNoComments(true);
            return;
        }
        Collections.reverse(listComments);
        showNoComments(false);

        if (null == listAll) {
            listAll = new ArrayList<>();
        }

        listAll.clear();
        if (adapter == null) {
            listAll = listComments;
            adapter = new CommentRecyclerAdapter(this, listAll);

            recyclerView.setAdapter(adapter);
        } else {
            listAll.addAll(listComments);
            adapter.notifyDataSetChanged();
        }
        String str = listAll.size() > 0 ? listAll.size() + " comments" : listAll.size() + " comment";
        mHeaderTitleTV.setText(str);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                onBackPressed();
                break;

            case R.id.tvPost:
                if (!Validator.isEmptyString(mCommentText.getText().toString().trim())) {
                    postComment(mCommentText.getText().toString().trim());
                    mCommentText.setText("");
                }
                break;
        }
    }

    private void postComment(String text) {
        API_POST_COMMENT = AppNetworkConstants.BASE_URL + "user/offer.php";
        NetworkService serviceCall = new NetworkService(API_POST_COMMENT, AppConstants.METHOD_POST, CommentsActivity.this);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_POST_COMMENT)
                .addFormDataPart(ParserKeys.offer_id.toString(), mOfferId)
                .addFormDataPart(ParserKeys.comment.toString(), text)
                .build();
        serviceCall.setRequestBody(requestBody);
        serviceCall.call(new NetworkModel());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_out_up, R.anim.slide_out_down);
    }

    @Override
    public void onNetworkCallInitiated(String service) {
        if (mProgressDialog != null && !swipeContainer.isRefreshing()) {
            mProgressDialog.show();
        }
    }

    @Override
    public void onNetworkCallCompleted(String service, String response) {
        Log.e(TAG, " success " + response);
        if (mProgressDialog != null)
            mProgressDialog.dismiss();

        swipeContainer.setRefreshing(false);
        if (service.equalsIgnoreCase(API_GET_COMMENTS)) {
            CommentList newsParentResponse = CommentList.fromJson(response);
            if (newsParentResponse != null && newsParentResponse.success && newsParentResponse.list != null) {
                setData(newsParentResponse.list);
            }
        } else if (service.equalsIgnoreCase(API_POST_COMMENT)) {
            BaseResponse baseResponse = BaseResponse.fromJson(response);
            if (baseResponse != null && baseResponse.success) {
                if (!Validator.isEmptyString(baseResponse.success_message))
                    CustomDialogFragment.getInstance(this, null, baseResponse.success_message, null, AppConstants.OK, null, 1, null);
                swipeContainer.setRefreshing(true);
                getComments(mOfferId);
            } else if (baseResponse != null && baseResponse.error && !Validator.isEmptyString(baseResponse.errorMessage)) {
                CustomDialogFragment.getInstance(this, null, baseResponse.errorMessage, null, AppConstants.OK, null, 1, null);
            } else {
                CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
            }
        }
    }

    private void getComments(String offerId) {
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_GET_COMMENTS = AppNetworkConstants.BASE_URL + "user/offer.php?action=get-offer-comments&offer_id=" + offerId;
            NetworkService service = new NetworkService(API_GET_COMMENTS, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        getComments(mOfferId);
    }
}

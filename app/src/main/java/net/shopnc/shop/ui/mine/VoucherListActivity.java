package net.shopnc.shop.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.shopnc.shop.BaseActivity;
import net.shopnc.shop.R;
import net.shopnc.shop.adapter.VoucherListViewAdapter;
import net.shopnc.shop.bean.VoucherList;
import net.shopnc.shop.common.Constants;
import net.shopnc.shop.common.MyExceptionHandler;
import net.shopnc.shop.common.MyShopApplication;
import net.shopnc.shop.custom.XListView;
import net.shopnc.shop.custom.XListView.IXListViewListener;
import net.shopnc.shop.http.RemoteDataHandler;
import net.shopnc.shop.http.RemoteDataHandler.Callback;
import net.shopnc.shop.http.ResponseData;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 我的代金券列表
 *
 * @author KingKong-HE
 * @Time 2015-1-26
 * @Email KingKong@QQ.COM
 */
public class VoucherListActivity extends BaseActivity implements IXListViewListener {

    private MyShopApplication myApplication;
    private Handler mXLHandler;
    private VoucherListViewAdapter adapter;
    private XListView listViewID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_list);
        MyExceptionHandler.getInstance().setContext(this);
        setCommonHeader("");
        setTabButton();

        myApplication = (MyShopApplication) getApplicationContext();


        listViewID = (XListView) findViewById(R.id.listViewID);
        adapter = new VoucherListViewAdapter(VoucherListActivity.this);
        mXLHandler = new Handler();
        listViewID.setAdapter(adapter);
        listViewID.setXListViewListener(this);
        listViewID.setPullLoadEnable(false);
        loadingListData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingListData();
    }

    /**
     * 设置头部切换按钮
     */
    private void setTabButton() {
        Button btnVoucherList = (Button) findViewById(R.id.btnVoucherList);
        Button btnVoucherPasswordAdd = (Button) findViewById(R.id.btnVoucherPasswordAdd);
        btnVoucherList.setActivated(true);
        btnVoucherPasswordAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VoucherListActivity.this, VoucherPasswordAddActivity.class));
                finish();
            }
        });
    }


    /**
     * 加载列表数据
     */
    public void loadingListData() {
        String url = Constants.URL_MEMBER_VOUCHER_LIST;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("voucher_state", "1");//(1-未使用 2-已使用 3-已过期)

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String updataTime = sdf.format(new Date(System.currentTimeMillis()));
        listViewID.setRefreshTime(updataTime);

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {

                listViewID.stopRefresh();

                if (data.getCode() == HttpStatus.SC_OK) {
                    String json = data.getJson();
                    try {
                        JSONObject obj = new JSONObject(json);
                        String objJson = obj.getString("voucher_list");
                        ArrayList<VoucherList> voucherList = VoucherList.newInstanceList(objJson);
                        adapter.setVoucherLists(voucherList);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(VoucherListActivity.this, R.string.load_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        //下拉刷新
        mXLHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingListData();
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        //上拉加载
    }

}

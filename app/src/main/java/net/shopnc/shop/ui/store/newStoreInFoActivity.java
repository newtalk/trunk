package net.shopnc.shop.ui.store;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Request;

import net.shopnc.shop.MainFragmentManager;
import net.shopnc.shop.R;
import net.shopnc.shop.bean.StoreIndex;
import net.shopnc.shop.bean.StoreIntroduce;
import net.shopnc.shop.bean.StoreVoucher;
import net.shopnc.shop.common.Constants;
import net.shopnc.shop.common.JsonFastUtil;
import net.shopnc.shop.common.LoadImage;
import net.shopnc.shop.common.LogHelper;
import net.shopnc.shop.common.MyExceptionHandler;
import net.shopnc.shop.common.MyShopApplication;
import net.shopnc.shop.common.OkHttpUtil;
import net.shopnc.shop.common.ShopHelper;
import net.shopnc.shop.custom.NCNewStoreVoucherPopupWindow;
import net.shopnc.shop.http.RemoteDataHandler;
import net.shopnc.shop.http.RemoteDataHandler.Callback;
import net.shopnc.shop.http.ResponseData;
import net.shopnc.shop.ui.home.SearchActivity;
import net.shopnc.shop.ui.mine.IMFriendsListActivity;
import net.shopnc.shop.ui.mine.LoginActivity;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 店铺详情页
 *
 * @author huting
 * @Time 2015-12-19
 * @Email
 */
public class newStoreInFoActivity extends FragmentActivity implements OnClickListener,
        StoreIndexFragment.OnFragmentInteractionListener,
        StoreAllGoodsFragment.OnFragmentInteractionListener,
        StoreNewGoodsFragment.OnFragmentInteractionListener,
        StoreActivitiesFragment.OnFragmentInteractionListener{

  FragmentManager fragmentManager = getSupportFragmentManager();
  private MyShopApplication myApplication;

  private ImageButton btnBack;//返回
  private TextView etSearchText;//搜素
  private ImageView imgClassify;//分类
  private ImageView imgMenu;//悬浮菜单

  private ImageView storeInFoPic;//店铺背景图
  private ImageView storePic;//店铺的图片

  private TextView storeNameID;//店铺名称
  private TextView textFanCount;//店铺的粉丝数目

  private Button favoritesAddID;  //收藏
  private Button favoritesDeleteID;//已经收藏

  private RelativeLayout rStoreHome;//店铺首页
  private RelativeLayout rStoreGoods;//全部商品
  private RelativeLayout rStoreNew;//商品上新
  private RelativeLayout rStoreActivity;//店铺活动

  private StoreIndexFragment storeIndexFragment;  //店铺首页
  private StoreAllGoodsFragment storeAllGoodsFragment; //店铺全部商品
  private StoreNewGoodsFragment storeNewGoodsFragment; //店铺上新
  private StoreActivitiesFragment storeActivityFragment; //店铺活动

  private TextView textIntroduce;//店铺介绍
  private TextView textGetQuan;//免费领券
  private TextView textCall;//联系客服

  private Intent intent = null;
  private String store_id; //店铺的id
  private String store_name;//记录店铺的名称

  private InputMethodManager imm;
  private NCNewStoreVoucherPopupWindow pwVoucher;

  private PopupWindow popupWindow ;
  private View mPopupWindowView;
  private LinearLayout llSearch;

  private int count = 0;
  private TimerTask task;
  private Timer delayTimer;
  private long interval = 500;
  private long firstTime = 0;
    // 点击事件结束后的事件处理
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (count == 1) {
            } else if (count > 1) {
            }
            delayTimer.cancel();
            count = 0;
            String key = myApplication.getLoginKey();
            int id = msg.arg1;
            if (id == 0) {
                AddFav();
            } else {
                DeleteFav();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_info_view_new);
        MyExceptionHandler.getInstance().setContext(this);
        myApplication = (MyShopApplication) this.getApplicationContext();
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        store_id = getIntent().getStringExtra("store_id");

        initView();
        //initData(store_id);
        initPopupWindow();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData(store_id);
        llSearch.setFocusable(true);
        llSearch.setFocusableInTouchMode(true);
        llSearch.requestFocus();
    }

    //延迟时间是连击的时间间隔有效范围
    private void delay(final int cns_id) {
        if (task != null)
            task.cancel();

        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.arg1 = cns_id;
                handler.sendMessage(message);
            }
        };
        delayTimer = new Timer();
        delayTimer.schedule(task, interval);
    }


    private void initView(){
        llSearch = (LinearLayout)this.findViewById(R.id.llSearch);
        btnBack = (ImageButton)this.findViewById(R.id.btnBack);
        etSearchText = (TextView)this.findViewById(R.id.etSearchText);
        imgClassify = (ImageView)this.findViewById(R.id.imgClassify);
        imgMenu = (ImageView)this.findViewById(R.id.imgMenu);

        storeInFoPic = (ImageView)this.findViewById(R.id.storeInFoPic);
        storePic = (ImageView)this.findViewById(R.id.storePic);
        storeNameID = (TextView)this.findViewById(R.id.storeNameID);
        textFanCount = (TextView)this.findViewById(R.id.textFanCount);

        favoritesAddID = (Button)this.findViewById(R.id.favoritesAddID);
        favoritesDeleteID = (Button)this.findViewById(R.id.favoritesDeleteID);

        textIntroduce = (TextView)this.findViewById(R.id.textIntroduce);
        textGetQuan = (TextView)this.findViewById(R.id.textGetQuan);
        textCall = (TextView)this.findViewById(R.id.textCall);

        rStoreHome = (RelativeLayout)this.findViewById(R.id.rStoreHome);
        rStoreGoods = (RelativeLayout)this.findViewById(R.id.rStoreGoods);
        rStoreNew = (RelativeLayout)this.findViewById(R.id.rStoreNew);
        rStoreActivity = (RelativeLayout)this.findViewById(R.id.rStoreActivity);

        storeIndexFragment = StoreIndexFragment.newInstance(store_id);
        storeAllGoodsFragment = StoreAllGoodsFragment.newInstance(store_id);
        storeNewGoodsFragment = StoreNewGoodsFragment.newInstance(store_id);
        storeActivityFragment = StoreActivitiesFragment.newInstance(store_id);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.llContent, storeIndexFragment);
        transaction.add(R.id.llContent, storeAllGoodsFragment);
        transaction.add(R.id.llContent, storeNewGoodsFragment);
        transaction.add(R.id.llContent, storeActivityFragment);
        transaction.commit();

        showStoreIndex();//默认显示店铺首页

        btnBack.setOnClickListener(this);
        etSearchText.setOnClickListener(this);
        imgClassify.setOnClickListener(this);
        imgMenu.setOnClickListener(this);

        favoritesAddID.setOnClickListener(this);
        favoritesDeleteID.setOnClickListener(this);

        textIntroduce.setOnClickListener(this);
        textGetQuan.setOnClickListener(this);
        textCall.setOnClickListener(this);

        rStoreHome.setOnClickListener(this);
        rStoreGoods.setOnClickListener(this);
        rStoreNew.setOnClickListener(this);
        rStoreActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      switch (view.getId()){
          case R.id.btnBack:
              this.finish();
              break;

          case R.id.etSearchText:
              intent = new Intent(newStoreInFoActivity.this, StoreSearchActivity.class);
              intent.putExtra("store_id", store_id);
              intent.putExtra("store_name", store_name);
              startActivity(intent);
              break;

          case R.id.imgClassify:
              intent = new Intent(newStoreInFoActivity.this, StoreSearchActivity.class);
              intent.putExtra("store_id", store_id);
              intent.putExtra("store_name", store_name);
              startActivity(intent);
              break;

          case R.id.imgMenu://溢出菜单
              showPopupWindow();
              break;

          case R.id.favoritesAddID:
              long secondTime = System.currentTimeMillis();
              if (secondTime - firstTime <= interval) {
                  ++count;
              } else {
                  count = 1;
              }
              delay(0);
              firstTime = secondTime;

              break;

          case R.id.favoritesDeleteID:
              long secondTime2 = System.currentTimeMillis();
              if (secondTime2 - firstTime <= interval) {
                  ++count;
              } else {
                  count = 1;
              }
              delay(1);
              firstTime = secondTime2;
              break;

          case R.id.rStoreHome:
              showStoreIndex();
              break;

          case R.id.rStoreGoods:
              showStoreAllGoods();
              break;

          case R.id.rStoreNew:
              showStoreNewGoods();
              break;

          case R.id.rStoreActivity:
              showStoreActivity();
              break;

          case R.id.textIntroduce:
              intent = new Intent(this, StoreIntroduceActivity.class);
              intent.putExtra("store_id",store_id);
              startActivity(intent);
              break;

          case R.id.textGetQuan:
              getStoreVoucher();
              break;

          case R.id.textCall:
              if (ShopHelper.isLogin(this,myApplication.getLoginKey())){
                  getMember();
              }else {
                  startActivity(new Intent(this, LoginActivity.class));
              }
           break;

          //菜单点击事件
          case R.id.textview_home:
             //ShopHelper.showMessage(this,"首页");
              intent = new Intent(newStoreInFoActivity.this, MainFragmentManager.class);
              myApplication.sendBroadcast(new Intent(Constants.SHOW_HOME_URL));
              startActivity(intent);
              popupWindow.dismiss();
              break;

          case R.id.textview_search:
              //ShopHelper.showMessage(this,"搜索");
              startActivity(new Intent(newStoreInFoActivity.this, SearchActivity.class));
              popupWindow.dismiss();
              break;

          case R.id.textview_cart:
              //ShopHelper.showMessage(this,"购物车");
              intent = new Intent(newStoreInFoActivity.this, MainFragmentManager.class);
              myApplication.sendBroadcast(new Intent(Constants.SHOW_CART_URL));
              startActivity(intent);
              popupWindow.dismiss();
              break;

          case R.id.textview_msg:
              //ShopHelper.showMessage(this,"消息");
              if (ShopHelper.isLogin(newStoreInFoActivity.this, myApplication.getLoginKey())) {
                  startActivity(new Intent(newStoreInFoActivity.this, IMFriendsListActivity.class));
              }else{
                  startActivity(new Intent(newStoreInFoActivity.this,LoginActivity.class));
              }
              popupWindow.dismiss();
              break;
      }
    }

    /**
     * 联系客服
     */
    private void getMember(){
        OkHttpUtil.postAsyn(this, Constants.URL_STORE_INTRODUCE, new OkHttpUtil.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                ResponseData data = JsonFastUtil.fromJsonFast(response, ResponseData.class);
                if (data.getCode() == HttpStatus.SC_OK) {
                    String datas = data.getDatas();
                    if (!datas.equals("") && !datas.equals("{}") && datas != null) {
                        try {
                            JSONObject obj = new JSONObject(datas);
                            String store_info = obj.getString("store_info");
                            StoreIntroduce storeIntroduce = JsonFastUtil.fromJsonFast(store_info, StoreIntroduce.class);
                            ShopHelper.showIm(newStoreInFoActivity.this, myApplication, storeIntroduce.getMember_id(), storeIntroduce.getMember_name());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new OkHttpUtil.Param[]{
                new OkHttpUtil.Param("store_id", store_id),
        });
    }


    /**
     * 初始化店铺的信息
     */
    private void initData(String store_id){
        String url = Constants.URL_STORE_INFO + "&store_id=" + store_id + "&key=" + myApplication.getLoginKey();
        RemoteDataHandler.asyncDataStringGet(url, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        String store_info = obj.getString("store_info");
                        StoreIndex storeInFo = StoreIndex.newInstanceList(store_info);

                        if (storeInFo != null) {
                            //设置店铺名
                            store_name = storeInFo.getStore_name() == null ? "" : storeInFo.getStore_name();
                            storeNameID.setText(store_name);

                            //设置店铺的粉丝数目
                            textFanCount.setText(storeInFo.getStore_collect());

                            //加载店铺的背景图
                            LoadImage.loadImg(newStoreInFoActivity.this, storeInFoPic, storeInFo.getMb_title_img());
                            //加载店铺图片
                            LoadImage.loadImg(newStoreInFoActivity.this, storePic, storeInFo.getStore_avatar());

                            //显示店铺是否收藏
                            if (storeInFo.getIs_favorate().equals("true")) {
                                favoritesAddID.setVisibility(View.GONE);
                                favoritesDeleteID.setVisibility(View.VISIBLE);
                            } else {
                                favoritesAddID.setVisibility(View.VISIBLE);
                                favoritesDeleteID.setVisibility(View.GONE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject obj = new JSONObject(json);
                        String error = obj.getString("error");
                        if (error != null) {
                            Toast.makeText(newStoreInFoActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 获取店铺的代金券(现在是可以获取免费代金券)
     */
    private void getStoreVoucher(){
        final ArrayList<StoreVoucher> storeVouchers = new ArrayList<StoreVoucher>();
        OkHttpUtil.postAsyn(this, Constants.URL_STORE_VOUCHER, new OkHttpUtil.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                ResponseData data = JsonFastUtil.fromJsonFast(response, ResponseData.class);
                if (data.getCode() == HttpStatus.SC_OK) {
                    String datas = data.getDatas();
                    try {
                        JSONObject obj = new JSONObject(datas);
                        String voucher_list = obj.getString("voucher_list");
                        JSONArray arr = new JSONArray(voucher_list);

                        for (int i = 0; i < arr.length(); i++) {
                            StoreVoucher storeVoucher = JsonFastUtil.fromJsonFast(arr.get(i).toString(), StoreVoucher.class);
                            LogHelper.d("huting--voucher:", storeVoucher.toString());
                            storeVouchers.add(storeVoucher);
                        }

                        initStoreVoucher(storeVouchers);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new OkHttpUtil.Param[]{
                new OkHttpUtil.Param("store_id", store_id),
                new OkHttpUtil.Param("gettype", "free"),
        });
    }

    /**
     * 初始化店铺代金券
     *
     * @param storeVoucher
     */
    private void initStoreVoucher(ArrayList<StoreVoucher> storeVoucher) {
        pwVoucher = new NCNewStoreVoucherPopupWindow(this);
        if (storeVoucher.size() > 0) {
          pwVoucher.setStoreName("");
          pwVoucher.setVoucherList(storeVoucher);
        }
        pwVoucher.showPopupWindow();
    }

    /**
     * 添加店铺收藏
     */
    public void AddFav() {
        String url = Constants.URL_STORE_ADD_FAVORITES;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("store_id", store_id);
        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    if (json.equals("1")) {
                        Toast.makeText(newStoreInFoActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                        initData(store_id);
                    }
                } else {
                    try {
                        JSONObject obj2 = new JSONObject(json);
                        String error = obj2.getString("error");
                        if (error != null) {
                            Toast.makeText(newStoreInFoActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 删除店铺收藏
     */
    public void DeleteFav() {
        String url = Constants.URL_STORE_DELETE;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("store_id", store_id);

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    if (json.equals("1")) {
                        Toast.makeText(newStoreInFoActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
                        initData(store_id);
                    }
                } else {
                    try {
                        JSONObject obj2 = new JSONObject(json);
                        String error = obj2.getString("error");
                        if (error != null) {
                            Toast.makeText(newStoreInFoActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 设置切换按钮状态
     */
    private void setTabButtonState(RelativeLayout rel) {
        rStoreHome.setActivated(false);
        rStoreGoods.setActivated(false);
        rStoreNew.setActivated(false);
        rStoreActivity.setActivated(false);
        rel.setActivated(true);
    }


    /**
     * 显示店铺首页Fragement
     */
    public void showStoreIndex() {
        setTabButtonState(rStoreHome);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(storeIndexFragment);
        transaction.hide(storeAllGoodsFragment);
        transaction.hide(storeNewGoodsFragment);
        transaction.hide(storeActivityFragment);
        transaction.commit();
    }

    /**
     * 显示店铺全部商品
     */
    public void showStoreAllGoods() {
        setTabButtonState(rStoreGoods);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(storeAllGoodsFragment);
        transaction.hide(storeIndexFragment);
        transaction.hide(storeNewGoodsFragment);
        transaction.hide(storeActivityFragment);
        transaction.commit();
    }

    /**
     * 显示店铺商品上新
     */
    public void showStoreNewGoods() {
        setTabButtonState(rStoreNew);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(storeNewGoodsFragment);
        transaction.hide(storeIndexFragment);
        transaction.hide(storeAllGoodsFragment);
        transaction.hide(storeActivityFragment);
        transaction.commit();
    }

    /**
     * 显示店铺活动
     */
    public void showStoreActivity() {
        setTabButtonState(rStoreActivity);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(storeActivityFragment);
        transaction.hide(storeIndexFragment);
        transaction.hide(storeNewGoodsFragment);
        transaction.hide(storeAllGoodsFragment);
        transaction.commit();
    }

    /**显示popupwindow*/
    private void showPopupWindow(){
        if(!popupWindow.isShowing()){
            popupWindow.showAsDropDown(imgMenu, imgMenu.getLayoutParams().width/2, 0);
        }else{
            popupWindow.dismiss();
        }
    }

    /**
     * 初始化popupwindow
     */
    private void initPopupWindow(){
        initPopupWindowView();
        popupWindow = new PopupWindow(mPopupWindowView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.nc_icon_setting));
        popupWindow.update();
        popupWindow.setAnimationStyle(R.anim.popup_window_enter);
        popupWindow.setAnimationStyle(R.anim.popup_window_exit);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    /**
     * 初始化popupwindowView
     */
    private void initPopupWindowView(){
        mPopupWindowView = LayoutInflater.from(this).inflate(R.layout.menu_store, null);
        TextView textview_home = (TextView) mPopupWindowView.findViewById(R.id.textview_home);
        textview_home.setOnClickListener(this);

        TextView textview_search = (TextView) mPopupWindowView.findViewById(R.id.textview_search);
        textview_search.setOnClickListener(this);

        TextView textview_cart = (TextView) mPopupWindowView.findViewById(R.id.textview_cart);
        textview_cart.setOnClickListener(this);

        TextView textview_msg = (TextView) mPopupWindowView.findViewById(R.id.textview_msg);
        textview_msg.setOnClickListener(this);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}
}

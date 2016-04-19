package net.shopnc.shop.ui.mine;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.shopnc.shop.R;
import net.shopnc.shop.bean.Mine;
import net.shopnc.shop.common.AnimateFirstDisplayListener;
import net.shopnc.shop.common.Constants;
import net.shopnc.shop.common.MyExceptionHandler;
import net.shopnc.shop.common.MyShopApplication;
import net.shopnc.shop.common.ShopHelper;
import net.shopnc.shop.common.SystemHelper;
import net.shopnc.shop.http.RemoteDataHandler;
import net.shopnc.shop.http.ResponseData;
import net.shopnc.shop.ui.type.AddressListActivity;
import net.shopnc.shop.ui.type.GoodsBrowseActivity;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 我的
 *
 * @author dqw
 * @Time 2015-7-6
 */
public class MineFragment extends Fragment {

    private MyShopApplication myApplication;

    private LinearLayout llLogin;
    private LinearLayout llMemberInfo;
    private ImageView ivMemberAvatar;
    private TextView tvMemberName;
    private ImageView ivFavGoods;
    private TextView tvFavGoodsCount;
    private ImageView ivFavStore;
    private TextView tvFavStoreCount;

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = SystemHelper.getRoundedBitmapDisplayImageOptions(100);
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewLayout = inflater.inflate(R.layout.main_mine_view, container, false);
        MyExceptionHandler.getInstance().setContext(getActivity());
        myApplication = (MyShopApplication) getActivity().getApplicationContext();

        initSettingButton(viewLayout);
        initMemberButton(viewLayout);
        initOrderButton(viewLayout);
        initAssetButton(viewLayout);

        return viewLayout;
    }

    /**
     * 初始化设置相关按钮
     *
     * @param viewLayout
     */
    private void initSettingButton(View viewLayout) {
        //设置
        Button btnSetting = (Button) viewLayout.findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), SettingActivity.class));
                }
            }
        });

        //设置2
        RelativeLayout rlSetting = (RelativeLayout) viewLayout.findViewById(R.id.rlSetting);
        rlSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), SettingActivity.class));
                }
            }
        });

        //IM
        Button btnIm = (Button) viewLayout.findViewById(R.id.btnIm);
        btnIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), IMFriendsListActivity.class));
                }
            }
        });

        //登录
        llLogin = (LinearLayout) viewLayout.findViewById(R.id.llLogin);
        llLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        llMemberInfo = (LinearLayout) viewLayout.findViewById(R.id.llMemberInfo);
        ivMemberAvatar = (ImageView) viewLayout.findViewById(R.id.ivMemberAvatar);
        tvMemberName = (TextView) viewLayout.findViewById(R.id.tvMemberName);
    }

    /**
     * 初始化用户信息相关按钮
     *
     * @param viewLayout
     */
    private void initMemberButton(View viewLayout) {
        //商品收藏
        LinearLayout llFavGoods = (LinearLayout) viewLayout.findViewById(R.id.llFavGoods);
        llFavGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), FavGoodsListActivity.class));
                }
            }
        });
        ivFavGoods = (ImageView) viewLayout.findViewById(R.id.ivFavGoods);
        tvFavGoodsCount = (TextView) viewLayout.findViewById(R.id.tvFavGoodsCount);

        //收藏店铺
        LinearLayout llFavStore = (LinearLayout) viewLayout.findViewById(R.id.llFavStore);
        llFavStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), FavStoreListActivity.class));
                }
            }
        });
        ivFavStore = (ImageView) viewLayout.findViewById(R.id.ivFavStore);
        tvFavStoreCount = (TextView) viewLayout.findViewById(R.id.tvFavStoreCount);

        //我的足迹
        LinearLayout llZuji = (LinearLayout) viewLayout.findViewById(R.id.llZuji);
        llZuji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), GoodsBrowseActivity.class));
                }
            }
        });

        //收货地址
        RelativeLayout rlAddress = (RelativeLayout) viewLayout.findViewById(R.id.rlAddress);
        rlAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    Intent intent = new Intent(getActivity(), AddressListActivity.class);
                    intent.putExtra("addressFlag", "0");
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 初始化订单相关按钮
     *
     * @param viewLayout
     */
    private void initOrderButton(View viewLayout) {
        //全部订单
        Button btnOrderAll = (Button) viewLayout.findViewById(R.id.btnOrderAll);
        btnOrderAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrderList("");
            }
        });
        //待付款订单
        LinearLayout llOrderNew = (LinearLayout) viewLayout.findViewById(R.id.llOrderNew);
        setOrderButtonEvent(llOrderNew, "state_new");
        //待收货
        LinearLayout llOrderSend = (LinearLayout) viewLayout.findViewById(R.id.llOrderSend);
        setOrderButtonEvent(llOrderSend, "state_send");
        //待自提订单
        LinearLayout llOrderNotakes = (LinearLayout) viewLayout.findViewById(R.id.llOrderNotakes);
        setOrderButtonEvent(llOrderNotakes, "state_notakes");
        //待评价订单
        LinearLayout llOrderNoeval = (LinearLayout) viewLayout.findViewById(R.id.llOrderNoeval);
        setOrderButtonEvent(llOrderNoeval, "state_noeval");
        //退款退货
        LinearLayout llRefund = (LinearLayout) viewLayout.findViewById(R.id.llRefund);
        llRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),OrderExchangeListActivity.class));
            }
        });
    }

    /**
     * 初始化财产相关按钮
     *
     * @param viewLayout
     */
    private void initAssetButton(View viewLayout) {
        //全部财产
        Button btnMyAsset = (Button) viewLayout.findViewById(R.id.btnMyAsset);
        btnMyAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), MyAssetActivity.class));
                }
            }
        });

        //预存款
        LinearLayout llPredeposit = (LinearLayout) viewLayout.findViewById(R.id.llPredeposit);
        llPredeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), PredepositActivity.class));
                }
            }
        });

        //充值卡
        LinearLayout llRechargeCard = (LinearLayout) viewLayout.findViewById(R.id.llRechargeCard);
        llRechargeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), RechargeCardLogActivity.class));
                }
            }
        });

        //代金券
        LinearLayout llVoucherList = (LinearLayout) viewLayout.findViewById(R.id.llVoucherList);
        llVoucherList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), VoucherListActivity.class));
                }
            }
        });

        //红包
        LinearLayout llRedpacket = (LinearLayout) viewLayout.findViewById(R.id.llRedpacketList);
        llRedpacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), RedpacketListActivity.class));
                }
            }
        });

        //积分
        LinearLayout llPointLog = (LinearLayout) viewLayout.findViewById(R.id.llPointLog);
        llPointLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), PointLogActivity.class));
                }
            }
        });
    }

    /**
     * 设置订单按钮事件
     */
    private void setOrderButtonEvent(LinearLayout btn, final String stateType) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrderList(stateType);
            }
        });
    }

    /**
     * 显示订单列表
     */
    private void showOrderList(String stateType) {
        if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
            Intent intent = new Intent(getActivity(), OrderListActivity.class);
            intent.putExtra("state_type", stateType);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setLoginInfo();
    }

    @Override
    public void onStart() {
        super.onStart();
        registerBoradcastReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.LOGIN_SUCCESS_URL)) {
                loadMemberInfo();
            }
        }
    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.LOGIN_SUCCESS_URL);
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);  //注册广播
    }

    /**
     * 检测是否登录
     */
    public void setLoginInfo() {
        String loginKey = myApplication.getLoginKey();
        if (loginKey != null && !loginKey.equals("")) {
            llMemberInfo.setVisibility(View.VISIBLE);
            llLogin.setVisibility(View.GONE);
            ivFavGoods.setVisibility(View.GONE);
            ivFavStore.setVisibility(View.GONE);
            tvFavGoodsCount.setVisibility(View.VISIBLE);
            tvFavStoreCount.setVisibility(View.VISIBLE);
            loadMemberInfo();
        } else {
            llMemberInfo.setVisibility(View.GONE);
            llLogin.setVisibility(View.VISIBLE);
            ivFavGoods.setVisibility(View.VISIBLE);
            ivFavStore.setVisibility(View.VISIBLE);
            tvFavGoodsCount.setVisibility(View.GONE);
            tvFavStoreCount.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化加载我的信息
     */
    public void loadMemberInfo() {
        String url = Constants.URL_MYSTOIRE;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {

                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        String objJson = obj.getString("member_info");
                        Mine bean = Mine.newInstanceList(objJson);

                        if (bean != null) {
                            tvMemberName.setText(bean.getMemberName() == null ? "" : bean.getMemberName());
                            imageLoader.displayImage(bean.getMemberAvatar(), ivMemberAvatar, options, animateFirstListener);
                            tvFavGoodsCount.setText(bean.getFavGoods() == null ? "0" : bean.getFavGoods());
                            tvFavStoreCount.setText(bean.getFavStore() == null ? "0" : bean.getFavStore());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(getActivity(), json);
                }
            }
        });
    }

//                                intent = new Intent(getActivity(), VoucherListActivity.class);
}

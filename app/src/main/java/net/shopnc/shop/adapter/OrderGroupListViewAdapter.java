package net.shopnc.shop.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.pay.PayDemoActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.shopnc.shop.R;
import net.shopnc.shop.bean.OrderGoodsList;
import net.shopnc.shop.bean.OrderGroupList;
import net.shopnc.shop.bean.OrderList;
import net.shopnc.shop.common.AnimateFirstDisplayListener;
import net.shopnc.shop.common.Constants;
import net.shopnc.shop.common.MyShopApplication;
import net.shopnc.shop.common.SystemHelper;
import net.shopnc.shop.http.RemoteDataHandler;
import net.shopnc.shop.http.RemoteDataHandler.Callback;
import net.shopnc.shop.http.ResponseData;
import net.shopnc.shop.ui.mine.OrderDeliverDetailsActivity;
import net.shopnc.shop.ui.mine.OrderExchangeActivity;
import net.shopnc.shop.ui.mine.PayMentWebActivity;
import net.shopnc.shop.ui.type.EvaluateActivity;
import net.shopnc.shop.ui.mine.OrderDetailsActivity;
import net.shopnc.shop.ui.type.EvaluateAddActivity;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 我的订单列表适配器
 *
 * @author KingKong·HE
 * @Time 2014-1-6 下午12:06:09
 * @E-mail hjgang@bizpoer.com
 */
public class OrderGroupListViewAdapter extends BaseAdapter {
    private Context context;

    private LayoutInflater inflater;

    private ArrayList<OrderGroupList> orderLists;

    private MyShopApplication myApplication;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options = SystemHelper.getDisplayImageOptions();

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private AlertDialog menuDialog;// menu菜单Dialog

    private GridView menuGrid;

    private View menuView;

    private OrderGroupList groupList2FU;

    public OrderGroupListViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        myApplication = (MyShopApplication) context.getApplicationContext();
        // 创建AlertDialog
        menuView = View.inflate(context, R.layout.gridview_menu, null);
        menuDialog = new AlertDialog.Builder(context).create();
        menuDialog.setView(menuView);
        menuGrid = (GridView) menuView.findViewById(R.id.gridview);
    }

    @Override
    public int getCount() {
        return orderLists == null ? 0 : orderLists.size();
    }

    @Override
    public Object getItem(int position) {
        return orderLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<OrderGroupList> getOrderLists() {
        return orderLists;
    }

    public void setOrderLists(ArrayList<OrderGroupList> orderLists) {
        this.orderLists = orderLists;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.listivew_order_item, null);
            holder = new ViewHolder();
            holder.linearLayoutFLag = (LinearLayout) convertView.findViewById(R.id.linearLayoutFLag);
            holder.buttonFuKuan = (Button) convertView.findViewById(R.id.buttonFuKuan);
            holder.addViewID = (LinearLayout) convertView.findViewById(R.id.addViewID);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final OrderGroupList bean = orderLists.get(position);

        if (!bean.getPay_amount().equals("")
                && !bean.getPay_amount().equals("null")
                && !bean.getPay_amount().equals("0")
                && bean.getPay_amount() != null) {
            holder.linearLayoutFLag.setVisibility(View.VISIBLE);
        } else {
            holder.linearLayoutFLag.setVisibility(View.GONE);
        }

        if (!bean.getPay_amount().equals("0") && !bean.getPay_amount().equals("null") && bean.getPay_amount() != null) {
            String price = new DecimalFormat("#0.00").format(Double.parseDouble((bean.getPay_amount() == null ? "0.00" : bean.getPay_amount()) == "" ? "0.00" : bean.getPay_amount()));
            holder.buttonFuKuan.setText("订单支付(￥ " + price + ")");
        }

        holder.buttonFuKuan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(context,PayMentWebAcivity.class);
                // intent.putExtra("pay_sn", bean.getPay_sn());
                // context.startActivity(intent);;
                groupList2FU = orderLists.get(position);
                menuDialog.show();
                loadingPaymentListData();
            }
        });

        ArrayList<OrderList> orderLists = OrderList.newInstanceList(bean.getOrder_list());

        holder.addViewID.removeAllViews();

        for (int i = 0; i < orderLists.size(); i++) {
            OrderList orderList = orderLists.get(i);
            View orderListView = inflater.inflate(R.layout.listivew_order2_item, null);

            initUIOrderList(orderListView, orderList);

            holder.addViewID.addView(orderListView);
        }

        menuGrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                menuDialog.dismiss();
                HashMap<String, Object> map = (HashMap<String, Object>) arg0
                        .getItemAtPosition(arg2);
                switch (Integer.parseInt(map.get("itemImage").toString())) {
                    case R.drawable.sns_weixin_icon:// "微信"

                        loadingWXPaymentData(groupList2FU.getPay_sn());

                        break;
                    case R.drawable.zhifubao_appicon:// "支付宝"
                        Intent intent = new Intent(context,
                                PayMentWebActivity.class);
                        intent.putExtra("pay_sn", groupList2FU.getPay_sn());
                        context.startActivity(intent);
                        break;

                    //TODO Modify
                    case R.drawable.pay:// "支付宝原生支付"
                        loadingAlipayNativePaymentData(groupList2FU.getPay_sn());
                        break;
            }
            }
        });

        return convertView;
    }

    /**
     * 生成界面
     */
    public void initUIOrderList(View view, final OrderList orderList) {

        TextView textOrderStoreName = (TextView) view.findViewById(R.id.textOrderStoreName);
        TextView textOrderAllPrice = (TextView) view.findViewById(R.id.textOrderAllPrice);
        TextView textOrderShippingFee = (TextView) view.findViewById(R.id.textOrderShippingFee);
        final Button textOrderOperation = (Button) view.findViewById(R.id.textOrderOperation);
        final Button buttonQueRen=(Button)view.findViewById(R.id.buttonQueRen);
        TextView textOrderSuccess = (TextView) view.findViewById(R.id.textOrderSuccess);
        LinearLayout addViewID = (LinearLayout) view.findViewById(R.id.addViewID);
        TextView textOrderGoodsNum=(TextView)view.findViewById(R.id.textOrderGoodsNum);
        TextView textOrderDel=(TextView)view.findViewById(R.id.textOrderDel);
        TextView textTui=(TextView)view.findViewById(R.id.textTui);

        textOrderStoreName.setText(orderList.getStore_name());
        textOrderAllPrice.setText("￥" + orderList.getOrder_amount());
        textOrderShippingFee.setText("(含运费￥" + orderList.getShipping_fee()+")");
        ArrayList<OrderGoodsList> goodsDatas = OrderGoodsList.newInstanceList(orderList.getExtend_order_goods());

        textOrderGoodsNum.setText("共"+goodsDatas.size()+"件商品，合计");


        if (orderList.getIf_cancel().equals("true")) {
            textOrderOperation.setVisibility(View.VISIBLE);
            textOrderOperation.setText("取消订单");
        }
        if (orderList.getIf_receive().equals("true")) {
            buttonQueRen.setVisibility(View.VISIBLE);
            buttonQueRen.setText("确认收货");
        }
        if (orderList.getIf_lock().equals("true")) {
            textTui.setVisibility(View.VISIBLE);
        }
        if(orderList.getIf_evaluation().equals("true")){
            buttonQueRen.setVisibility(View.VISIBLE);
            buttonQueRen.setText("评价");
        }
        if(orderList.getIf_evaluation_again().equals("true")){
            buttonQueRen.setVisibility(View.VISIBLE);
            buttonQueRen.setText("追加评价");
        }
        if(orderList.getIf_refund_cancel().equals("true")){
            textOrderOperation.setVisibility(View.VISIBLE);
            textOrderOperation.setText("退款");
        }
        if (orderList.getIf_deliver().equals("true")){
            textOrderOperation.setVisibility(View.VISIBLE);
            textOrderOperation.setText("查看物流");
        }


//        if (orderList.getIf_deliver().equals("true")) {
//            textOrderOperation2.setText(Html.fromHtml("<a href='#'>查看物流</a>"));
//            textOrderOperation2.setVisibility(View.VISIBLE);
//        } else {
//            textOrderOperation2.setVisibility(View.GONE);
//        }

        if (orderList.getState_desc() != null
                && !orderList.getState_desc().equals("")) {
            textOrderSuccess.setVisibility(View.VISIBLE);
            textOrderSuccess.setText(orderList.getState_desc());
            if(orderList.getState_desc().equals("已取消")){
                textOrderDel.setVisibility(View.VISIBLE);
                textOrderDel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadingSaveOrderData(Constants.URL_ORDER_DEL, orderList.getOrder_id());
                    }
                });
            }
        } else {
            textOrderSuccess.setVisibility(View.GONE);
        }

        buttonQueRen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=buttonQueRen.getText().toString();
                if(s.equals("确认收货")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("操作提示")
                            .setMessage("是否确认操作")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                            .setPositiveButton("确认",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            loadingSaveOrderData(Constants.URL_ORDER_RECEIVE, orderList.getOrder_id());
                                        }
                                    }).create().show();
                }else if (s.equals("评价")){
                    Intent i = new Intent(context,EvaluateActivity.class);
                    i.putExtra("order_id", orderList.getOrder_id());
                    context.startActivity(i);
                }else if (s.equals("追加评价")){
                    Intent i = new Intent(context,EvaluateAddActivity.class);
                    i.putExtra("order_id", orderList.getOrder_id());
                    context.startActivity(i);
                }
            }
        });

        textOrderOperation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               final String key = textOrderOperation.getText().toString();
                if(key.equals("查看物流")){
                    Intent intent=new Intent(context,OrderDeliverDetailsActivity.class);
                    intent.putExtra("order_id",orderList.getOrder_id());
                    context.startActivity(intent);
                    return;
                }

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("操作提示")
                            .setMessage("是否确认操作")
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        }
                                    })
                            .setPositiveButton("确认",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int whichButton) {
                                            if (key.equals("取消订单")) {
                                                loadingSaveOrderData(Constants.URL_ORDER_CANCEL, orderList.getOrder_id());
                                            }
                                            if(key.equals("退款")){
                                                Intent intent=new Intent(context,OrderExchangeActivity.class);
                                                intent.putExtra("order_id",orderList.getOrder_id());
                                                context.startActivity(intent);
                                            }
                                        }
                                    }).create().show();

            }
        });
//        textOrderOperation2.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, OrderDeliverDetailsActivity.class);
//                intent.putExtra("order_id", orderList.getOrder_id());
//                context.startActivity(intent);
//            }
//        });

        LinearLayout llGift=null;
        LinearLayout llGiftList=null;
        TextView imgZeng=null;
        for (int j = 0; j < goodsDatas.size(); j++) {
            final OrderGoodsList ordergoodsList = goodsDatas.get(j);
            View orderGoodsListView = inflater.inflate(
                    R.layout.listivew_order_goods_item, null);
            addViewID.addView(orderGoodsListView);

            ImageView imageGoodsPic = (ImageView) orderGoodsListView
                    .findViewById(R.id.imageGoodsPic);
            TextView textGoodsName = (TextView) orderGoodsListView
                    .findViewById(R.id.textGoodsName);
            TextView textGoodsPrice = (TextView) orderGoodsListView
                    .findViewById(R.id.textGoodsPrice);
            TextView textGoodsNUM = (TextView) orderGoodsListView
                    .findViewById(R.id.textGoodsNUM);
            imgZeng=(TextView)orderGoodsListView.findViewById(R.id.imgZeng);;
            TextView textGoodsSPec=(TextView)orderGoodsListView.findViewById(R.id.textGoodsSPec);
            llGift = (LinearLayout) orderGoodsListView.findViewById(R.id.llGift);
            llGiftList = (LinearLayout) orderGoodsListView.findViewById(R.id.llGiftList);

            textGoodsName.setText(ordergoodsList.getGoods_name());
            textGoodsPrice.setText("￥" + ordergoodsList.getGoods_price());
            textGoodsNUM.setText("×"+ordergoodsList.getGoods_num());
            textGoodsName.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(context, OrderDetailsActivity.class);
                    i.putExtra("order_id",orderList.getOrder_id());
                    context.startActivity(i);
                }
            });
            if(ordergoodsList.getGoods_spec().equals("null")||ordergoodsList.getGoods_spec().equals("")){
                textGoodsSPec.setVisibility(View.GONE);
            }else{
                textGoodsSPec.setText(ordergoodsList.getGoods_spec());
            }


            imageLoader.displayImage(ordergoodsList.getGoods_image_url(),
                    imageGoodsPic, options, animateFirstListener);

        }


        //赠品
        String giftListString = orderList.getZengpin_list();
        if (giftListString.equals("") || giftListString.equals("[]")) {
            llGift.setVisibility(View.GONE);
        } else {
            try{
                imgZeng.setVisibility(View.VISIBLE);
                JSONArray giftArray = new JSONArray(giftListString);
                for (int j = 0; j < giftArray.length(); j++) {
                    View giftView = inflater.inflate(R.layout.cart_list_gift_item, null);
                    TextView tvGiftInfo = (TextView) giftView.findViewById(R.id.tvGiftInfo);
                    JSONObject giftObj = (JSONObject) giftArray.get(j);
                    tvGiftInfo.setText(giftObj.optString("goods_name") + "x" + giftObj.optString("goods_num"));
                    llGiftList.addView(giftView);
            }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        // goodsListView.setOnItemClickListener(new OnItemClickListener() {
        // @Override
        // public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
        // long arg3) {
        // OrderGoodsList bean =(OrderGoodsList)
        // goodsListView.getItemAtPosition(arg2);
        // if(bean != null){
        // Intent intent =new Intent(context,GoodsDetailsActivity.class);
        // intent.putExtra("goods_id", bean.getGoods_id());
        // context.startActivity(intent);
        // }
        // }
        // });
    }

    /**
     * 获取可用支付方式
     */
    public void loadingPaymentListData() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        RemoteDataHandler.asyncLoginPostDataString(
                Constants.URL_ORDER_PAYMENT_LIST, params, myApplication,
                new Callback() {
                    @Override
                    public void dataLoaded(ResponseData data) {
                        String json = data.getJson();
                        if (data.getCode() == HttpStatus.SC_OK) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                String JosnObj = jsonObject
                                        .getString("payment_list");
                                JSONArray arr = new JSONArray(JosnObj);
                                Log.d("huting====pay", arr.toString());

                                int size = null == arr ? 0 : arr.length();
                                ArrayList<HashMap<String, Object>> hashMaps = new ArrayList<HashMap<String, Object>>();
                                for (int i = 0; i < size; i++) {
                                    String Values = arr.getString(i);
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    if (Values.equals("wxpay")) {
                                        map.put("itemImage",
                                                R.drawable.sns_weixin_icon);
                                        map.put("itemText", "微信支付");
                                    } else if (Values.equals("alipay")) {
                                        map.put("itemImage",
                                                R.drawable.zhifubao_appicon);
                                        map.put("itemText", "支付宝");
                                    } else if (Values.equals("alipay_native")) {//TODO Modify 支付宝原生支付
                                        map.put("itemImage",
                                                R.drawable.pay);
                                        map.put("itemText", "原生支付");
                                    }
                                    if(!map.isEmpty()){
                                        hashMaps.add(map);
                                    }

                                }
                                SimpleAdapter simperAdapter = new SimpleAdapter(
                                        context,
                                        hashMaps,
                                        R.layout.item_menu,
                                        new String[]{"itemImage", "itemText"},
                                        new int[]{R.id.item_image,
                                                R.id.item_text});
                                menuGrid.setAdapter(simperAdapter);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        } else {
                            try {
                                JSONObject obj2 = new JSONObject(json);
                                String error = obj2.getString("error");
                                if (error != null) {
                                    Toast.makeText(context, error,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 获取微信参数
     *
     * @param pay_sn 支付编号
     */
    public void loadingWXPaymentData(String pay_sn) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("pay_sn", pay_sn);
        Log.d("dqw", Constants.URL_MEMBER_WX_PAYMENT);
        Log.d("dqw", myApplication.getLoginKey());
        Log.d("dqw", pay_sn);

        RemoteDataHandler.asyncLoginPostDataString(
                Constants.URL_MEMBER_WX_PAYMENT, params, myApplication,
                new Callback() {
                    @Override
                    public void dataLoaded(ResponseData data) {
                        String json = data.getJson();
                        if (data.getCode() == HttpStatus.SC_OK) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                String appid = jsonObject.getString("appid");// 微信开放平台appid
                                String noncestr = jsonObject
                                        .getString("noncestr");// 随机字符串
                                String packageStr = jsonObject
                                        .getString("package");// 支付内容
                                String partnerid = jsonObject
                                        .getString("partnerid");// 财付通id
                                String prepayid = jsonObject
                                        .getString("prepayid");// 微信预支付编号
                                String sign = jsonObject.getString("sign");// 签名
                                String timestamp = jsonObject
                                        .getString("timestamp");// 时间戳

                                IWXAPI api = WXAPIFactory.createWXAPI(context, appid);

                                PayReq req = new PayReq();
                                req.appId = appid;
                                req.partnerId = partnerid;
                                req.prepayId = prepayid;
                                req.nonceStr = noncestr;
                                req.timeStamp = timestamp;
                                req.packageValue = packageStr;
                                req.sign = sign;
                                req.extData = "app data"; // optional

                                Log.d("huting----------", req.toString());

                                Toast.makeText(context, "正常调起支付",
                                        Toast.LENGTH_SHORT).show();
                                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                                api.sendReq(req);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                JSONObject obj2 = new JSONObject(json);
                                String error = obj2.getString("error");
                                if (error != null) {
                                    Toast.makeText(context, error,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    /**
     *
     *  获取支付宝原生支付的参数
     */
    public void loadingAlipayNativePaymentData(String pay_sn){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("pay_sn", pay_sn);

        Log.d("huting", Constants.URL_ALIPAY_NATIVE_GOODS);
        Log.d("huting", myApplication.getLoginKey());
        Log.d("huting", pay_sn);

        RemoteDataHandler.asyncLoginPostDataString(
                Constants.URL_ALIPAY_NATIVE_GOODS, params, myApplication,
                new Callback() {
                    @Override
                    public void dataLoaded(ResponseData data) {
                        String json = data.getJson();
                        if (data.getCode() == HttpStatus.SC_OK) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                String signStr = jsonObject.optString("signStr");

                                Log.d("huting-----nativePay", signStr);
                                PayDemoActivity payDemoActivity = new PayDemoActivity(context, signStr);
                                payDemoActivity.doPay();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                JSONObject obj2 = new JSONObject(json);
                                String error = obj2.getString("error");
                                if (error != null) {
                                    Toast.makeText(context, error,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 确认收货、取消订单
     *
     * @param url
     * @param //orderID 订单ID
     */
    public void loadingSaveOrderData(String url, String order_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("order_id", order_id);

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication,
                new Callback() {
                    @Override
                    public void dataLoaded(ResponseData data) {
                        String json = data.getJson();
                        if (data.getCode() == HttpStatus.SC_OK) {
                            if (json.equals("1")) {
                                // Toast.makeText(context, "",
                                // Toast.LENGTH_SHORT).show();;
                                // 刷新界面
                                Intent mIntent = new Intent(
                                        Constants.PAYMENT_SUCCESS);
                                context.sendBroadcast(mIntent);
                            }

                        } else {
                            try {
                                JSONObject obj2 = new JSONObject(json);
                                String error = obj2.getString("error");
                                if (error != null) {
                                    Toast.makeText(context, error,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    class ViewHolder {
        LinearLayout linearLayoutFLag;
        Button buttonFuKuan;
        LinearLayout addViewID;
    }
}

package net.shopnc.shop.custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.shopnc.shop.MainFragmentManager;
import net.shopnc.shop.R;
import net.shopnc.shop.bean.AddressDetails;
import net.shopnc.shop.bean.BuyStep1;
import net.shopnc.shop.bean.InvoiceInFO;
import net.shopnc.shop.bean.RpacketInfo;
import net.shopnc.shop.bean.Spec;
import net.shopnc.shop.common.Constants;
import net.shopnc.shop.common.MyShopApplication;
import net.shopnc.shop.common.ShopHelper;
import net.shopnc.shop.http.RemoteDataHandler;
import net.shopnc.shop.http.ResponseData;
import net.shopnc.shop.ncinterface.INCOnNumModify;
import net.shopnc.shop.ncinterface.INCOnStringModify;
import net.shopnc.shop.ui.type.BuyStep1Activity;
import net.shopnc.shop.ui.type.VBuyStep1Activity;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 商品详细页代金券选择弹出窗口
 *
 * @author dqw
 * @date 2015/6/30.
 */
public class NCGoodsSpecPopupWindow {
    private Context context;
    private MyShopApplication myApplication;

    private INCOnNumModify incOnNumModify;
    private INCOnStringModify incOnStringModify;

    private String storeMemberId, storeMemberName;
    private String goodsId, ifCart, isFcode, isVirtual;
    private int goodsNum, goodsLimit;
    private View popupView;
    private PopupWindow mPopupWindow;
    private ImageView ivGoodsImage;
    private TextView tvGoodsName, tvGoodsPrice, tvGoodsStorng;

    //规格
    private LinearLayout llGoodsSpec;
    private HashMap<String, View> viewsSpec = new HashMap<String, View>();//存储规格View
    private HashMap<String, Integer> hashMap = new HashMap<String, Integer>();//存储规格参数
    private int[] specListSort;

    private Button btnAppCommonAdd, btnAppCommonMinus;
    private TextView tvAppCommonCount;

    //按钮
    private TextView imID, showCartLayoutID;
    private Button buyStepID, addCartID;


    public NCGoodsSpecPopupWindow(final Context context, INCOnNumModify incOnNumModify,INCOnStringModify incOnStringModify, final String storeMemberId, final String storeMemberName) {
        this.context = context;
        this.incOnNumModify = incOnNumModify;
        this.incOnStringModify = incOnStringModify;
        this.storeMemberId = storeMemberId;
        this.storeMemberName = storeMemberName;
        myApplication = (MyShopApplication) context.getApplicationContext();

        popupView = LayoutInflater.from(context).inflate(R.layout.popupwindow_goods_spec_view, null);
        mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });

        //半透明背景
        FrameLayout flBack = (FrameLayout) popupView.findViewById(R.id.flBack);
        flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });

        //商品信息
        ivGoodsImage = (ImageView) popupView.findViewById(R.id.ivGoodsImage);
        tvGoodsName = (TextView) popupView.findViewById(R.id.tvGoodsName);
        tvGoodsPrice = (TextView) popupView.findViewById(R.id.tvGoodsPrice);
        tvGoodsStorng = (TextView) popupView.findViewById(R.id.tvGoodsStrong);

        //规格
        llGoodsSpec = (LinearLayout) popupView.findViewById(R.id.llGoodsSpec);

        //数量控制
        btnAppCommonAdd = (Button) popupView.findViewById(R.id.btnAppCommonAdd);
        btnAppCommonMinus = (Button) popupView.findViewById(R.id.btnAppCommonMinus);
        tvAppCommonCount = (TextView) popupView.findViewById(R.id.tvAppCommonCount);

        //客服按钮
        imID = (TextView) popupView.findViewById(R.id.imID);
        imID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShopHelper.showIm(context, myApplication, storeMemberId, storeMemberName);
            }
        });

        //购物车按钮
        showCartLayoutID = (TextView) popupView.findViewById(R.id.showCartLayoutID);
        showCartLayoutID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainFragmentManager.class);
                myApplication.sendBroadcast(new Intent(Constants.SHOW_CART_URL));
                context.startActivity(intent);
            }
        });

        //立即购买
        buyStepID = (Button) popupView.findViewById(R.id.buyStepID);
        buyStepID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(context, myApplication.getLoginKey())) {
                        if (isVirtual.equals("1")) {
                            Intent intent = new Intent(context, VBuyStep1Activity.class);
                            intent.putExtra("is_fcode", isFcode);
                            intent.putExtra("ifcart", 0);
                            intent.putExtra("goodscount", tvAppCommonCount.getText().toString());
                            intent.putExtra("cart_id", goodsId);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, BuyStep1Activity.class);
                            intent.putExtra("is_fcode", isFcode);
                            intent.putExtra("ifcart", 0);
                            intent.putExtra("cart_id", goodsId + "|" + tvAppCommonCount.getText().toString());
                            context.startActivity(intent);
                        }
                    }
            }
        });

        //加购物车
        addCartID = (Button) popupView.findViewById(R.id.addCartID);
        addCartID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(context, myApplication.getLoginKey())) {
                   ShopHelper.addCart(context, myApplication, goodsId, Integer.parseInt(tvAppCommonCount.getText().toString()));
                }
            }
        });

    }


    /**
     * 设置商品信息
     *
     * @param goodsName
     * @param goodsImage
     * @param goodsPrice
     * @param goodsStorage
     * @param goodsId
     * @param ifCart
     * @param goodsNum
     * @param goodsLimit
     * @param isFcode
     * @param isVirtual
     */
    public void setGoodsInfo(String goodsName, String goodsImage, String goodsPrice, String goodsStorage, String goodsId, String ifCart, int goodsNum, final int goodsLimit, String isFcode, String isVirtual) {
        this.goodsId = goodsId;
        this.ifCart = ifCart;
        this.goodsNum = goodsNum;
        this.goodsLimit = goodsLimit;
        this.isFcode = isFcode;
        this.isVirtual = isVirtual;

        //商品信息显示
        ShopHelper.loadImage(ivGoodsImage, goodsImage);
        tvGoodsName.setText(goodsName);
        tvGoodsPrice.setText(context.getText(R.string.text_rmb) + goodsPrice);
        tvGoodsStorng.setText("库存:" + goodsStorage + "件");

        //设置购买数量
        tvAppCommonCount.setText(String.valueOf(goodsNum));

        //数量增加
        btnAppCommonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.valueOf(tvAppCommonCount.getText().toString());
                if (goodsLimit > 0 && num < goodsLimit) {
                    num = num + 1;
                    incOnNumModify.onModify(num);
                    tvAppCommonCount.setText(String.valueOf(num));
                } else {
                    ShopHelper.showMessage(context, "最多购买" + String.valueOf(goodsLimit) + "件");
                }
            }
        });

        //数量减少
        btnAppCommonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.valueOf(tvAppCommonCount.getText().toString());
                if (num > 1) {
                    num = num - 1;
                    incOnNumModify.onModify(num);
                    tvAppCommonCount.setText(String.valueOf(num));
                }
            }
        });

        //控制是否显示购物车按钮
        if(!ifCart.equals("1")) {
            addCartID.setVisibility(View.GONE);
        } else {
            addCartID.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置规格信息
     *
     * @param specListString
     * @param specNameString
     * @param specValueString
     * @param goodsSpecString
     */
    public void setSpecInfo(final String specListString, String specNameString, String specValueString, String goodsSpecString) {
        llGoodsSpec.removeAllViews();
        if (specNameString.equals("null") || specValueString.equals("null")) {
            return;
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        //显示规格属性
        try {
            JSONObject jsonObjName = new JSONObject(specNameString);
            JSONObject jsonObjValue = new JSONObject(specValueString);
            Iterator<?> itName = jsonObjName.keys();
            while (itName.hasNext()) {
                ArrayList<Spec> list = new ArrayList<Spec>();
                final String specID = itName.next().toString();
                String specName = jsonObjName.getString(specID);
                String specValueName = jsonObjValue.getString(specID);
                JSONObject jsonObj = new JSONObject(specValueName);
                Iterator<?> iterator = jsonObj.keys();


                LinearLayout goodSpecListView = (LinearLayout) inflater.inflate(R.layout.goods_spec_list_view, null);
                TextView specNameID = (TextView) goodSpecListView.findViewById(R.id.specNameID);
                LinearLayout specListID = (LinearLayout) goodSpecListView.findViewById(R.id.specListID);
                specNameID.setText(specName);

                while (iterator.hasNext()) {
                    final String SpecID = iterator.next().toString();
                    String SpecName = jsonObj.getString(SpecID);
                    Spec s = new Spec();
                    s.setSpecID(SpecID);
                    s.setSpecName(SpecName);
                    list.add(s);

                    LinearLayout specListItemView = (LinearLayout) inflater.inflate(R.layout.spec_list_item_view, null);
                    Button specValuesID = (Button) specListItemView.findViewById(R.id.specValuesID);
                    specValuesID.setPadding(10, 10, 10, 10);

                    specValuesID.setText(SpecName);
                    specListID.addView(specListItemView);
                    viewsSpec.put(SpecID, specValuesID);//存储全部规格view

                    if (goodsSpecString != null && !goodsSpecString.equals("") && !goodsSpecString.equals("null")) {
                        JSONObject jsonGoodsSpec = new JSONObject(goodsSpecString);
                        Iterator<?> itGoodsSpec = jsonGoodsSpec.keys();
                        while (itGoodsSpec.hasNext()) {
                            String goodsSpecID = itGoodsSpec.next().toString();
                            if (goodsSpecID.equals(SpecID)) {
                                specValuesID.setActivated(true);
                            }
                        }
                    }


                    specValuesID.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hashMap.put(specID, Integer.parseInt(SpecID));
                            Iterator iterator = viewsSpec.keySet().iterator();

                            while (iterator.hasNext()) {
                                String viewSpecID = iterator.next().toString();
                                Button sview = (Button) viewsSpec.get(viewSpecID);
                                sview.setActivated(false);

                                Iterator it = hashMap.keySet().iterator();
                                specListSort = new int[hashMap.size()];
                                int count = 0;

                                while (it.hasNext()) {
                                    String sID = it.next().toString();
                                    String ssID = String.valueOf(hashMap.get(sID));
                                    specListSort[count] = hashMap.get(sID);
                                    count++;
                                    if (viewSpecID.equals(ssID)) {
                                        sview.setActivated(true);
                                    }
                                }
                            }

                            Arrays.sort(specListSort);

                            String spec_list_str = "";
                            for (int i = 0; i < specListSort.length; i++) {
                                spec_list_str += "|" + specListSort[i];
                            }

                            spec_list_str = spec_list_str.replaceFirst("\\|", "");

                            try {
                                JSONObject specListObj = new JSONObject(specListString);
                                goodsId = specListObj.getString(spec_list_str);
                                incOnStringModify.onModify(goodsId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
                llGoodsSpec.addView(goodSpecListView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void showPopupWindow() {
        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    /**关掉popurWindow*/
    public void closePoperWindow(){
        mPopupWindow.dismiss();
    }
}

package net.shopnc.shop.ui.type;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import net.shopnc.shop.BaseActivity;
import net.shopnc.shop.R;

import net.shopnc.shop.common.MyExceptionHandler;

import net.shopnc.shop.custom.MyProgressDialog;


/**
 * 商品详细页Activity
 *
 * @author dqw
 * @Time 2015-7-14
 */
public class GoodsDetailsActivity extends BaseActivity implements GoodsDetailFragment.OnFragmentInteractionListener, GoodsDetailBodyFragment.OnFragmentInteractionListener, GoodsDetailEvaluateFragment.OnFragmentInteractionListener {

    FragmentManager fragmentManager = getFragmentManager();

    private String goods_id;
    private Button btnGoodsDetail, btnGoodsBody, btnGoodsEvaluate;
    private GoodsDetailFragment goodsDetailFragment;
    private GoodsDetailBodyFragment goodsDetailBodyFragment;
    private GoodsDetailEvaluateFragment goodsDetailEvaluateFragment;

    private String title = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_details_view);
        MyExceptionHandler.getInstance().setContext(this);
        goods_id = GoodsDetailsActivity.this.getIntent().getStringExtra("goods_id");

        btnGoodsDetail = (Button) findViewById(R.id.btnGoodsDetail);
        btnGoodsBody = (Button) findViewById(R.id.btnGoodsBody);
        btnGoodsEvaluate = (Button) findViewById(R.id.btnGoodsEvaluate);

        goodsDetailFragment = GoodsDetailFragment.newInstance(goods_id);
        goodsDetailBodyFragment = GoodsDetailBodyFragment.newInstance(goods_id);
        goodsDetailEvaluateFragment = GoodsDetailEvaluateFragment.newInstance(goods_id);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.llMain, goodsDetailFragment);
        transaction.add(R.id.llMain, goodsDetailBodyFragment);
        transaction.add(R.id.llMain, goodsDetailEvaluateFragment);
        transaction.commit();
        showGoodsDetail();

    }

    /**
     * 更换新商品
     */
    public void changeGoods(String goodsId) {
        goods_id = goodsId;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(goodsDetailBodyFragment);
        transaction.remove(goodsDetailEvaluateFragment);
        goodsDetailBodyFragment = GoodsDetailBodyFragment.newInstance(goods_id);
        goodsDetailEvaluateFragment = GoodsDetailEvaluateFragment.newInstance(goods_id);
        transaction.add(R.id.llMain, goodsDetailBodyFragment);
        transaction.add(R.id.llMain, goodsDetailEvaluateFragment);
        transaction.show(goodsDetailFragment);
        transaction.hide(goodsDetailBodyFragment);
        transaction.hide(goodsDetailEvaluateFragment);
        transaction.commit();
    }

    /**
     * 返回按钮点击
     */
    public void btnBackClick(View view) {
        if (title.equals("商品")){
            finish();
        }else{
            (GoodsDetailsActivity.this).showGoodsDetail();
        }
    }

    /**
     * 商品详细按钮点击
     */
    public void btnGoodsDetailClick(View view) {
        showGoodsDetail();
    }

    /**
     * 商品描述按钮点击
     */
    public void btnGoodsBodyClick(View view) {
        showGoodsBody();
    }

    /**
     * 商品评价按钮点击
     */
    public void btnGoodsEvaluateClick(View view) {
        showGoodsEvaluate();
    }

    /**
     * 设置Tab按钮状态
     */
    private void setTabButtonState(Button btn) {
        btnGoodsDetail.setActivated(false);
        btnGoodsBody.setActivated(false);
        btnGoodsEvaluate.setActivated(false);
        btn.setActivated(true);
    }

    /**
     * 显示商品Fragement
     */
    public void showGoodsDetail() {
        setTabButtonState(btnGoodsDetail);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(goodsDetailFragment);
        transaction.hide(goodsDetailBodyFragment);
        transaction.hide(goodsDetailEvaluateFragment);
        title = "商品";
        transaction.commit();
    }

    /**
     * 显示商品描述
     */
    public void showGoodsBody() {
        setTabButtonState(btnGoodsBody);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(goodsDetailFragment);
        transaction.show(goodsDetailBodyFragment);
        transaction.hide(goodsDetailEvaluateFragment);
        title = "详情";
        transaction.commit();
    }

    /**
     * 显示商品评价
     */
    public void showGoodsEvaluate() {
        setTabButtonState(btnGoodsEvaluate);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(goodsDetailFragment);
        transaction.hide(goodsDetailBodyFragment);
        transaction.show(goodsDetailEvaluateFragment);
        title = "详情";
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

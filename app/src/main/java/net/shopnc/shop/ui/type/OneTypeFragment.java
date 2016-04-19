package net.shopnc.shop.ui.type;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.shopnc.shop.R;
import net.shopnc.shop.adapter.BrandGridViewAdapter;
import net.shopnc.shop.adapter.GoodsClassGridViewAdapter;
import net.shopnc.shop.bean.BrandInfo;
import net.shopnc.shop.bean.GoodsClassInfo;
import net.shopnc.shop.bean.OneType;
import net.shopnc.shop.bracode.ui.CaptureActivity;
import net.shopnc.shop.common.Constants;
import net.shopnc.shop.common.MyExceptionHandler;
import net.shopnc.shop.common.MyShopApplication;
import net.shopnc.shop.common.ShopHelper;
import net.shopnc.shop.http.RemoteDataHandler;
import net.shopnc.shop.http.RemoteDataHandler.Callback;
import net.shopnc.shop.http.ResponseData;
import net.shopnc.shop.ui.home.SearchActivity;
import net.shopnc.shop.ui.mine.IMFriendsListActivity;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 商品分类界面
 *
 * @author dqw
 * @Time 2015-7-2
 */
public class OneTypeFragment extends Fragment {
    private MyShopApplication myApplication;
    private ScrollView svGoodsClassRoot;
    private LinearLayout llGoodsClassRoot;
    private View currentGoodsClassView;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private GridView gvBrand;
    private ScrollView svGoodsClass;
    private LinearLayout llGoodsClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View viewLayout = inflater.inflate(R.layout.main_one_type_view, container, false);

        myApplication = (MyShopApplication) getActivity().getApplicationContext();
        MyExceptionHandler.getInstance().setContext(getActivity());
        svGoodsClassRoot = (ScrollView) viewLayout.findViewById(R.id.svGoodsClassRoot);
        llGoodsClassRoot = (LinearLayout) viewLayout.findViewById(R.id.llGoodsClassRoot);
        gvBrand = (GridView) viewLayout.findViewById(R.id.gvBrand);
        svGoodsClass = (ScrollView) viewLayout.findViewById(R.id.svGoodsClass);
        llGoodsClass = (LinearLayout) viewLayout.findViewById(R.id.llGoodsClass);

        TextView tvSearch = (TextView)viewLayout.findViewById(R.id.tvSearch);

        //显示搜索热词
        String searchHotName = myApplication.getSearchHotName();
        if (searchHotName != null && !searchHotName.equals("")) {
            tvSearch.setHint(searchHotName);
        } else {
            tvSearch.setHint(R.string.default_search_text);
        }

        //搜索
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        //二维码扫描
        Button btnCamera = (Button) viewLayout.findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivity(intent);
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

        loadGoodsClassRoot();
        loadBrandList();

        return viewLayout;
    }

    /**
     * 加载一级商品分类
     */
    public void loadGoodsClassRoot() {
        String url = Constants.URL_GOODSCLASS;
        RemoteDataHandler.asyncDataStringGet(url, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    String json = data.getJson();
                    try {
                        JSONObject obj = new JSONObject(json);
                        String array = obj.getString("class_list");
                        ArrayList<OneType> typeList = OneType.newInstanceList(array);
                        OneType oneType = new OneType("0", "品牌推荐", Constants.WAP_BRAND_ICON, "");
                        typeList.add(0, oneType);

                        for (int i = 0; i < typeList.size(); i++) {
                            OneType classItem = typeList.get(i);
                            boolean isOn = false;
                            if (i == 0) {
                                isOn = true;
                            }
                            addGoodsClass(classItem, isOn);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.load_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 添加分类节点
     */
    private void addGoodsClass(OneType classItem, final boolean isOn) {
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.listivew_type_item, null);
        final ImageView ivGoodsClassImage = (ImageView) v.findViewById(R.id.ivGoodsClassImage);
        TextView tvGoodsClassId = (TextView) v.findViewById(R.id.tvGoodsClassId);
        TextView tvGoodsClassName = (TextView) v.findViewById(R.id.tvGoodsClassName);
        tvGoodsClassId.setText(classItem.getGc_id());
        tvGoodsClassName.setText(classItem.getGc_name());

        imageLoader.loadImage(classItem.getImage(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ivGoodsClassImage.setImageBitmap(loadedImage);
                if (!isOn) {
                    ivGoodsClassImage.getDrawable().setColorFilter(R.color.nc_fg, PorterDuff.Mode.MULTIPLY);
                }
            }

        });
        if (isOn) {
            currentGoodsClassView = v;
            setCurrentGoodsClass(v);
        } else {
            resetCurrentGoodsClass(v);
        }

        //点击
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCurrentGoodsClass(currentGoodsClassView);
                setCurrentGoodsClass(view);

                currentGoodsClassView = view;
                svGoodsClassRoot.smoothScrollTo(0, view.getTop());

                TextView tvGoodsClassId = (TextView) view.findViewById(R.id.tvGoodsClassId);
                String goodsClassId = tvGoodsClassId.getText().toString();
                if (goodsClassId.equals("0")) {
                    gvBrand.setVisibility(View.VISIBLE);
                    svGoodsClass.setVisibility(View.GONE);
                } else {
                    showGoodsClass(goodsClassId);
                }
            }
        });
        llGoodsClassRoot.addView(v);
    }

    /**
     * 高亮显示选中分类
     */
    private void setCurrentGoodsClass(View view) {
        ImageView ivGoodsClassImage = (ImageView) view.findViewById(R.id.ivGoodsClassImage);
        TextView tvGoodsClassName = (TextView) view.findViewById(R.id.tvGoodsClassName);
        TextView tvLine = (TextView) view.findViewById(R.id.tvLine);

        tvGoodsClassName.setTextColor(getActivity().getResources().getColor(R.color.nc_btn_bg));
        tvLine.setBackgroundColor(getActivity().getResources().getColor(R.color.nc_btn_bg));
        ViewGroup.LayoutParams params = tvLine.getLayoutParams();
        params.height = getResources().getDimensionPixelSize(R.dimen.dimen_2);
        tvLine.setLayoutParams(params);
        ivGoodsClassImage.getDrawable().clearColorFilter();
    }

    /**
     * 重置一级分类选中状态
     */
    private void resetCurrentGoodsClass(View view) {
        ImageView ivGoodsClassImage = (ImageView) view.findViewById(R.id.ivGoodsClassImage);
        TextView tvGoodsClassName = (TextView) view.findViewById(R.id.tvGoodsClassName);
        TextView tvLine = (TextView) view.findViewById(R.id.tvLine);

        tvGoodsClassName.setTextColor(getActivity().getResources().getColor(R.color.nc_text));
        tvLine.setBackgroundColor(getActivity().getResources().getColor(R.color.nc_fg));
        ViewGroup.LayoutParams params = tvLine.getLayoutParams();
        params.height = getResources().getDimensionPixelSize(R.dimen.dimen_1);
        tvLine.setLayoutParams(params);
        ivGoodsClassImage.getDrawable().setColorFilter(R.color.nc_fg, PorterDuff.Mode.MULTIPLY);
    }

    /**
     * 显示品牌列表
     */
    private void loadBrandList() {
        RemoteDataHandler.asyncDataStringGet(Constants.URL_BRAND, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    String json = data.getJson();
                    try {

                        JSONObject obj = new JSONObject(json);
                        String brandList = obj.getString("brand_list");
                        if (brandList != "" && brandList != null && !brandList.equals("[]")) {
                            ArrayList<BrandInfo> brandArray = BrandInfo.newInstanceList(brandList);
                            BrandGridViewAdapter brandGridViewAdapter = new BrandGridViewAdapter(getActivity());
                            brandGridViewAdapter.setBrandArray(brandArray);
                            gvBrand.setAdapter(brandGridViewAdapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.load_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 显示下级分类
     */
    private void showGoodsClass(String classId) {
        //隐藏品牌列表
        gvBrand.setVisibility(View.GONE);
        svGoodsClass.setVisibility(View.VISIBLE);
        svGoodsClass.scrollTo(0, 0);
        RemoteDataHandler.asyncDataStringGet(Constants.URL_GOODS_CLASS_CHILD_ALL + "&gc_id=" + classId, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    String json = data.getJson();
                    try {
                        JSONObject obj = new JSONObject(json);
                        String list = obj.getString("class_list");
                        if (list != "" && list != null && !list.equals("[]")) {
                            ArrayList<GoodsClassInfo> array = GoodsClassInfo.newInstanceList(list);
                            llGoodsClass.removeAllViews();
                            for (int i = 0; i < array.size(); i++) {
                                final GoodsClassInfo goodsClassInfo = array.get(i);

                                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view = vi.inflate(R.layout.item_goods_class, null);
                                if (i == 0) {
                                    TextView tvLine = (TextView) view.findViewById(R.id.tvLine);
                                    tvLine.setVisibility(View.INVISIBLE);
                                }

                                ImageView ivGoodsClassDot = (ImageView) view.findViewById(R.id.tvGoodsClassDot);
                                int index = i % 10;
                                switch (index) {
                                    case 0:
                                        ivGoodsClassDot.setImageDrawable(getResources().getDrawable(R.drawable.nc_sharp_dot0));
                                        break;
                                    case 1:
                                        ivGoodsClassDot.setImageDrawable(getResources().getDrawable(R.drawable.nc_sharp_dot1));
                                        break;
                                    case 2:
                                        ivGoodsClassDot.setImageDrawable(getResources().getDrawable(R.drawable.nc_sharp_dot2));
                                        break;
                                    case 3:
                                        ivGoodsClassDot.setImageDrawable(getResources().getDrawable(R.drawable.nc_sharp_dot3));
                                        break;
                                    case 4:
                                        ivGoodsClassDot.setImageDrawable(getResources().getDrawable(R.drawable.nc_sharp_dot4));
                                        break;
                                    case 5:
                                        ivGoodsClassDot.setImageDrawable(getResources().getDrawable(R.drawable.nc_sharp_dot5));
                                        break;
                                    case 6:
                                        ivGoodsClassDot.setImageDrawable(getResources().getDrawable(R.drawable.nc_sharp_dot6));
                                        break;
                                    case 7:
                                        ivGoodsClassDot.setImageDrawable(getResources().getDrawable(R.drawable.nc_sharp_dot7));
                                        break;
                                    case 8:
                                        ivGoodsClassDot.setImageDrawable(getResources().getDrawable(R.drawable.nc_sharp_dot8));
                                        break;
                                    case 9:
                                        ivGoodsClassDot.setImageDrawable(getResources().getDrawable(R.drawable.nc_sharp_dot9));
                                        break;
                                }

                                TextView tvGoodsClassName = (TextView) view.findViewById(R.id.tvGoodsClassName);
                                tvGoodsClassName.setText(goodsClassInfo.getGcName());

                                Button btnGoodsClass = (Button) view.findViewById(R.id.btnGoodsClass);
                                btnGoodsClass.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent =new Intent(getActivity(),GoodsListFragmentManager.class);
                                        intent.putExtra("gc_id", goodsClassInfo.getGcId());
                                        intent.putExtra("gc_name", goodsClassInfo.getGcName());
                                        startActivity(intent);
                                    }
                                });

                                GridView gvGoodsClass = (GridView) view.findViewById(R.id.gvGoodsClass);

                                ArrayList<GoodsClassInfo> goodsClassList = GoodsClassInfo.newInstanceList(goodsClassInfo.getChild());

                                final GoodsClassGridViewAdapter goodsClassGridViewAdapter = new GoodsClassGridViewAdapter(getActivity());
                                goodsClassGridViewAdapter.setGoodsClassInfoList(goodsClassList);
                                gvGoodsClass.setAdapter(goodsClassGridViewAdapter);
                                gvGoodsClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        GoodsClassInfo goodsClassInfo = (GoodsClassInfo)goodsClassGridViewAdapter.getItem(i);
                                        Intent intent =new Intent(getActivity(),GoodsListFragmentManager.class);
                                        intent.putExtra("gc_id", goodsClassInfo.getGcId());
                                        intent.putExtra("gc_name", goodsClassInfo.getGcName());
                                        startActivity(intent);
                                    }
                                });
                                llGoodsClass.addView(view);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.load_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
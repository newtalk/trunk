package net.shopnc.shop.ui.home;

import java.util.List;

import net.shopnc.shop.R;
import net.shopnc.shop.adapter.SearchGridViewAdapter;
import net.shopnc.shop.adapter.SearchListViewAdapter;
import net.shopnc.shop.bean.Search;
import net.shopnc.shop.common.DatabaseHelper;
import net.shopnc.shop.common.MyExceptionHandler;
import net.shopnc.shop.common.MyShopApplication;
import net.shopnc.shop.common.SystemHelper;
import net.shopnc.shop.common.T;
import net.shopnc.shop.custom.MyGridView;
import net.shopnc.shop.custom.MyListView;
import net.shopnc.shop.ui.type.GoodsListFragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

/**
 * 商品搜索
 *
 * @author dqw
 * @Time 2015/7/10
 */
public class SearchActivity extends OrmLiteBaseActivity<DatabaseHelper> {
    private MyShopApplication myApplication;

    private String searchHotName;
    private String searchHotValue;

    private EditText etSearchText;
    private Button btnSearch;

    //搜索关键词
    private MyGridView gvSearchKeyList;
    private SearchGridViewAdapter searchKeyListAdapter;

    //历史搜索
    private MyListView searchListView;
    private SearchListViewAdapter adapter;
    private RuntimeExceptionDao<Search, Integer> searchDAO;

    private LinearLayout llHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);
        myApplication = (MyShopApplication) getApplicationContext();
        MyExceptionHandler.getInstance().setContext(this);
        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        etSearchText = (EditText) findViewById(R.id.etSearchText);
        btnSearch = (Button) findViewById(R.id.btnSearch);

        //搜索关键词
        gvSearchKeyList = (MyGridView) findViewById(R.id.gvSearchKeyList);
        searchKeyListAdapter = new SearchGridViewAdapter(SearchActivity.this);
        gvSearchKeyList.setAdapter(searchKeyListAdapter);
        searchKeyListAdapter.setSearchLists(myApplication.getSearchKeyList());
        searchKeyListAdapter.notifyDataSetChanged();
        gvSearchKeyList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String keyword = myApplication.getSearchKeyList().get(i);
                showGoodsList(keyword);
            }
        });

        //历史搜索
        llHistory = (LinearLayout) findViewById(R.id.llHistory);
        searchListView = (MyListView) findViewById(R.id.searchListView);
        adapter = new SearchListViewAdapter(SearchActivity.this);
        searchDAO = getHelper().getSearchDataDao();
        searchListView.setAdapter(adapter);
        final List<Search> sList = queryAll();
        adapter.setSearchLists(sList);
        adapter.notifyDataSetChanged();
        if (sList.size() <= 0) {
            llHistory.setVisibility(View.GONE);
        } else {
            llHistory.setVisibility(View.VISIBLE);
        }

        //设置热门搜索词
        searchHotName = myApplication.getSearchHotName();
        searchHotValue = myApplication.getSearchHotValue();
        if (searchHotName != null && !searchHotName.equals("")) {
            etSearchText.setHint(searchHotName);
        } else {
            etSearchText.setHint(R.string.default_search_text);
        }

        //清空搜索历史
        Button btnClearHistory = (Button) findViewById(R.id.btnClearHistory);
        btnClearHistory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDAO.delete(sList);
                llHistory.setVisibility(View.GONE);
            }
        });

        //搜索按钮点击
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(-1 != SystemHelper.getNetworkType(SearchActivity.this)){
                    String keyword = etSearchText.getText().toString();
                    if (keyword.equals("") || keyword.equals("") || keyword == null) {
                        if (searchHotValue.equals("") || searchHotValue == null) {
                            Toast.makeText(SearchActivity.this, "内容不能为空",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            keyword = searchHotValue;
                        }
                    }
                    //最多保留10条搜索记录
                    if (sList.size() > 7) {
                        searchDAO.delete(sList.get(0));
                    }
                    searchDAO.createIfNotExists(new Search(keyword));
                    showGoodsList(keyword);
                }else {
                    T.showShort(SearchActivity.this,getString(R.string.network_inavaible));
                }

            }
        });

        //历史搜索记录点击
        searchListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Search bean = (Search) searchListView.getItemAtPosition(arg2);
                showGoodsList(bean.getSearchKeyWord());
            }
        });
    }

    /**
     * 显示商品搜索结果列表
     */
    private void showGoodsList(String keyword) {
        Intent intent = new Intent(SearchActivity.this,
                GoodsListFragmentManager.class);
        intent.putExtra("keyword", keyword);
        SearchActivity.this.startActivity(intent);
        SearchActivity.this.finish();
    }

    /**
     * 查询所有的
     */
    private List<Search> queryAll() {
        List<Search> searchList = searchDAO.queryForAll();
        return searchList;
    }
}

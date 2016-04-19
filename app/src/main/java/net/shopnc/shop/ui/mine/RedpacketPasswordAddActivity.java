package net.shopnc.shop.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import net.shopnc.shop.BaseActivity;
import net.shopnc.shop.R;
import net.shopnc.shop.common.Constants;
import net.shopnc.shop.common.MyExceptionHandler;
import net.shopnc.shop.common.MyShopApplication;
import net.shopnc.shop.common.ShopHelper;
import net.shopnc.shop.http.RemoteDataHandler;
import net.shopnc.shop.http.ResponseData;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RedpacketPasswordAddActivity extends BaseActivity {
    private MyShopApplication myApplication;
    private EditText etPwdCode;
    private EditText etCode;
    private ImageView ivCode;
    private Button btnSubmit;
    private String codeKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redpacket_password_add);
        MyExceptionHandler.getInstance().setContext(this);
        setCommonHeader("");
        setTabButton();

        myApplication = (MyShopApplication) getApplicationContext();

        etPwdCode = (EditText) findViewById(R.id.etPwdCode);
        etCode = (EditText) findViewById(R.id.etCode);
        ivCode = (ImageView) findViewById(R.id.ivCode);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        ivCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadSeccodeCode();
            }
        });

        initSubmitButton();
        loadSeccodeCode();
    }

    /**
     * 提交按钮状态
     */
    private void initSubmitButton() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etPwdCode.getText().toString().equals("") || etCode.getText().toString().equals("")) {
                    btnSubmit.setActivated(false);
                } else {
                    btnSubmit.setActivated(true);
                }
            }
        };
        etPwdCode.addTextChangedListener(textWatcher);
        etCode.addTextChangedListener(textWatcher);
    }

    /**
     * 设置头部切换按钮
     */
    private void setTabButton() {
        Button btnRedpacketList = (Button) findViewById(R.id.btnRedpacketList);
        Button btnRedpacketPasswordAdd = (Button) findViewById(R.id.btnRedpacketPasswordAdd);
        btnRedpacketPasswordAdd.setActivated(true);
        btnRedpacketList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RedpacketPasswordAddActivity.this, RedpacketListActivity.class));
                finish();
            }
        });
    }

    /**
     * 加载图片验证码
     */
    private void loadSeccodeCode() {
        etCode.setText("");
        RemoteDataHandler.asyncDataStringGet(Constants.URL_SECCODE_MAKECODEKEY, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        codeKey = obj.getString("codekey");
                        ShopHelper.loadImage(ivCode, Constants.URL_SECCODE_MAKECODE + "&k=" + codeKey);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(RedpacketPasswordAddActivity.this, json);
                }
            }
        });
    }

    /**
     * 提交
     */
    public void btnSubmitClick(View view) {
        if (btnSubmit.isActivated()) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("key", myApplication.getLoginKey());
            params.put("pwd_code", etPwdCode.getText().toString());
            params.put("codekey", codeKey);
            params.put("captcha", etCode.getText().toString());
            RemoteDataHandler.asyncLoginPostDataString(Constants.URL_MEMBER_REDPACKET_ADD, params, myApplication, new RemoteDataHandler.Callback() {
                @Override
                public void dataLoaded(ResponseData data) {
                    String json = data.getJson();
                    if (data.getCode() == HttpStatus.SC_OK) {
                        ShopHelper.showMessage(RedpacketPasswordAddActivity.this, "红包领取成功");
                        startActivity(new Intent(RedpacketPasswordAddActivity.this, RedpacketListActivity.class));
                        finish();
                    } else {
                        ShopHelper.showApiError(RedpacketPasswordAddActivity.this, json);
                        loadSeccodeCode();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_redpacket_password_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

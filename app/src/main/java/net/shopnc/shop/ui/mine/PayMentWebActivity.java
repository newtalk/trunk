package net.shopnc.shop.ui.mine;

import net.shopnc.shop.R;
import net.shopnc.shop.common.Constants;
import net.shopnc.shop.common.MyExceptionHandler;
import net.shopnc.shop.common.MyShopApplication;
import net.shopnc.shop.custom.RoundProgressBar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author KingKong-HE
 * @Time 2015-1-26
 * @Email KingKong@QQ.COM
 */
public class PayMentWebActivity extends Activity implements OnClickListener{
	private RoundProgressBar  roundProgressBar;
	private WebView webviewID;
	private MyShopApplication myApplication;
	private Handler mHandler = new Handler();
	@SuppressLint("JavascriptInterface") 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment_web_view);
		MyExceptionHandler.getInstance().setContext(this);
		String pay_sn = getIntent().getStringExtra("pay_sn");
		String order_sn = getIntent().getExtras().getString("order_sn");
		
		myApplication = (MyShopApplication) getApplicationContext();
		
		webviewID = (WebView) findViewById(R.id.webviewID);
		roundProgressBar = (RoundProgressBar)findViewById(R.id.roundProgressBarID);
		ImageView imageBack = (ImageView) findViewById(R.id.imageBack);
		
		WebSettings settings = webviewID.getSettings();
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);//根据屏幕缩放
		settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);//根据屏幕缩放
		settings.setSupportZoom(false);
		settings.setBuiltInZoomControls(false);
		settings.setJavaScriptEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		settings.setAppCacheEnabled(false);
		webviewID.setWebChromeClient(new MyWebChromeClient()); 
		
		if(order_sn!= null && !order_sn.equals("") && !order_sn.equals("null")){
			webviewID.loadUrl(Constants.URL_VIRTUAL_ORDER_PAYMENT+"&key="+myApplication.getLoginKey()+"&pay_sn="+order_sn);
		}else{
			webviewID.loadUrl(Constants.URL_ORDER_PAYMENT+"&key="+myApplication.getLoginKey()+"&pay_sn="+pay_sn);
		}
		webviewID.addJavascriptInterface(new PayMentJavaScriptInterface(), "demo");
		webviewID.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				view.loadUrl("file:///android_asset/error.html");
			}
		});
		imageBack.setOnClickListener(this);
		
	}
	
	private class MyWebChromeClient extends WebChromeClient {  
	    @Override  
	    public void onProgressChanged(WebView view, int newProgress) {  
	    	roundProgressBar.setProgress(newProgress);  
	        if(newProgress==100){  
	        	roundProgressBar.setVisibility(View.GONE);  
	        }  
	        super.onProgressChanged(view, newProgress);  
	    }  
	      
	}
	
	 final class PayMentJavaScriptInterface {  
		 PayMentJavaScriptInterface() { }  

	     public void checkPaymentAndroid(final String flag) {        
	         mHandler.post(new Runnable() {  
	             public void run() {  
	                 // 此处调用 HTML 中的javaScript 函数  
	            	 if(flag.equals("success")){
	            		 Toast.makeText(PayMentWebActivity.this, "支付成功",Toast.LENGTH_SHORT).show();
	            		 Intent mIntent = new Intent(Constants.PAYMENT_SUCCESS); 
			             sendBroadcast(mIntent);
	            	 }else if(flag.equals("fail")){
	            		 Toast.makeText(PayMentWebActivity.this, "支付失败，请稍后重试",Toast.LENGTH_SHORT).show();
	            	 }
	            	 finish();
	             }  
	         });  
	     }  
	 }  
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageBack:
			
			finish();
			
			break;
		default:
			break;
		}
	}  
}

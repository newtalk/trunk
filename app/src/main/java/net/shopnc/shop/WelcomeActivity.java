package net.shopnc.shop;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.shopnc.shop.common.MyExceptionHandler;

/**
 * 软件启动界面
 * @author KingKong-HE
 * @Time 2014-12-30
 * @Email KingKong@QQ.COM
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_view);

		MyExceptionHandler.getInstance().setContext(this);
        
        //加入定时器 睡眠 2000毫秒 自动跳转页面
        new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent it=new Intent();
				it.setClass(WelcomeActivity.this,MainFragmentManager.class);
				startActivity(it);
				WelcomeActivity.this.finish();
			}
		}, 1000);
        
    }
}

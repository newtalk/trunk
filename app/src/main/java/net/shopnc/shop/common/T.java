package net.shopnc.shop.common;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wj on 2015/12/18.
 */
public class T {

        private final static boolean isShow = true;

        private T(){
            throw new UnsupportedOperationException("T cannot be instantiated");
        }

        public static void showShort(Context context,CharSequence text) {
            if(isShow) Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }

        public static void showLong(Context context,CharSequence text) {
            if (isShow) Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        }
}

package br.com.hbird.whatstatus;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

public class DisplayUtils {

    private int itemWidthDP;

    public DisplayUtils(Context context, int colunms) {
        DisplayMetrics dm = new DisplayMetrics();

        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);

        itemWidthDP = Math.round((dm.widthPixels / dm.density) / colunms);
    }

    public int getItemWidthDP() {
        return itemWidthDP;
    }
}

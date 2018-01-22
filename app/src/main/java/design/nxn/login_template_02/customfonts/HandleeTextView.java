package design.nxn.login_template_02.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Borja on 14/01/2018.
 */

public class HandleeTextView extends android.support.v7.widget.AppCompatTextView{

    public HandleeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HandleeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HandleeTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Handlee-Regular.ttf");
        setTypeface(tf ,1);

    }
}

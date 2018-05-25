package ziweiyang.toppine.com.oschinadome.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import ziweiyang.toppine.com.oschinadome.MainActivity;
import ziweiyang.toppine.com.oschinadome.R;


/**
 * 异常信息界面
 * Created by haibin on 2017/5/8.
 */

public class ErrorActivity extends BaseBackActivity implements View.OnClickListener {
    @Bind(R.id.tv_crash_info)
    TextView mTextCrashInfo;

    public static void show(Context context, String message) {
        if (message == null)
            return;
        Intent intent = new Intent(context, ErrorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("message", message);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_error;
    }

    @Override
    protected void initData() {
        super.initData();
        mTextCrashInfo.setText(getIntent().getStringExtra("message"));
    }

    @OnClick({R.id.btn_restart})
    @Override
    public void onClick(View v) {   //重启到MainActivity
        switch (v.getId()) {
            case R.id.btn_restart:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}

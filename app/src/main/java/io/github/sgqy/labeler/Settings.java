package io.github.sgqy.labeler;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

public class Settings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void commandAboutClicked(View view) {
        Uri uri = Uri.parse("https://github.com/sgqy/LabelPlusAndroid");
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public void commandHelpClicked(View view) {
        Uri uri = Uri.parse("http://noodlefighter.com/label_plus/");
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public void commandExtraToolClicked(View view) {
        Intent intent = new Intent(this, Extra.class);
        startActivity(intent);
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

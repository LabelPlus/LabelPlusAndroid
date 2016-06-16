package io.github.sgqy.labeler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

public class Extra extends Activity {

    static final int FILE_CHOOSER_FOR_CONVERT = 78003;

    static {
        System.loadLibrary("bdrscan");
    }

    native boolean ScanFile(String infile, String outfile);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);
    }

    void takePath(int requestCode) {
        Intent target = FileUtils.createGetContentIntent();
        Intent intent = Intent.createChooser(target, getString(R.string.file_chooser_title));
        try {
            startActivityForResult(intent, requestCode);
        } catch (Exception ec) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            String tempPath = FileUtils.getPath(this, data.getData());

            switch (requestCode) {
                case FILE_CHOOSER_FOR_CONVERT: {
                    String bareNameExt = tempPath.substring(tempPath.lastIndexOf("/") + 1);
                    String bareExt = bareNameExt.substring(bareNameExt.lastIndexOf("."));
                    String dirPath = FileUtils.getPathWithoutFilename(new File(tempPath)).getAbsolutePath();
                    String bareName = bareNameExt.substring(0, bareNameExt.indexOf("."));
                    String targetPath = dirPath + "/" + bareName + "_bi" + bareExt;
                    try {
                        boolean convRslt = ScanFile(tempPath, targetPath);
                        if (convRslt) {
                            Toast.makeText(getApplicationContext(), R.string.extra_toast_convert_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.extra_toast_convert_fail, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ec) {
                        Toast.makeText(getApplicationContext(), R.string.extra_toast_convert_fail, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void commandSelectAndConvertClicked(View view) {
        takePath(FILE_CHOOSER_FOR_CONVERT);
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

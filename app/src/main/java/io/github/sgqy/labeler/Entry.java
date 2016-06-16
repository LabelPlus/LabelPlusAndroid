package io.github.sgqy.labeler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import io.github.sgqy.labeler.function.WorkData;

public class Entry extends Activity {

    static final int FILE_CHOOSER_FOR_LOAD = 33920;
    static final int FILE_CHOOSER_FOR_NEW = 22723;
    WorkData workData = new WorkData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        getExternalFilesDirs(null);
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
            String tempPath = FileUtils.getPathWithoutFilename(new File(FileUtils.getPath(this, data.getData()))).getAbsolutePath();
            workData = new WorkData();
            workData.setWorkingDirectory(tempPath);

            switch (requestCode) {
                case FILE_CHOOSER_FOR_LOAD:
                    workData.loadData();
                    break;
                case FILE_CHOOSER_FOR_NEW:
                    workData.newData();
                    break;
            }

            Intent intent = new Intent(this, WorkSpace.class);
            intent.putExtra("WorkData", workData);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void commandSettingsClicked(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void commandNewProjectClicked(View view) {
        takePath(FILE_CHOOSER_FOR_NEW);
    }

    public void commandOpenProjectClicked(View view) {
        takePath(FILE_CHOOSER_FOR_LOAD);
    }

    public void commandContinueProjectClicked(View view) {
        throw new UnsupportedOperationException();
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

package io.github.sgqy.labeler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;

import io.github.sgqy.labeler.function.WorkData;
import io.github.sgqy.views.EditTextAdapter;
import io.github.sgqy.views.FreeImageView;
import io.github.sgqy.views.MatrixModifiedCallback;

public class WorkSpace extends Activity implements MatrixModifiedCallback {

    WorkData workData = null;
    ArrayList<WorkData.DocumentContent.ContentFile> cfl = null;
    int currentFileIndex = -1;
    ArrayList<EditTextAdapter> edits = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_space);
        workData = (WorkData) getIntent().getSerializableExtra("WorkData");

        ((FreeImageView) findViewById(R.id.baseImageView)).setMatrixModifiedCallback(this);
        cfl = workData.dc.fileItems;
    }

    private void loadItems(ArrayList<WorkData.DocumentContent.ContentFile.ContentItem> items) {
        FrameLayout layout = (FrameLayout) findViewById(R.id.frameLayoutMain);
        for (EditTextAdapter e : edits) {
            layout.removeView(e.getEditText());
        }
        edits = new ArrayList<>();

        for (WorkData.DocumentContent.ContentFile.ContentItem item : items) {
            EditTextAdapter itemEditText = new EditTextAdapter(this);
            itemEditText.setItem(item);
            itemEditText.setRealImagePosition(
                    ((FreeImageView) findViewById(R.id.baseImageView)).getRealImagePositionFromProp(
                            new PointF(item.rX, item.rY)
                    )
            );
            layout.addView(itemEditText.getEditText());
            edits.add(itemEditText);
        }

        Matrix matrix = ((FreeImageView) findViewById(R.id.baseImageView)).getImageMatrix();
        onMatrixModified(matrix);
    }


    private void loadImage() {
        File imgFile = workData.getFullPathByIndex(currentFileIndex);
        if (imgFile.exists()) {
            FreeImageView imageView = (FreeImageView) findViewById(R.id.baseImageView);
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
    }

    public void commandPreviousClicked(View view) {
        if (currentFileIndex > 0) {
            --currentFileIndex;
            loadImage();
            loadItems(cfl.get(currentFileIndex).items);
        } else {
            Toast.makeText(getApplicationContext(), R.string.workspace_toast_first_page, Toast.LENGTH_SHORT).show();
        }
    }

    public void commandNextClicked(View view) {
        if (currentFileIndex < cfl.size() - 1) {
            ++currentFileIndex;
            loadImage();
            loadItems(cfl.get(currentFileIndex).items);
        } else {
            Toast.makeText(getApplicationContext(), R.string.workspace_toast_last_page, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMatrixModified(Matrix matrix) {
        FrameLayout layout = (FrameLayout) findViewById(R.id.frameLayoutMain);
        for (EditTextAdapter e : edits) {
            //layout.removeView(e.getEditText());
            e.setMatrix(matrix);
            //layout.addView(e.getEditText());
        }
    }

    public void imagePointClicked(View view) {
        WorkData.DocumentContent.ContentFile.ContentItem item =
                new WorkData().new DocumentContent().new ContentFile("").new ContentItem();
        PointF pos = ((FreeImageView) view).getPropOfLastTouchedAndRealImageSize();
        item.rX = pos.x;
        item.rY = pos.y;
        cfl.get(currentFileIndex).items.add(item);
        loadItems(cfl.get(currentFileIndex).items);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        try {
            workData.saveData();
        } catch (Exception ec) {

        }
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

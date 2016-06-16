package io.github.sgqy.views;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;

import io.github.sgqy.labeler.R;
import io.github.sgqy.labeler.function.WorkData;
import io.github.sgqy.utils.PointConvert;

/**
 * Created by aya on 2016/6/17.
 */

// Why use Adapter?
// Instantiating a class extending EditText : { ClassNotFoundException : android.view.ViewOutlineProvider }

public class EditTextAdapter {

    EditText editText = null;
    PointF realImagePosition = new PointF();
    private WorkData.DocumentContent.ContentFile.ContentItem item = null;

    public EditTextAdapter(Context context) {
        editText = new EditText(context);
        onCreate();
    }

    public EditText getEditText() {
        return editText;
    }

    private void onCreate() {
        editText.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        editText.setSingleLine(false);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setHint(R.string.workspace_editor_tips);
        editText.setTextSize(9);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    item.content = editText.getText().toString();
                } catch (Exception ec) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                }
        );
    }
//    public void setRealImagePosProp(PointF position) {
//        item.rX = position.x;
//        item.rY = position.y;
//    }

    public void setItem(WorkData.DocumentContent.ContentFile.ContentItem item) {
        this.item = item;
        editText.setText(item.content);
    }

    public void setRealImagePosition(PointF position) {
        realImagePosition = position;
    }

    public void setMatrix(Matrix matrix) {
        try {
            PointF screenPosition = PointConvert.FromRealImageToScreen(realImagePosition, matrix);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) editText.getLayoutParams();
            params.leftMargin = (int) screenPosition.x;
            params.topMargin = (int) screenPosition.y;
            editText.setLayoutParams(params);
        } catch (Exception ec) {

        }
    }
}

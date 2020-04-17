package com.example.multyplay;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

public class MonitoredEditText extends AppCompatEditText {

    private final Context context;
    private final int max_words_allowed = 1;


    public MonitoredEditText(Context context) {
        super(context);
        this.context = context;
    }

    public MonitoredEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public MonitoredEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    /**
     * <p>This is where the "magic" happens.</p>
     * <p>The menu used to cut/copy/paste is a normal ContextMenu, which allows us to
     *  overwrite the consuming method and react on the different events.</p>
     * @see <a href="http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.3_r1/android/widget/TextView.java#TextView.onTextContextMenuItem%28int%29">Original Implementation</a>
     */
    @Override
    public boolean onTextContextMenuItem(int id) {
        // Do your thing:
        boolean consumed = super.onTextContextMenuItem(id);
        // React:
        switch (id){
            case android.R.id.cut:
                onTextCut();
                break;
            case android.R.id.paste:
                onTextPaste();
                break;
            case android.R.id.copy:
                onTextCopy();
        }
        return consumed;
    }

    /**
     * Text was cut from this EditText.
     */
    public void onTextCut(){
        Toast.makeText(context, "Cut!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Text was copied from this EditText.
     */
    public void onTextCopy(){
        Toast.makeText(context, "Copy!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Text was pasted into the EditText.
     */
    public void onTextPaste(){
        if(this.getText() != null) {
            int wordsCount = countWords(this.getText().toString());// words.length;
            if(wordsCount > Constants.MAX_WORDS_FOR_NICKNAME) {
                if(Constants.MAX_WORDS_FOR_NICKNAME == 1) {
                    Toast.makeText(context, "Only 1 word allowed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Only " + Constants.MAX_WORDS_FOR_NICKNAME + " words allowed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //    util methods to limit nickname editText to 1 word
    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }
//
//    private InputFilter filter;
//
//    private void setCharLimit(EditText et, int max) {
//        filter = new InputFilter.LengthFilter(max);
//        et.setFilters(new InputFilter[] { filter });
//    }
//
//    private void removeFilter(EditText et) {
//        if (filter != null) {
//            et.setFilters(new InputFilter[0]);
//            filter = null;
//        }
//    }
}

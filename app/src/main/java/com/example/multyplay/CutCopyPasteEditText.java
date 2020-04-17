package com.example.multyplay;
import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * Original:
 * An EditText, which notifies when something was cut/copied/pasted inside it.
 * @author Lukas Knuth
 * @version 1.0
 *
 * Update:
 * Added a OnCutCopyPasteListener so this class can be used as a plug&play component
 * @author Guillermo Muntaner on 14/01/16.
 *
 * Source & discussion:
 * https://stackoverflow.com/questions/14980227/android-intercept-paste-copy-cut-on-edittext
 */
public class CutCopyPasteEditText extends AppCompatEditText {

    public interface OnCutCopyPasteListener {
        void onCut();
        void onCopy();
        void onPaste();
    }

    private OnCutCopyPasteListener mOnCutCopyPasteListener;

    /**
     * Set a OnCutCopyPasteListener.
     * @param listener
     */
    public void setOnCutCopyPasteListener(OnCutCopyPasteListener listener) {
        mOnCutCopyPasteListener = listener;
    }

    /*
        Just the constructors to create a new EditText...
     */
    public CutCopyPasteEditText(Context context) {
        super(context);
    }

    public CutCopyPasteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CutCopyPasteEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
                onCut();
                break;
            case android.R.id.copy:
                onCopy();
                break;
            case android.R.id.paste:
                onPaste();
        }
        return consumed;
    }

    /**
     * Text was cut from this EditText.
     */
    public void onCut(){
        if(mOnCutCopyPasteListener!=null)
            mOnCutCopyPasteListener.onCut();
    }

    /**
     * Text was copied from this EditText.
     */
    public void onCopy(){
        if(mOnCutCopyPasteListener!=null)
            mOnCutCopyPasteListener.onCopy();
    }

    /**
     * Text was pasted into the EditText.
     */
    public void onPaste(){
        if(mOnCutCopyPasteListener!=null)
            mOnCutCopyPasteListener.onPaste();
        int wordsCount = countWords(this.getText().toString());// words.length;
        if(wordsCount > Constants.MAX_WORDS_FOR_NICKNAME) {
            if (Constants.MAX_WORDS_FOR_NICKNAME == 1) {
                Toast.makeText(this.getContext(), "Only 1 word allowed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getContext(), "Only " + Constants.MAX_WORDS_FOR_NICKNAME + " words allowed", Toast.LENGTH_SHORT).show();
            }
            StringBuilder textToShow = new StringBuilder();
            String[] words = this.getText().toString().split(" ");
            for(int i = 0; i < Constants.MAX_WORDS_FOR_NICKNAME; i++) {
                textToShow.append(words[i]).append(" ");
            }
            this.setText(textToShow.toString().trim());
            this.setSelection(this.length());
        }
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        super.addTextChangedListener(watcher);
    }

    //    util methods to limit nickname editText to 1 word
    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }

}

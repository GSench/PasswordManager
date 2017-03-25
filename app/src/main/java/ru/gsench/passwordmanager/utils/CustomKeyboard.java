package ru.gsench.passwordmanager.utils;

/**
 * Copyright 2013 Maarten Pennings extended by SimplicityApks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 */

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import ru.gsench.passwordmanager.R;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

/**
 * When an activity hosts a keyboardView, this class allows several EditText's to register for it.
 *
 * @author Maarten Pennings, extended by SimplicityApks
 * @date   2012 December 23
 */
public class CustomKeyboard {

    /** A link to the KeyboardView that is used to render this CustomKeyboard. */
    private KeyboardView mKeyboardView;
    /** A link to the activity that hosts the {@link #mKeyboardView}. */
    private Activity mHostActivity;
    private boolean hapticFeedback;
    private boolean caps = false;
    private Keyboard mKeyboard;

    /** The key (code) handler. */
    private OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {

        @Override public void onKey(int primaryCode, int[] keyCodes) {
            // NOTE We can say '<Key android:codes="49,50" ... >' in the xml file; all codes come in keyCodes, the first in this list in primaryCode
            // Get the EditText and its Editable
            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
            if( focusCurrent==null || !(focusCurrent.getClass()==EditText.class || focusCurrent.getClass()==TextInputEditText.class) ) return;
            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();
            int start = edittext.getSelectionStart();
            // delete the selection, if chars are selected:
            int end = edittext.getSelectionEnd();
            if(end>start){
                editable.delete(start, end);
            }
            // Apply the key to the edittext
            switch(primaryCode){
                case Keyboard.KEYCODE_DELETE :
                    if( editable!=null && start>0 ) editable.delete(start - 1, start);
                    break;
                case Keyboard.KEYCODE_SHIFT:
                    caps = !caps;
                    mKeyboard.setShifted(caps);
                    mKeyboardView.invalidateAllKeys();
                    break;
                case Keyboard.KEYCODE_DONE:
                    //ON ENTER PRESSED
                    break;
                default:
                    char code = (char)primaryCode;
                    if(Character.isLetter(code) && caps){
                        code = Character.toUpperCase(code);
                    }
                    editable.insert(start, Character.toString((char) code));
            }
        }

        @Override public void onPress(int arg0) {
            // vibrate if haptic feedback is enabled:
            if(hapticFeedback && arg0!=0)
                mKeyboardView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }

        @Override public void onRelease(int primaryCode) {
        }

        @Override public void onText(CharSequence text) {
        }

        @Override public void swipeDown() {
        }

        @Override public void swipeLeft() {
        }

        @Override public void swipeRight() {
        }

        @Override public void swipeUp() {
        }
    };

    /**
     * Create a custom keyboard, that uses the KeyboardView (with resource id <var>viewid</var>) of the <var>host</var> activity,
     * and load the keyboard layout from xml file <var>layoutid</var> (see {@link Keyboard} for description).
     * Note that the <var>host</var> activity must have a <var>KeyboardView</var> in its layout (typically aligned with the bottom of the activity).
     * Note that the keyboard layout xml file may include key codes for navigation; see the constants in this class for their values.
     * Note that to enable EditText's to use this custom keyboard, call the {@link #registerEditText(EditText, boolean)}.
     *
     * @param host The hosting activity.
     * @param keyboardView KeyboardView.
     */
    public CustomKeyboard(Activity host, KeyboardView keyboardView, boolean keyPreview) {
        mHostActivity = host;
        mKeyboardView = keyboardView;
        mKeyboard = new Keyboard(mHostActivity, R.xml.keyboard);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setPreviewEnabled(keyPreview);
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        // Hide the standard keyboard initially
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /** Returns whether the CustomKeyboard is visible. */
    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /** Make the CustomKeyboard visible, and hide the system keyboard for view v. */
    public void showCustomKeyboard( View v ) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if( v!=null ) ((InputMethodManager)mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        v.requestFocus();
    }

    /** Make the CustomKeyboard invisible. */
    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    /**
     * Register <var>EditText<var> with resource id <var>resid</var> (on the hosting activity) for using this custom keyboard.
     *
     * @param edittext EditText that registers to the custom keyboard.
     * @param enableClipboard true if copy/paste menu should appear
     */
    public void registerEditText(final EditText edittext, final boolean enableClipboard) {
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        //Copy/Paste menu
        final PopupMenu popupMenu = getCopyPasteClipboard(edittext);
        //On long press listener
        final GestureDetector gestureDetector = new GestureDetector(mHostActivity, new MyOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent motionEvent) {
                if(enableClipboard) popupMenu.show();
            }
        });
        /**
         * Try to show cursor the complicated way:
         * @source http://androidpadanam.wordpress.com/2013/05/29/customkeyboard-example/
         * fixes the cursor not movable bug
         */
        OnTouchListener otl = new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showCustomKeyboard(v);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Layout layout = ((EditText) v).getLayout();
                        float x = event.getX() + edittext.getScrollX();
                        int offset = layout.getOffsetForHorizontal(0, x);
                        if (offset > 0)
                            if (x > layout.getLineMax(0))
                                edittext.setSelection(offset);     // touch was at the end of the text
                            else
                                edittext.setSelection(offset - 1);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        layout = ((EditText) v).getLayout();
                        x = event.getX() + edittext.getScrollX();
                        offset = layout.getOffsetForHorizontal(0, x);
                        if (offset > 0)
                            if (x > layout.getLineMax(0))
                                edittext.setSelection(offset);     // Touchpoint was at the end of the text
                            else
                                edittext.setSelection(offset - 1);
                        break;
                }
                gestureDetector.onTouchEvent(event);
                return true;
            }
        };
        edittext.setOnTouchListener(otl);
    }

    /**
     * Enables or disables the Haptic feedback on keyboard touches
     * @param goEnabled true if you want haptic feedback, falso otherwise
     */
    public void enableHapticFeedback(boolean goEnabled){
        mKeyboardView.setHapticFeedbackEnabled(goEnabled);
        hapticFeedback = goEnabled;
    }

    private PopupMenu getCopyPasteClipboard(final EditText editText){
        final int COPY = 0;
        final int PASTE = 1;
        PopupMenu popupMenu = new PopupMenu(mHostActivity, editText);
        popupMenu.getMenu().add(Menu.NONE, COPY, 0, mHostActivity.getString(R.string.copy));
        popupMenu.getMenu().add(Menu.NONE, PASTE, 1, mHostActivity.getString(R.string.paste));
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case COPY:
                        copyText(editText.getText().toString());
                        Toast.makeText(mHostActivity, R.string.copied, Toast.LENGTH_SHORT).show();
                        break;
                    case PASTE:
                        editText.setText(pasteText());
                        editText.setSelection(editText.getText().length());
                        break;
                }
                return false;
            }
        });
        return popupMenu;
    }

    private void copyText(String text){
        ClipboardManager clipboard = (ClipboardManager) mHostActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", text);
        clipboard.setPrimaryClip(clip);
    }

    private String pasteText(){
        ClipboardManager clipboard = (ClipboardManager) mHostActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        if(clipboard.hasPrimaryClip()&&clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))
            return clipboard.getPrimaryClip().getItemAt(0).getText().toString();
        else return "";
    }

}
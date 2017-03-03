package utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by grish on 04.03.2017.
 */

public class IntentUtils {

    public static void copyText(String text, Context context){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", text);
        clipboard.setPrimaryClip(clip);
    }

    public static void openURL(String url, Context context){
        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    public static boolean isStringUrl(String string){
        Pattern p = Pattern.compile("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$");
        Matcher m = p.matcher(string);
        return m.find();
    }

    public static String toURL(String str){
        return str.startsWith("http") ? str : "http://"+str;
    }

}

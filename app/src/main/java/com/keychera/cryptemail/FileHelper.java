package com.keychera.cryptemail;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class FileHelper {
  public final static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cryptemail/" ;
  private final static String TAG = FileHelper.class.getName();

  public static void CallFilePicker(Activity activity, int requestCode, String ext) {
    MaterialFilePicker picker = new MaterialFilePicker()
        .withActivity(activity)
        .withRequestCode(requestCode)
        .withFilterDirectories(true)
        .withHiddenFiles(false);
    if (ext != null) {
      picker.withFilter(Pattern.compile(".*\\." + ext + "$"));
    }
    picker.start();
  }

  static  String ReadFile(String fileName, Context context){
    String line = null;

    try {
      if (ContextCompat.checkSelfPermission(context, permission.READ_EXTERNAL_STORAGE)
          != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(context, "Needs Storage Permission", Toast.LENGTH_LONG).show();
      }
      FileInputStream fileInputStream = new FileInputStream(new File(fileName));
      InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      StringBuilder stringBuilder = new StringBuilder();

      while ( (line = bufferedReader.readLine()) != null )
      {
        stringBuilder.append(line).append(System.getProperty("line.separator"));
      }
      fileInputStream.close();
      line = stringBuilder.toString();

      bufferedReader.close();
    }
    catch(FileNotFoundException ex) {
      Log.e(TAG, ex.getMessage());
    }
    catch(IOException ex) {
      Log.e(TAG, ex.getMessage());
    }
    return line;
  }

  public static boolean saveToFile(String fileName, String data){
    try {
      new File(path).mkdir();
      File file = new File(path+ fileName);
      if (!file.exists()) {
        file.createNewFile();
      }
      FileOutputStream fileOutputStream = new FileOutputStream(file,true);
      fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());

      return true;
    }  catch(FileNotFoundException ex) {
      Log.d(TAG, ex.getMessage());
    }  catch(IOException ex) {
      Log.d(TAG, ex.getMessage());
    }
    return  false;


  }
}

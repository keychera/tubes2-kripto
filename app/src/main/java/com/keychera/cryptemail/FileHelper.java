package com.keychera.cryptemail;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import java.io.BufferedInputStream;
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
  // Storage Permissions
  public static final int REQUEST_EXTERNAL_STORAGE = 1;
  private static String[] PERMISSIONS_STORAGE = {
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
  };

  public static void verifyStoragePermissions(Activity activity) {
    // Check if we have write permission
    int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    //if (permission != PackageManager.PERMISSION_GRANTED) {
      // We don't have permission so prompt the user
      ActivityCompat.requestPermissions(
          activity,
          PERMISSIONS_STORAGE,
          REQUEST_EXTERNAL_STORAGE
      );
    //}
  }

  static void CallFilePicker(Activity activity, int requestCode, String ext) {
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

  static byte[] ReadFileBytes(String fileName, Context context) {
    File file = new File(fileName);
    int size = (int) file.length();
    byte[] bytes = new byte[size];
    try {
      BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
      buf.read(bytes, 0, bytes.length);
      buf.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bytes;
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

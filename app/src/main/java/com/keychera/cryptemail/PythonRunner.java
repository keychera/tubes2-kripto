package com.keychera.cryptemail;

import android.content.Context;
import com.chaquo.python.PyInvocationHandler;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

class PythonRunner {
  PythonRunner(Context context) {

    //test python
    if (!Python.isStarted()) {
      Python.start(new AndroidPlatform(context));
    }
    Python os = Python.getInstance();
    PyObject builtins = os.getBuiltins();
    PyObject bcmachine_module = os.getModule("BlockCipher.BlockCipherMachine");
    PyObject bcmode_module = os.getModule("BlockCipher.BlockCipherModes");
    PyObject bccryptor_module = os.getModule("BlockCipher.BlockCipherCryptor");

    String key = "namasaya";
    PyObject ECB_mode = bcmode_module.callAttr("ECBMode");
    PyObject cryptor = bccryptor_module.callAttr("Cipher",(Object)key.getBytes());
    PyObject bcmachine = bcmachine_module.callAttr("BlockCipherMachine", ECB_mode, cryptor);

    System.out.println(bcmachine.containsKey("set_block_list"));

    String plain = "bob";
    PyObject py_str = builtins.get("str");
    PyObject plain_pybytes = py_str.callAttr("encode",plain);

    bcmachine.callAttr("set_block_list", plain_pybytes, 8);
    bcmachine.callAttr("run", true);

    PyObject res = bcmachine.get("result_list");

    if (res != null) {
      System.out.println("result is" + res.toString());
    } else {
      System.out.println("is null");
    }

  }
}

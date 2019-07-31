package com.keychera.cryptemail;

import android.util.Base64;

public class SimpleSignedData {
  public byte[] data;
  public byte[] signature;

  SimpleSignedData(byte[] data, byte[] signature) {
    this.data = data;
    this.signature = signature;
  }

  public String getSignatureString() {
    return Base64.encodeToString(signature, Base64.DEFAULT);
  }
}

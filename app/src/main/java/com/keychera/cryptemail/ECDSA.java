package com.keychera.cryptemail;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.io.IOUtils;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECParameterSpec;

public class ECDSA {
  private KeyPairGenerator gen;
  private Provider provider;

  ECDSA() {
    try {
      ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("prime192v1");
      provider = new org.spongycastle.jce.provider.BouncyCastleProvider();
      Security.addProvider(provider);
      gen = KeyPairGenerator.getInstance("ECDSA", "SC");
      gen.initialize(ecSpec, new SecureRandom());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void saveKeyPair(String filename) throws IOException {
    KeyPair pair = gen.generateKeyPair();

    FileOutputStream output;

    PrivateKey privateKey = pair.getPrivate();
    byte[] encodedPv = privateKey.getEncoded();
    output = new FileOutputStream(new File(FileHelper.path + "private-" + filename + ".key"));
    IOUtils.write(encodedPv, output);

    PublicKey publicKey = pair.getPublic();
    byte[] encodedPb = publicKey.getEncoded();
    output = new FileOutputStream(new File(FileHelper.path + "public-" + filename + ".key"));
    IOUtils.write(encodedPb, output);
  }

  public SimpleSignedData signData(byte[] dataToSign,  byte[] encodedPv) {
    PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encodedPv);
    try {
      KeyFactory kf = KeyFactory.getInstance("ECDSA", provider);
      PrivateKey savedPrivateKey = kf.generatePrivate(privKeySpec);

      Signature sig = Signature.getInstance("ECDSA", "BC");
      sig.initSign(savedPrivateKey);
      sig.update(dataToSign);

      byte[] signatureBytes = sig.sign();
      return new SimpleSignedData(dataToSign, signatureBytes);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public boolean verify(byte[] dataToVerify, byte[] sigToVerify, byte[] encodedPb) {
    X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encodedPb);

    try {
      KeyFactory kf = KeyFactory.getInstance("ECDSA", provider);
      PublicKey savedPublicKey  = kf.generatePublic(pubKeySpec);

      Signature sig = Signature.getInstance("ECDSA", "BC");
      sig.initVerify(savedPublicKey);
      sig.update(dataToVerify);
      return sig.verify(sigToVerify);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

}
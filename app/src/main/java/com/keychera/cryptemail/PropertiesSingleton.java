package com.keychera.cryptemail;

import java.util.List;

public class PropertiesSingleton {
  private static PropertiesSingleton mInstance= null;

  public List<SimpleEmail> emails;

  protected PropertiesSingleton(){}

  public static synchronized PropertiesSingleton getInstance() {
    if(null == mInstance){
      mInstance = new PropertiesSingleton();
    }
    return mInstance;
  }
}

package com.keychera.cryptemail;

import java.util.ArrayList;
import java.util.List;

public class PropertiesSingleton {
  private static PropertiesSingleton mInstance= null;
  private static List<PropertyListener> listeners;

  public List<SimpleEmail> emails;
  public String sharedString;
  public int sharedInt;

  protected PropertiesSingleton(){}

  public static synchronized PropertiesSingleton getInstance() {
    if(null == mInstance){
      mInstance = new PropertiesSingleton();
      listeners = new ArrayList<PropertyListener>();
    }
    return mInstance;
  }

  public void subscribe(PropertyListener listener) {
    listeners.add(listener);
  }

  public void unsubscribe(PropertyListener listener) {
    listeners.remove(listener);
  }

  public void clearSharedData() {
    sharedString = null;
    sharedInt = 0;
  }

  public void notifyListener() {
    for (int i = 0; i < listeners.size();i++) {
      listeners.get(i).OnPropertyChanged();
    }
  }

  public interface PropertyListener {

    void OnPropertyChanged();
  }
}

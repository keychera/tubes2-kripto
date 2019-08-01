Sourcecode project untuk tugas IF4020 Kriptografi 2018/2019

Kevin Erdiza Yogatama - 13515016

karena aplikasi tidak menghandle login, untuk mengkompilasi project, harus membuat file `Config.java` di folder `\app\src\main\java\com\keychera\cryptemail\` dan membuat kelas `Config` seperti berikut

``` java
package com.keychera.cryptemail;

public class Config {
  private static Config mInstance = null;

  public String email;
  public String password ;

  protected Config(){}

  public static synchronized Config getInstance() {
    if(null == mInstance){
      mInstance = new Config();
      //if you want email initialized
      mInstance.email = "string@your.email";
      mInstance.password ="yourPassword";
    }
    return mInstance;
  }
}
```
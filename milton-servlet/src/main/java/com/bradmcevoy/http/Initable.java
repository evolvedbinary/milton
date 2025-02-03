package com.bradmcevoy.http;

public interface Initable {
   void init(ApplicationConfig var1, HttpManager var2);

   void destroy(HttpManager var1);
}

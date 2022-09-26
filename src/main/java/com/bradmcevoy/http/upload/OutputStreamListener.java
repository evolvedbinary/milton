package com.bradmcevoy.http.upload;

public interface OutputStreamListener {
   void start();

   void bytesRead(int var1);

   void error(String var1);

   void done();
}

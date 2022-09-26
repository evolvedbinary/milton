package com.bradmcevoy.http.upload;

import java.io.IOException;
import java.io.OutputStream;

public class MonitoredOutputStream extends OutputStream {
   private OutputStream target;
   private OutputStreamListener listener;

   public MonitoredOutputStream(OutputStream target, OutputStreamListener listener) {
      this.target = target;
      this.listener = listener;
      this.listener.start();
   }

   public void write(byte[] b, int off, int len) throws IOException {
      this.target.write(b, off, len);
      this.listener.bytesRead(len - off);
   }

   public void write(byte[] b) throws IOException {
      this.target.write(b);
      this.listener.bytesRead(b.length);
   }

   public void write(int b) throws IOException {
      this.target.write(b);
      this.listener.bytesRead(1);
   }

   public void close() throws IOException {
      this.target.close();
      this.listener.done();
   }

   public void flush() throws IOException {
      this.target.flush();
   }
}

package com.bradmcevoy.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileItemWrapper implements FileItem {
   final org.apache.commons.fileupload.FileItem wrapped;
   final String name;

   public static String fixIEFileName(String s) {
      if (s.contains("\\")) {
         int pos = s.lastIndexOf(92);
         s = s.substring(pos + 1);
      }

      return s;
   }

   public FileItemWrapper(org.apache.commons.fileupload.FileItem wrapped) {
      this.wrapped = wrapped;
      this.name = fixIEFileName(wrapped.getName());
   }

   public String getContentType() {
      return this.wrapped.getContentType();
   }

   public String getFieldName() {
      return this.wrapped.getFieldName();
   }

   public InputStream getInputStream() {
      try {
         return this.wrapped.getInputStream();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public OutputStream getOutputStream() {
      try {
         return this.wrapped.getOutputStream();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public String getName() {
      return this.name;
   }

   public long getSize() {
      return this.wrapped.getSize();
   }
}

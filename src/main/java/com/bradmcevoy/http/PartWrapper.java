package com.bradmcevoy.http;

import jakarta.servlet.http.Part;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class PartWrapper implements FileItem {
   final Part wrapped;
   final String submittedFileName;

   Path tempFile = null;

   public static String fixIEFileName(String s) {
      if (s.contains("\\")) {
         int pos = s.lastIndexOf(92);
         s = s.substring(pos + 1);
      }

      return s;
   }

   public PartWrapper(final Part wrapped) {
      this.wrapped = wrapped;
      this.submittedFileName = fixIEFileName(wrapped.getSubmittedFileName());
   }

   @Override
   public String getContentType() {
      return this.wrapped.getContentType();
   }

   @Override
   public String getFieldName() {
      return this.wrapped.getName();
   }

   @Override
   public InputStream getInputStream() {
      try {
         if (tempFile != null) {
            return Files.newInputStream(tempFile);
         }
         return this.wrapped.getInputStream();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   @Override
   public OutputStream getOutputStream() {
      try {
         if (tempFile != null) {
            this.tempFile = Files.createTempFile(null, null);
         }
         return Files.newOutputStream(tempFile);
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   @Override
   public String getName() {
      return this.submittedFileName;
   }

   @Override
   public long getSize() {
      return this.wrapped.getSize();
   }
}

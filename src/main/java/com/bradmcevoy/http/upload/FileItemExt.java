package com.bradmcevoy.http.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.activation.DataSource;
import org.apache.commons.fileupload.FileItem;

public class FileItemExt implements DataSource, FileItem {
   private static final long serialVersionUID = 1L;
   private final FileItem item;

   public FileItemExt(FileItem item) {
      this.item = item;
   }

   public InputStream getInputStream() throws IOException {
      return this.item.getInputStream();
   }

   public OutputStream getOutputStream() throws IOException {
      return this.item.getOutputStream();
   }

   public String getContentType() {
      return this.item.getContentType();
   }

   public String getName() {
      return this.item.getName();
   }

   public boolean isInMemory() {
      return this.item.isInMemory();
   }

   public long getSize() {
      return this.item.getSize();
   }

   public byte[] get() {
      return this.item.get();
   }

   public String getString(String string) throws UnsupportedEncodingException {
      return this.item.getString(string);
   }

   public String getString() {
      return this.item.getString();
   }

   public void write(File file) throws Exception {
      this.item.write(file);
   }

   public void delete() {
      this.item.delete();
   }

   public String getFieldName() {
      return this.item.getFieldName();
   }

   public void setFieldName(String string) {
      this.item.setFieldName(string);
   }

   public boolean isFormField() {
      return this.item.isFormField();
   }

   public void setFormField(boolean b) {
      this.item.setFormField(b);
   }
}

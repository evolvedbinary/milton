package com.bradmcevoy.http;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class StaticResource implements GetableResource {
   private final File file;
   private String contentType;

   public StaticResource(File file, String url, String contentType) {
      if (file.isDirectory()) {
         throw new IllegalArgumentException("Static resource must be a file, this is a directory: " + file.getAbsolutePath());
      } else {
         this.file = file;
         this.contentType = contentType;
      }
   }

   public String getUniqueId() {
      return this.file.hashCode() + "";
   }

   public int compareTo(Resource res) {
      return this.getName().compareTo(res.getName());
   }

   public void sendContent(OutputStream out, Range range, Map params, String contentType) throws IOException {
      FileInputStream fis = new FileInputStream(this.file);
      BufferedInputStream bin = new BufferedInputStream(fis);
      byte[] buffer = new byte[1024];

      int n;
      while(-1 != (n = bin.read(buffer))) {
         out.write(buffer, 0, n);
      }

   }

   public String getName() {
      return this.file.getName();
   }

   public Object authenticate(String user, String password) {
      return "ok";
   }

   public boolean authorise(Request request, Request.Method method, Auth auth) {
      return true;
   }

   public String getRealm() {
      return "ettrema";
   }

   public Date getModifiedDate() {
      Date dt = new Date(this.file.lastModified());
      return dt;
   }

   public Long getContentLength() {
      return this.file.length();
   }

   public String getContentType(String preferredList) {
      Collection mimeTypes = MimeUtil.getMimeTypes(this.file);
      StringBuilder sb = null;

      MimeType mt;
      for(Iterator i$ = mimeTypes.iterator(); i$.hasNext(); sb.append(mt.toString())) {
         Object o = i$.next();
         mt = (MimeType)o;
         if (sb == null) {
            sb = new StringBuilder();
         } else {
            sb.append(",");
         }
      }

      if (sb == null) {
         return null;
      } else {
         String mime = sb.toString();
         mt = MimeUtil.getPreferedMimeType(preferredList, mime);
         return mt.toString();
      }
   }

   public String checkRedirect(Request request) {
      return null;
   }

   public Long getMaxAgeSeconds(Auth auth) {
      Long ll = 315360000L;
      return ll;
   }

   public LockToken getLockToken() {
      return null;
   }
}

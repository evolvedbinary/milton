package com.bradmcevoy.http;

import com.bradmcevoy.common.ContentTypeUtils;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletResource implements GetableResource {
   private final String localPath;
   private final File file;
   private final String name;
   private final HttpServletRequest req;
   private final HttpServletResponse response;

   public ServletResource(File file, String localPath, HttpServletRequest req, HttpServletResponse response) {
      this.file = file;
      this.name = file.getName();
      this.localPath = localPath;
      this.req = req;
      this.response = response;
   }

   public ServletResource(String localPath, HttpServletRequest req, HttpServletResponse response) {
      this.file = null;
      this.name = localPath.substring(localPath.lastIndexOf("/"));
      this.localPath = localPath;
      this.req = req;
      this.response = response;
   }

   public String getUniqueId() {
      return null;
   }

   public int compareTo(Resource res) {
      return this.getName().compareTo(res.getName());
   }

   public void sendContent(OutputStream out, Range range, Map params, String contentType) throws IOException {
      try {
         MyResponse myResponse = new MyResponse(HttpManager.response(), out);
         this.req.getRequestDispatcher(this.localPath).include(this.req, myResponse);
      } catch (ServletException var6) {
         throw new RuntimeException(var6);
      }
   }

   public String getName() {
      return this.name;
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
      if (this.file != null) {
         Date dt = new Date(this.file.lastModified());
         return dt;
      } else {
         return null;
      }
   }

   public Long getContentLength() {
      return null;
   }

   public String getContentType(String preferredList) {
      return this.file != null ? ContentTypeUtils.findContentTypes(this.file) : ContentTypeUtils.findContentTypes(this.name);
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

   private class MyResponse extends ServletOutputStream implements HttpServletResponse {
      private final Response response;
      private final OutputStream out;

      public MyResponse(Response response, OutputStream out) {
         this.response = response;
         this.out = out;
      }

      public void addCookie(Cookie cookie) {
         this.response.setCookie(new ServletCookie(cookie));
      }

      public boolean containsHeader(String name) {
         return this.response.getHeaders().containsKey(name);
      }

      public String encodeURL(String url) {
         return MiltonServlet.response().encodeURL(url);
      }

      public String encodeRedirectURL(String url) {
         return MiltonServlet.response().encodeRedirectURL(url);
      }

      public String encodeUrl(String url) {
         return MiltonServlet.response().encodeUrl(url);
      }

      public String encodeRedirectUrl(String url) {
         return MiltonServlet.response().encodeRedirectUrl(url);
      }

      public void sendError(int sc, String msg) throws IOException {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void sendError(int sc) throws IOException {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void sendRedirect(String location) throws IOException {
         MiltonServlet.response().sendRedirect(location);
      }

      public void setDateHeader(String name, long date) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void addDateHeader(String name, long date) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void setHeader(String name, String value) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void addHeader(String name, String value) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void setIntHeader(String name, int value) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void addIntHeader(String name, int value) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void setStatus(int sc) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void setStatus(int sc, String sm) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public String getCharacterEncoding() {
         return MiltonServlet.response().getCharacterEncoding();
      }

      public ServletOutputStream getOutputStream() throws IOException {
         return this;
      }

      public PrintWriter getWriter() throws IOException {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void setContentLength(int len) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void setContentType(String type) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void setBufferSize(int size) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public int getBufferSize() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void flushBuffer() throws IOException {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void resetBuffer() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public boolean isCommitted() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void reset() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void setLocale(Locale loc) {
         MiltonServlet.response().setLocale(loc);
      }

      public Locale getLocale() {
         return MiltonServlet.response().getLocale();
      }

      public void write(int b) throws IOException {
         this.out.write(b);
      }

      public void write(byte[] b) throws IOException {
         this.out.write(b);
      }

      public void write(byte[] b, int off, int len) throws IOException {
         this.out.write(b, off, len);
      }
   }
}

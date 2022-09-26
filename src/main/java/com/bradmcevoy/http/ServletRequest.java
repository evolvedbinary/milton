package com.bradmcevoy.http;

import com.bradmcevoy.http.Request.Header;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.Response.ContentType;
import com.bradmcevoy.http.upload.MonitoredDiskFileItemFactory;
import com.bradmcevoy.http.upload.UploadListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletRequest extends AbstractRequest {
   private static final Logger log = LoggerFactory.getLogger(ServletRequest.class);
   private final HttpServletRequest request;
   private final ServletContext servletContext;
   private final Request.Method method;
   private final String url;
   private Auth auth;
   private static final Map contentTypes = new EnumMap(Response.ContentType.class);
   private static final Map typeContents = new HashMap();
   private static ThreadLocal tlRequest;
   private static ThreadLocal tlServletContext;

   public static HttpServletRequest getRequest() {
      return (HttpServletRequest)tlRequest.get();
   }

   public static ServletContext getTLServletContext() {
      return (ServletContext)tlServletContext.get();
   }

   static void clearThreadLocals() {
      tlRequest.remove();
      tlServletContext.remove();
   }

   public ServletRequest(HttpServletRequest r, ServletContext servletContext) {
      this.request = r;
      this.servletContext = servletContext;
      String sMethod = r.getMethod();
      this.method = Method.valueOf(sMethod);
      String s = r.getRequestURL().toString();
      this.url = s;
      tlRequest.set(r);
      tlServletContext.set(servletContext);
   }

   public HttpSession getSession() {
      return this.request.getSession();
   }

   public String getFromAddress() {
      return this.request.getRemoteHost();
   }

   public String getRequestHeader(Request.Header header) {
      return this.request.getHeader(header.code);
   }

   public Request.Method getMethod() {
      return this.method;
   }

   public String getAbsoluteUrl() {
      return this.url;
   }

   public Auth getAuthorization() {
      if (this.auth != null) {
         log.trace("using cached auth object");
         return this.auth;
      } else {
         String enc = this.getRequestHeader(Header.AUTHORIZATION);
         if (enc == null) {
            return null;
         } else if (enc.length() == 0) {
            log.trace("authorization header is not-null, but is empty");
            return null;
         } else {
            this.auth = new Auth(enc);
            if (log.isTraceEnabled()) {
               log.trace("creating new auth object {}", this.auth.getScheme());
            }

            return this.auth;
         }
      }
   }

   public void setAuthorization(Auth auth) {
      this.auth = auth;
   }

   public InputStream getInputStream() throws IOException {
      return this.request.getInputStream();
   }

   public void parseRequestParameters(Map params, Map files) throws RequestParseException {
      try {
         if (this.isMultiPart()) {
            log.trace("parseRequestParameters: isMultiPart");
            UploadListener listener = new UploadListener();
            MonitoredDiskFileItemFactory factory = new MonitoredDiskFileItemFactory(listener);
            ServletFileUpload upload = new ServletFileUpload(factory);
            List items = upload.parseRequest(this.request);
            this.parseQueryString(params);
            Iterator i$ = items.iterator();

            while(true) {
               while(i$.hasNext()) {
                  Object o = i$.next();
                  FileItem item = (FileItem)o;
                  if (item.isFormField()) {
                     params.put(item.getFieldName(), item.getString());
                  } else {
                     String itemKey = item.getFieldName();
                     if (files.containsKey(itemKey)) {
                        int count;
                        for(count = 1; files.containsKey(itemKey + count); ++count) {
                        }

                        itemKey = itemKey + count;
                     }

                     files.put(itemKey, new FileItemWrapper(item));
                  }
               }

               return;
            }
         } else {
            Enumeration en = this.request.getParameterNames();

            while(en.hasMoreElements()) {
               String nm = (String)en.nextElement();
               String val = this.request.getParameter(nm);
               params.put(nm, val);
            }

         }
      } catch (FileUploadException var12) {
         throw new RequestParseException("FileUploadException", var12);
      } catch (Throwable var13) {
         throw new RequestParseException(var13.getMessage(), var13);
      }
   }

   private void parseQueryString(Map map) {
      String qs = this.request.getQueryString();
      parseQueryString(map, qs);
   }

   public static void parseQueryString(Map map, String qs) {
      if (qs != null) {
         String[] nvs = qs.split("&");
         String[] arr$ = nvs;
         int len$ = nvs.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String nv = arr$[i$];
            String[] parts = nv.split("=");
            String key = parts[0];
            String val = null;
            if (parts.length > 1) {
               val = parts[1];
            }

            if (val != null) {
               try {
                  val = URLDecoder.decode(val, "UTF-8");
               } catch (UnsupportedEncodingException var11) {
                  throw new RuntimeException(var11);
               }
            }

            map.put(key, val);
         }

      }
   }

   protected Response.ContentType getRequestContentType() {
      String s = this.request.getContentType();
      log.trace("request content type", s);
      if (s == null) {
         return null;
      } else {
         return s.contains("multipart/form-data") ? ContentType.MULTIPART : (Response.ContentType)typeContents.get(s);
      }
   }

   protected boolean isMultiPart() {
      Response.ContentType ct = this.getRequestContentType();
      log.trace("content type:", ct);
      return ContentType.MULTIPART.equals(ct);
   }

   public Map getHeaders() {
      Map map = new HashMap();
      Enumeration num = this.request.getHeaderNames();

      while(num.hasMoreElements()) {
         String name = (String)num.nextElement();
         String val = this.request.getHeader(name);
         map.put(name, val);
      }

      return map;
   }

   public Cookie getCookie(String name) {
      if (this.request.getCookies() != null) {
         javax.servlet.http.Cookie[] arr$ = this.request.getCookies();
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            javax.servlet.http.Cookie c = arr$[i$];
            if (c.getName().equals(name)) {
               return new ServletCookie(c);
            }
         }
      }

      return null;
   }

   public List getCookies() {
      ArrayList list = new ArrayList();
      if (this.request.getCookies() != null) {
         javax.servlet.http.Cookie[] arr$ = this.request.getCookies();
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            javax.servlet.http.Cookie c = arr$[i$];
            list.add(new ServletCookie(c));
         }
      }

      return list;
   }

   public String getRemoteAddr() {
      return this.request.getRemoteAddr();
   }

   public ServletContext getServletContext() {
      return this.servletContext;
   }

   static {
      contentTypes.put(ContentType.HTTP, "text/html");
      contentTypes.put(ContentType.MULTIPART, "multipart/form-data");
      contentTypes.put(ContentType.XML, "text/xml; charset=UTF-8");
      Iterator i$ = contentTypes.keySet().iterator();

      while(i$.hasNext()) {
         Response.ContentType key = (Response.ContentType)i$.next();
         typeContents.put(contentTypes.get(key), key);
      }

      tlRequest = new ThreadLocal();
      tlServletContext = new ThreadLocal();
   }
}

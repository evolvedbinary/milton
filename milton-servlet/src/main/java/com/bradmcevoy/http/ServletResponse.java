package com.bradmcevoy.http;

import com.bradmcevoy.http.Response.Header;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletResponse extends AbstractResponse {
   private static final Logger log = LoggerFactory.getLogger(ServletResponse.class);
   private static ThreadLocal tlResponse = new ThreadLocal();
   private final HttpServletResponse r;
   private Response.Status status;
   private Map headers = new HashMap();

   public static HttpServletResponse getResponse() {
      return (HttpServletResponse)tlResponse.get();
   }

   public ServletResponse(HttpServletResponse r) {
      this.r = r;
      tlResponse.set(r);
   }

   protected void setAnyDateHeader(Response.Header name, Date date) {
      if (date == null) {
         this.r.setHeader(name.code, "");
      } else {
         this.r.setDateHeader(name.code, date.getTime());
      }

   }

   public String getNonStandardHeader(String code) {
      return (String)this.headers.get(code);
   }

   public void setNonStandardHeader(String name, String value) {
      this.r.addHeader(name, value);
      this.headers.put(name, value);
   }

   public void setStatus(Response.Status status) {
      if (status.text == null) {
         this.r.setStatus(status.code);
      } else {
         this.r.setStatus(status.code);
      }

      this.status = status;
   }

   public Response.Status getStatus() {
      return this.status;
   }

   public OutputStream getOutputStream() {
      try {
         return this.r.getOutputStream();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public void close() {
      try {
         this.r.flushBuffer();
      } catch (Throwable var2) {
         log.trace("exception closing and flushing", var2);
      }

   }

   public void sendRedirect(String url) {
      String u = this.r.encodeRedirectURL(url);

      try {
         this.r.sendRedirect(u);
      } catch (IOException var4) {
         log.warn("exception sending redirect", var4);
      }

   }

   public Map getHeaders() {
      return Collections.unmodifiableMap(this.headers);
   }

   public void setAuthenticateHeader(List challenges) {
      Iterator i$ = challenges.iterator();

      while(i$.hasNext()) {
         String ch = (String)i$.next();
         this.r.addHeader(Header.WWW_AUTHENTICATE.code, ch);
      }

   }

   public Cookie setCookie(Cookie cookie) {
      if (cookie instanceof ServletCookie) {
         ServletCookie sc = (ServletCookie)cookie;
         this.r.addCookie(sc.getWrappedCookie());
         return cookie;
      } else {
         jakarta.servlet.http.Cookie c = new jakarta.servlet.http.Cookie(cookie.getName(), cookie.getValue());
         c.setDomain(cookie.getDomain());
         c.setMaxAge(cookie.getExpiry());
         c.setPath(cookie.getPath());
         c.setSecure(cookie.getSecure());
         c.setVersion(cookie.getVersion());
         this.r.addCookie(c);
         return new ServletCookie(c);
      }
   }

   public Cookie setCookie(String name, String value) {
      jakarta.servlet.http.Cookie c = new jakarta.servlet.http.Cookie(name, value);
      c.setPath("/");
      this.r.addCookie(c);
      return new ServletCookie(c);
   }
}

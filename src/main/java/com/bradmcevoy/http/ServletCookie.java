package com.bradmcevoy.http;

public class ServletCookie implements Cookie {
   private final jakarta.servlet.http.Cookie cookie;

   public ServletCookie(jakarta.servlet.http.Cookie cookie) {
      this.cookie = cookie;
   }

   public jakarta.servlet.http.Cookie getWrappedCookie() {
      return this.cookie;
   }

   public int getVersion() {
      return this.cookie.getVersion();
   }

   public void setVersion(int version) {
      this.cookie.setVersion(version);
   }

   public String getName() {
      return this.cookie.getName();
   }

   public String getValue() {
      return this.cookie.getValue();
   }

   public void setValue(String value) {
      this.cookie.setValue(value);
   }

   public boolean getSecure() {
      return this.cookie.getSecure();
   }

   public void setSecure(boolean secure) {
      this.cookie.setSecure(secure);
   }

   public int getExpiry() {
      return this.cookie.getMaxAge();
   }

   public void setExpiry(int expiry) {
      this.cookie.setMaxAge(expiry);
   }

   public String getPath() {
      return this.cookie.getPath();
   }

   public void setPath(String path) {
      this.cookie.setPath(path);
   }

   public String getDomain() {
      return this.cookie.getDomain();
   }

   public void setDomain(String domain) {
      this.cookie.setDomain(domain);
   }
}

package com.bradmcevoy.http;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

public class MiltonUtils {
   public static String stripContext(HttpServletRequest req) {
      String s = req.getRequestURI();
      String contextPath = req.getContextPath();
      s = s.replaceFirst(contextPath, "");
      return s;
   }

   public static String getContentType(ServletContext context, String fileName) {
      return context.getMimeType(fileName);
   }
}

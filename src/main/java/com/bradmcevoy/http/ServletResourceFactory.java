package com.bradmcevoy.http;

import java.io.File;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ServletResourceFactory implements ResourceFactory {
   private final ServletContext servletContext;

   @Autowired
   public ServletResourceFactory(ServletContext servletContext) {
      this.servletContext = servletContext;
   }

   public Resource getResource(String host, String path) {
      String contextPath = MiltonServlet.request().getContextPath();
      String localPath = path.substring(contextPath.length());
      String realPath = this.servletContext.getRealPath(localPath);
      if (realPath != null) {
         File file = new File(realPath);
         return file.exists() && !file.isDirectory() ? new ServletResource(file, localPath, MiltonServlet.request(), MiltonServlet.response()) : null;
      } else {
         return null;
      }
   }
}

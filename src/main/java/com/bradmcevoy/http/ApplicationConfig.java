package com.bradmcevoy.http;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class ApplicationConfig {
   final FilterConfig config;
   final ServletConfig servletConfig;
   final ServletContext servletContext;
   final List parameterNames = new ArrayList();

   public ApplicationConfig() {
      this.config = null;
      this.servletConfig = null;
      this.servletContext = null;
   }

   public ApplicationConfig(FilterConfig config) {
      this.config = config;
      this.servletConfig = null;
      this.servletContext = config.getServletContext();
      if (config != null) {
         Enumeration en = config.getInitParameterNames();

         while(en.hasMoreElements()) {
            this.parameterNames.add((String)en.nextElement());
         }

      }
   }

   public ApplicationConfig(ServletConfig config) {
      this.config = null;
      this.servletConfig = config;
      this.servletContext = this.servletConfig.getServletContext();
      if (config != null) {
         Enumeration en = config.getInitParameterNames();

         while(en.hasMoreElements()) {
            this.parameterNames.add((String)en.nextElement());
         }

      }
   }

   public String getFilterName() {
      return this.servletConfig != null ? this.servletConfig.getServletName() : this.config.getFilterName();
   }

   public String getContextName() {
      return this.servletContext.getServletContextName();
   }

   public String getInitParameter(String string) {
      return this.servletConfig != null ? this.servletConfig.getInitParameter(string) : this.config.getInitParameter(string);
   }

   public Collection getInitParameterNames() {
      return this.parameterNames;
   }

   public File getConfigFile(String path) {
      File f = new File(this.getWebInfDir(), path);
      return f;
   }

   public File getWebInfDir() {
      String s = this.servletContext.getRealPath("WEB-INF/");
      File f = new File(s);
      return f;
   }

   public File getRootFolder() {
      String s = this.servletContext.getRealPath("/");
      File f = new File(s);
      return f;
   }

   public File mapPath(String url) {
      String pth = this.servletContext.getRealPath(url);
      File file = new File(pth);
      return file;
   }
}

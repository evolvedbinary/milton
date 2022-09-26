package com.bradmcevoy.http;

import com.bradmcevoy.common.ContentTypeUtils;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceFactory implements ResourceFactory, Initable {
   private static final Logger log = LoggerFactory.getLogger(StaticResourceFactory.class);
   private ApplicationConfig config;
   private File root;
   private String contextPath;

   public StaticResourceFactory() {
   }

   public StaticResourceFactory(ApplicationConfig config) {
      this.config = config;
   }

   public StaticResourceFactory(String context, File root) {
      this.root = root;
      this.contextPath = context;
      log.debug("root: " + root.getAbsolutePath() + " - context:" + context);
   }

   public void init(ApplicationConfig config, HttpManager manager) {
      this.config = config;
   }

   public Resource getResource(String host, String url) {
      File file;
      String contentType;
      if (this.root != null) {
         log.debug("url: " + url);
         contentType = this.stripContext(url);
         log.debug("url: " + contentType);
         file = new File(this.root, contentType);
      } else {
         if (this.config == null) {
            throw new RuntimeException("ResourceFactory was not configured. ApplicationConfig is null");
         }

         if (this.config.servletContext == null) {
            throw new NullPointerException("config.servletContext is null");
         }

         contentType = "WEB-INF/static" + url;
         contentType = this.config.servletContext.getRealPath(contentType);
         file = new File(contentType);
      }

      if (file.exists() && !file.isDirectory()) {
         if (this.config != null) {
            contentType = MiltonUtils.getContentType(this.config.servletContext, file.getName());
         } else {
            contentType = ContentTypeUtils.findContentTypes(file);
         }

         return new StaticResource(file, url, contentType);
      } else {
         return null;
      }
   }

   private String stripContext(String url) {
      if (this.contextPath != null && this.contextPath.length() > 0) {
         url = url.replaceFirst('/' + this.contextPath, "");
         log.debug("stripped context: " + url);
         return url;
      } else {
         return url;
      }
   }

   public void destroy(HttpManager manager) {
   }

   public String getSupportedLevels() {
      return "1,2";
   }
}

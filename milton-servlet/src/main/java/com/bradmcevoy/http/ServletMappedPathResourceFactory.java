package com.bradmcevoy.http;

import com.ettrema.common.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletMappedPathResourceFactory implements ResourceFactory {
   private static final Logger log = LoggerFactory.getLogger(ServletMappedPathResourceFactory.class);
   private String basePath;

   public Resource getResource(String host, String path) {
      String contextPath = MiltonServlet.request().getContextPath();
      String localPath = path.substring(contextPath.length());
      if (localPath.startsWith(this.basePath)) {
         LogUtils.trace(log, new Object[]{"getResource: matched path: ", localPath});
         return new ServletResource(localPath, MiltonServlet.request(), MiltonServlet.response());
      } else {
         LogUtils.trace(log, new Object[]{"getResource: did not match path: requested:", localPath, "base:", this.basePath});
         return null;
      }
   }

   public String getBasePath() {
      return this.basePath;
   }

   public void setBasePath(String basePath) {
      this.basePath = basePath;
   }
}

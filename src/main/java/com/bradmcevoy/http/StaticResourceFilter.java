package com.bradmcevoy.http;

import java.io.File;
import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceFilter implements Filter {
   private Logger log = LoggerFactory.getLogger(StaticResourceFilter.class);
   private FilterConfig filterConfig = null;

   public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response, FilterChain chain) throws IOException, ServletException {
      HttpServletRequest req = (HttpServletRequest)request;
      String s = MiltonUtils.stripContext(req);
      this.log.debug("url: " + s);
      s = "WEB-INF/static/" + s;
      this.log.debug("check path: " + s);
      String path = this.filterConfig.getServletContext().getRealPath(s);
      this.log.debug("real path: " + path);
      File f = null;
      if (path != null && path.length() > 0) {
         f = new File(path);
      }

      if (f != null && f.exists() && !f.isDirectory()) {
         this.log.debug("static file exists. forwarding..");
         req.getRequestDispatcher(s).forward(request, response);
      } else {
         this.log.debug("static file does not exist, continuing chain..");
         chain.doFilter(request, response);
      }

   }

   public FilterConfig getFilterConfig() {
      return this.filterConfig;
   }

   public void destroy() {
   }

   public void init(FilterConfig filterConfig) {
      this.filterConfig = filterConfig;
   }
}

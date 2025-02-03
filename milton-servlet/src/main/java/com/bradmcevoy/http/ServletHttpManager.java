package com.bradmcevoy.http;

import com.bradmcevoy.http.webdav.WebDavResponseHandler;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletHttpManager extends HttpManager implements Initable {
   private static final Logger log = LoggerFactory.getLogger(ServletHttpManager.class);

   public ServletHttpManager(ResourceFactory resourceFactory, WebDavResponseHandler responseHandler, AuthenticationService authenticationService) {
      super(resourceFactory, responseHandler, authenticationService);
   }

   public ServletHttpManager(ResourceFactory resourceFactory, AuthenticationService authenticationService) {
      super(resourceFactory, authenticationService);
   }

   public ServletHttpManager(ResourceFactory resourceFactory) {
      super(resourceFactory);
   }

   public void init(ApplicationConfig config, HttpManager manager) {
      log.debug("init");
      if (this.resourceFactory != null) {
         if (this.resourceFactory instanceof Initable) {
            Initable i = (Initable)this.resourceFactory;
            i.init(config, manager);
         }

         Iterator i$ = config.getInitParameterNames().iterator();

         while(i$.hasNext()) {
            String paramName = (String)i$.next();
            if (paramName.startsWith("filter_")) {
               String filterClass = config.getInitParameter(paramName);
               log.debug("init filter: " + filterClass);
               String[] arr = paramName.split("_");
               String ordinal = arr[arr.length - 1];
               int pos = Integer.parseInt(ordinal);
               this.initFilter(config, filterClass, pos);
            }
         }
      }

   }

   private void initFilter(ApplicationConfig config, String filterClass, int pos) {
      try {
         Class c = Class.forName(filterClass);
         Filter filter = (Filter)c.newInstance();
         if (filter instanceof Initable) {
            ((Initable)filter).init(config, this);
         }

         this.addFilter(pos, filter);
      } catch (ClassNotFoundException var6) {
         throw new RuntimeException(filterClass, var6);
      } catch (IllegalAccessException var7) {
         throw new RuntimeException(filterClass, var7);
      } catch (InstantiationException var8) {
         throw new RuntimeException(filterClass, var8);
      }
   }

   public void destroy(HttpManager manager) {
      log.debug("destroy");
      if (this.resourceFactory != null && this.resourceFactory instanceof Initable) {
         Initable i = (Initable)this.resourceFactory;
         i.destroy(manager);
      }

      this.shutdown();
   }
}

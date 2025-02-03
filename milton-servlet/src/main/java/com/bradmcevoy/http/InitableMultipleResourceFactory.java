package com.bradmcevoy.http;

import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitableMultipleResourceFactory extends MultipleResourceFactory {
   private Logger log = LoggerFactory.getLogger(InitableMultipleResourceFactory.class);

   public InitableMultipleResourceFactory() {
   }

   public InitableMultipleResourceFactory(List factories) {
      super(factories);
   }

   public void init(ApplicationConfig config, HttpManager manager) {
      String sFactories = config.getInitParameter("resource.factory.multiple");
      this.init(sFactories, config, manager);
   }

   protected void init(String sFactories, ApplicationConfig config, HttpManager manager) {
      this.log.debug("init: " + sFactories);
      String[] arr = sFactories.split(",");
      String[] arr$ = arr;
      int len$ = arr.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String s = arr$[i$];
         this.createFactory(s, config, manager);
      }

   }

   private void createFactory(String s, ApplicationConfig config, HttpManager manager) {
      this.log.debug("createFactory: " + s);

      Class c;
      try {
         c = Class.forName(s);
      } catch (ClassNotFoundException var10) {
         throw new RuntimeException(s, var10);
      }

      Object o;
      try {
         o = c.newInstance();
      } catch (IllegalAccessException var8) {
         throw new RuntimeException(s, var8);
      } catch (InstantiationException var9) {
         throw new RuntimeException(s, var9);
      }

      ResourceFactory rf = (ResourceFactory)o;
      if (rf instanceof Initable) {
         Initable i = (Initable)rf;
         i.init(config, manager);
      }

      this.factories.add(rf);
   }

   public void destroy(HttpManager manager) {
      if (this.factories == null) {
         this.log.warn("factories is null");
      } else {
         Iterator i$ = this.factories.iterator();

         while(i$.hasNext()) {
            ResourceFactory f = (ResourceFactory)i$.next();
            if (f instanceof Initable) {
               ((Initable)f).destroy(manager);
            }
         }

      }
   }
}

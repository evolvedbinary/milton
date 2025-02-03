package com.bradmcevoy.http;

import com.bradmcevoy.http.webdav.WebDavResponseHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringResourceFactoryFactory implements ResourceFactoryFactory {
   ApplicationContext context;

   public void init() {
      this.context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
   }

   public ResourceFactory createResourceFactory() {
      ResourceFactory rf = (ResourceFactory)this.context.getBean("milton.resource.factory");
      return rf;
   }

   public WebDavResponseHandler createResponseHandler() {
      return (WebDavResponseHandler)this.context.getBean("milton.response.handler");
   }
}

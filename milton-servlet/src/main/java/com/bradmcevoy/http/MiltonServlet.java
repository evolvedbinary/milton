package com.bradmcevoy.http;

import com.bradmcevoy.http.webdav.WebDavResponseHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MultipartConfig
public class MiltonServlet implements Servlet {
   private Logger log = LoggerFactory.getLogger(MiltonServlet.class);
   private static final ThreadLocal originalRequest = new ThreadLocal();
   private static final ThreadLocal originalResponse = new ThreadLocal();
   private static final ThreadLocal tlServletConfig = new ThreadLocal();
   private ServletConfig config;
   private ServletContext servletContext;
   protected ServletHttpManager httpManager;

   public static HttpServletRequest request() {
      return (HttpServletRequest)originalRequest.get();
   }

   public static HttpServletResponse response() {
      return (HttpServletResponse)originalResponse.get();
   }

   public static ServletConfig servletConfig() {
      return (ServletConfig)tlServletConfig.get();
   }

   public static void forward(String url) {
      try {
         request().getRequestDispatcher(url).forward((jakarta.servlet.ServletRequest)originalRequest.get(), (jakarta.servlet.ServletResponse)originalResponse.get());
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      } catch (ServletException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void init(ServletConfig config) throws ServletException {
      try {
         this.config = config;
         this.servletContext = config.getServletContext();
         List authHandlers = this.loadAuthHandlersIfAny(config.getInitParameter("authentication.handler.classes"));
         String resourceFactoryFactoryClassName = config.getInitParameter("resource.factory.factory.class");
         if (resourceFactoryFactoryClassName != null && resourceFactoryFactoryClassName.length() > 0) {
            this.initFromFactoryFactory(resourceFactoryFactoryClassName, authHandlers);
         } else {
            String resourceFactoryClassName = config.getInitParameter("resource.factory.class");
            String responseHandlerClassName = config.getInitParameter("response.handler.class");
            this.init(resourceFactoryClassName, responseHandlerClassName, authHandlers);
         }

         this.httpManager.init(new ApplicationConfig(config), this.httpManager);
      } catch (ServletException var6) {
         this.log.error("Exception starting milton servlet", var6);
         throw var6;
      } catch (Throwable var7) {
         this.log.error("Exception starting milton servlet", var7);
         throw new RuntimeException(var7);
      }
   }

   protected void init(String resourceFactoryClassName, String responseHandlerClassName, List authHandlers) throws ServletException {
      this.log.debug("resourceFactoryClassName: " + resourceFactoryClassName);
      ResourceFactory rf = (ResourceFactory)this.instantiate(resourceFactoryClassName);
      WebDavResponseHandler responseHandler;
      if (responseHandlerClassName == null) {
         responseHandler = null;
      } else {
         responseHandler = (WebDavResponseHandler)this.instantiate(responseHandlerClassName);
      }

      this.init(rf, responseHandler, authHandlers);
   }

   protected void initFromFactoryFactory(String resourceFactoryFactoryClassName, List authHandlers) throws ServletException {
      this.log.debug("resourceFactoryFactoryClassName: " + resourceFactoryFactoryClassName);
      ResourceFactoryFactory rff = (ResourceFactoryFactory)this.instantiate(resourceFactoryFactoryClassName);
      rff.init();
      ResourceFactory rf = rff.createResourceFactory();
      WebDavResponseHandler responseHandler = rff.createResponseHandler();
      this.init(rf, responseHandler, authHandlers);
   }

   protected void init(ResourceFactory rf, WebDavResponseHandler responseHandler, List authHandlers) throws ServletException {
      AuthenticationService authService;
      if (authHandlers == null) {
         authService = new AuthenticationService();
      } else {
         List list = new ArrayList();
         Iterator i$ = authHandlers.iterator();

         while(i$.hasNext()) {
            String authHandlerClassName = (String)i$.next();
            Object o = this.instantiate(authHandlerClassName);
            if (!(o instanceof AuthenticationHandler)) {
               throw new ServletException("Class: " + authHandlerClassName + " is not a: " + AuthenticationHandler.class.getCanonicalName());
            }

            AuthenticationHandler auth = (AuthenticationHandler)o;
            list.add(auth);
         }

         authService = new AuthenticationService(list);
      }

      this.log.debug("Configured authentication handlers: " + authService.getAuthenticationHandlers().size());
      if (authService.getAuthenticationHandlers().size() > 0) {
         Iterator i$ = authService.getAuthenticationHandlers().iterator();

         while(i$.hasNext()) {
            AuthenticationHandler hnd = (AuthenticationHandler)i$.next();
            this.log.debug(" - " + hnd.getClass().getCanonicalName());
         }
      } else {
         this.log.warn("No authentication handlers are configured! Any requests requiring authorisation will fail.");
      }

      if (responseHandler == null) {
         this.httpManager = new ServletHttpManager(rf, authService);
      } else {
         this.httpManager = new ServletHttpManager(rf, responseHandler, authService);
      }

   }

   protected Object instantiate(String className) throws ServletException {
      try {
         Class c = Class.forName(className);
         Object rf = c.newInstance();
         return rf;
      } catch (Throwable var4) {
         throw new ServletException("Failed to instantiate: " + className, var4);
      }
   }

   public void destroy() {
      this.log.debug("destroy");
      if (this.httpManager != null) {
         this.httpManager.destroy(this.httpManager);
      }
   }

   public void service(jakarta.servlet.ServletRequest servletRequest, jakarta.servlet.ServletResponse servletResponse) throws ServletException, IOException {
      HttpServletRequest req = (HttpServletRequest)servletRequest;
      HttpServletResponse resp = (HttpServletResponse)servletResponse;

      try {
         setThreadlocals(req, resp);
         tlServletConfig.set(this.config);
         Request request = new ServletRequest(req, this.servletContext);
         Response response = new ServletResponse(resp);
         this.httpManager.process(request, response);
      } finally {
         clearThreadlocals();
         tlServletConfig.remove();
         ServletRequest.clearThreadLocals();
         servletResponse.getOutputStream().flush();
         servletResponse.flushBuffer();
      }

   }

   public static void clearThreadlocals() {
      originalRequest.remove();
      originalResponse.remove();
   }

   public static void setThreadlocals(HttpServletRequest req, HttpServletResponse resp) {
      originalRequest.set(req);
      originalResponse.set(resp);
   }

   public String getServletInfo() {
      return "MiltonServlet";
   }

   public ServletConfig getServletConfig() {
      return this.config;
   }

   private List loadAuthHandlersIfAny(String initParameter) {
      if (initParameter == null) {
         return null;
      } else {
         String[] arr = initParameter.split(",");
         List list = new ArrayList();
         String[] arr$ = arr;
         int len$ = arr.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String s = arr$[i$];
            s = s.trim();
            if (s.length() > 0) {
               list.add(s);
            }
         }

         return list;
      }
   }
}

package com.bradmcevoy.http;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringAwareMiltonServlet implements Servlet {
   private Logger log = LoggerFactory.getLogger(SpringAwareMiltonServlet.class);
   ServletConfig config;
   ApplicationContext context;
   HttpManager httpManager;
   private ServletContext servletContext;
   private static final ThreadLocal originalRequest = new ThreadLocal();
   private static final ThreadLocal originalResponse = new ThreadLocal();

   public static HttpServletRequest request() {
      return (HttpServletRequest)originalRequest.get();
   }

   public static HttpServletResponse response() {
      return (HttpServletResponse)originalResponse.get();
   }

   public static void forward(String url) {
      try {
         request().getRequestDispatcher(url).forward((javax.servlet.ServletRequest)originalRequest.get(), (javax.servlet.ServletResponse)originalResponse.get());
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
         this.context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
         this.httpManager = (HttpManager)this.context.getBean("milton.http.manager");
      } catch (Throwable var3) {
         this.log.error("Exception starting milton servlet", var3);
         throw new RuntimeException(var3);
      }
   }

   public void service(javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse) throws ServletException, IOException {
      HttpServletRequest req = (HttpServletRequest)servletRequest;
      HttpServletResponse resp = (HttpServletResponse)servletResponse;

      try {
         originalRequest.set(req);
         originalResponse.set(resp);
         Request request = new ServletRequest(req, this.servletContext);
         Response response = new ServletResponse(resp);
         this.httpManager.process(request, response);
      } finally {
         originalRequest.remove();
         originalResponse.remove();
         servletResponse.getOutputStream().flush();
         servletResponse.flushBuffer();
      }

   }

   public String getServletInfo() {
      return "SpringAwareMiltonServlet";
   }

   public ServletConfig getServletConfig() {
      return this.config;
   }

   public void destroy() {
   }
}

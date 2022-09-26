package com.bradmcevoy.http;

import com.bradmcevoy.http.http11.DefaultHttp11ResponseHandler.BUFFERING;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

public class SpringMiltonFilter implements Filter {
   private ClassPathXmlApplicationContext context;
   private HttpManager httpManager;
   private FilterConfig filterConfig;
   private ServletContext servletContext;
   private String[] excludeMiltonPaths;

   public void init(FilterConfig fc) throws ServletException {
      StaticApplicationContext parent = new StaticApplicationContext();
      parent.getBeanFactory().registerSingleton("servletContext", fc.getServletContext());
      parent.refresh();
      this.context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"}, parent);
      this.httpManager = (HttpManager)this.context.getBean("milton.http.manager");
      this.httpManager.setBuffering(BUFFERING.never);
      this.filterConfig = fc;
      this.servletContext = fc.getServletContext();
      String sExcludePaths = fc.getInitParameter("milton.exclude.paths");
      this.excludeMiltonPaths = sExcludePaths.split(",");
   }

   public void doFilter(javax.servlet.ServletRequest req, javax.servlet.ServletResponse resp, FilterChain fc) throws IOException, ServletException {
      if (req instanceof HttpServletRequest) {
         HttpServletRequest hsr = (HttpServletRequest)req;
         String url = hsr.getRequestURI();
         String[] arr$ = this.excludeMiltonPaths;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String s = arr$[i$];
            if (url.startsWith(s)) {
               fc.doFilter(req, resp);
               return;
            }
         }

         this.doMiltonProcessing((HttpServletRequest)req, (HttpServletResponse)resp);
      } else {
         fc.doFilter(req, resp);
      }
   }

   public void destroy() {
      this.context.close();
   }

   private void doMiltonProcessing(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      try {
         MiltonServlet.setThreadlocals(req, resp);
         Request request = new ServletRequest(req, this.servletContext);
         Response response = new ServletResponse(resp);
         this.httpManager.process(request, response);
      } finally {
         MiltonServlet.clearThreadlocals();
         resp.getOutputStream().flush();
         resp.flushBuffer();
      }

   }
}

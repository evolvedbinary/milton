package com.bradmcevoy.http;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class MiltonController implements Controller {
   private static final Logger log = LoggerFactory.getLogger(MiltonController.class);
   private HttpManager httpManager;

   public MiltonController() {
   }

   public MiltonController(HttpManager httpManager) {
      log.debug("created miltoncontroller");
      this.httpManager = httpManager;
   }

   public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
      log.debug("handleRequest: " + request.getRequestURI() + " method:" + request.getMethod());
      ServletRequest rq = new ServletRequest(request, (ServletContext)null);
      ServletResponse rs = new ServletResponse(response);
      this.httpManager.process(rq, rs);
      return null;
   }

   public HttpManager getHttpManager() {
      return this.httpManager;
   }

   public void setHttpManager(HttpManager httpManager) {
      this.httpManager = httpManager;
   }
}

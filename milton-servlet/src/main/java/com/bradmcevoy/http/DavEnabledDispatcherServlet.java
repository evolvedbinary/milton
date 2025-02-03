package com.bradmcevoy.http;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.DispatcherServlet;

public class DavEnabledDispatcherServlet extends DispatcherServlet {
   protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      try {
         this.doService(req, resp);
      } catch (ServletException var4) {
         throw var4;
      } catch (IOException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new ServletException(var6);
      }
   }
}

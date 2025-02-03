package com.bradmcevoy.http;

import com.ettrema.sso.SsoSessionProvider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

public class ServletSsoSessionProvider implements SsoSessionProvider, HttpSessionListener {
   private static final Map mapOfSessions = new ConcurrentHashMap();
   private String userSessionVariableName = "user";

   public Object getUserTag(String firstComp) {
      HttpSession sess = (HttpSession)mapOfSessions.get(firstComp);
      if (sess == null) {
         return null;
      } else {
         Object oUser = sess.getAttribute(this.userSessionVariableName);
         return oUser;
      }
   }

   public void sessionCreated(HttpSessionEvent hse) {
      String id = hse.getSession().getId();
      mapOfSessions.put(id, hse.getSession());
   }

   public void sessionDestroyed(HttpSessionEvent hse) {
      String id = hse.getSession().getId();
      mapOfSessions.remove(id);
   }

   public String getUserSessionVariableName() {
      return this.userSessionVariableName;
   }

   public void setUserSessionVariableName(String userSessionVariableName) {
      this.userSessionVariableName = userSessionVariableName;
   }
}

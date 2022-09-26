package com.bradmcevoy.http;

import com.bradmcevoy.http.webdav.WebDavResponseHandler;

public interface ResourceFactoryFactory {
   WebDavResponseHandler createResponseHandler();

   void init();

   ResourceFactory createResourceFactory();
}

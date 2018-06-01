/*
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.elytron.web.undertow.server.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wildfly.elytron.web.undertow.server.ElytronContextAssociationHandler;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;

/**
 * An extension of {@link ElytronContextAssociationHandler} to register the servlet specific security context.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
public class ElytronServletContextAssociationHandler extends ElytronContextAssociationHandler {

    private final String applicationContext;

    private ElytronServletContextAssociationHandler(Builder builder) {
        super(builder);

        this.applicationContext = builder.applicationContext;
    }

    @Override
    public SecurityContext createSecurityContext(HttpServerExchange exchange) {
        final ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequestContext.getServletRequest();
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletRequestContext.getServletResponse();

        return populateSecurityContextBuilder(ServletSecurityContextImpl.builder()
                .setApplicationContext(applicationContext)
                .setHttpServletRequest(httpServletRequest)
                .setHttpServletResponse(httpServletResponse)
                , exchange).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends org.wildfly.elytron.web.undertow.server.ElytronContextAssociationHandler.Builder {

        private String applicationContext;

        public Builder setApplicationContext(final String applicationContext) {
            this.applicationContext = applicationContext;

            return this;
        }

        @Override
        public HttpHandler build() {
            return new ElytronServletContextAssociationHandler(this);
        }

    }

}

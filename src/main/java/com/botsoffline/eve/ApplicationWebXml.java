/*
 * ApplicationWebXml.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve;

import com.botsoffline.eve.config.DefaultProfileUtil;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * This is a helper Java class that provides an alternative to creating a web.xml.
 * This will be invoked only when the application is deployed to a servlet container like Tomcat, JBoss etc.
 */
public class ApplicationWebXml extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        /**
         * set a default to use when no profile is configured.
         */
        DefaultProfileUtil.addDefaultProfile(application.application());
        return application.sources(App.class);
    }
}

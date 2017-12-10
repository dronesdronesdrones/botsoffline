/*
 * SpringSecurityAuditorAware.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve.security;

import com.botsoffline.eve.config.Constants;
import com.botsoffline.eve.config.Constants;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        String userName = SecurityUtils.getCurrentUserLogin();
        return userName != null ? userName : Constants.SYSTEM_ACCOUNT;
    }
}

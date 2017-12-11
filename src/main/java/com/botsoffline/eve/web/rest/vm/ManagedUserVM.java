/*
 * ManagedUserVM.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve.web.rest.vm;

import java.time.Instant;
import java.util.Set;

import com.botsoffline.eve.domain.enums.TrackingStatus;
import com.botsoffline.eve.service.dto.UserDTO;

/**
 * View Model extending the UserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserVM extends UserDTO {

    public ManagedUserVM() {
        // Empty constructor needed for Jackson.
    }

    public ManagedUserVM(String id, String login, boolean activated,
                         String createdBy, Instant createdDate, String lastModifiedBy, Instant lastModifiedDate,
                        Set<String> authorities, TrackingStatus trackingStatus) {
        super(id, login, activated, createdBy, createdDate, lastModifiedBy, lastModifiedDate,  authorities, trackingStatus);
    }

    @Override
    public String toString() {
        return "ManagedUserVM{" +
            "} " + super.toString();
    }
}

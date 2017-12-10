/*
 * InitialSetupMigration.java
 *
 * Created on 2017-12-10
 *
 */

package com.botsoffline.eve.config.dbmigrations;

import com.botsoffline.eve.domain.Authority;
import com.botsoffline.eve.security.AuthoritiesConstants;
import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;

import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Creates the initial database setup
 */
@ChangeLog(order = "001")
public class InitialSetupMigration {

    @ChangeSet(order = "01", author = "initiator", id = "01-addAuthorities")
    public void addAuthorities(MongoTemplate mongoTemplate) {
        Authority adminAuthority = new Authority();
        adminAuthority.setName(AuthoritiesConstants.ADMIN);
        Authority userAuthority = new Authority();
        userAuthority.setName(AuthoritiesConstants.USER);
        Authority managerAuthority = new Authority();
        managerAuthority.setName(AuthoritiesConstants.MANAGER);
        mongoTemplate.save(adminAuthority);
        mongoTemplate.save(userAuthority);
        mongoTemplate.save(managerAuthority);
    }
}

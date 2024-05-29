package com.mung.api.config;

import com.mung.api.config.auth.PrincipalDetails;
import com.mung.api.entity.CustomRevisionEntity;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CustomRevisionEntityListener implements RevisionListener {
    @Override
    public void newRevision(Object o) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomRevisionEntity customRevisionEntity = (CustomRevisionEntity) o;

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            customRevisionEntity.setMemberId(null);
            return;
        }

        PrincipalDetails member = (PrincipalDetails) authentication.getPrincipal();
        customRevisionEntity.setMemberId(member.getMemberId());

    }
}

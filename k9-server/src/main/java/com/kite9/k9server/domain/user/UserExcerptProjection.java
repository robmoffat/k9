package com.kite9.k9server.domain.user;

import org.springframework.data.rest.core.config.Projection;

import com.kite9.k9server.domain.entity.BasicExcerptProjection;

@Projection(types={User.class}, name="default")
public interface UserExcerptProjection extends BasicExcerptProjection {
	
	public boolean isAccountExpired();
	public boolean isAccountLocked();
	public boolean isEmailable();
	public boolean isEmailVerified();
	public boolean isPasswordExpired();
}

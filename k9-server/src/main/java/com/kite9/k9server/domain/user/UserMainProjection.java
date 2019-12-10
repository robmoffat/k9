package com.kite9.k9server.domain.user;

import java.util.List;

import com.kite9.k9server.domain.entity.BasicExcerptProjection;
import com.kite9.k9server.domain.permission.Member;

public interface UserMainProjection extends BasicExcerptProjection {

	public List<Member> getMemberships();
}

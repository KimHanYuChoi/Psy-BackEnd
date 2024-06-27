package com.sentinels.psychology.member.repository;

import com.sentinels.psychology.member.domain.Refresh;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends CrudRepository<Refresh, Long> {

    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);
}

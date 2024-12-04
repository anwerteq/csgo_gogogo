package com.chenerzhu.crawler.proxy.steam.repository;

import com.chenerzhu.crawler.proxy.steam.entity.Descriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DescriptionsRepository extends JpaRepository<Descriptions, Long> {




}

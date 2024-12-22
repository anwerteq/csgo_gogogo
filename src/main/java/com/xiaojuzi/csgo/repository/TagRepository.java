package com.xiaojuzi.csgo.repository;

import com.xiaojuzi.csgo.entity.Tag;
import com.xiaojuzi.csgo.entity.TagPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, TagPk> {

}

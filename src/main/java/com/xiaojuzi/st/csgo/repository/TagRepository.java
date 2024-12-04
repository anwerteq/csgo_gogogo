package com.xiaojuzi.st.csgo.repository;

import com.xiaojuzi.st.csgo.entity.Tag;
import com.xiaojuzi.st.csgo.entity.TagPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, TagPk> {

}

package com.xiaojuzi.config;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class BatchInsertService<T> {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 批量插入实体
     *
     * @param entities  待插入的实体列表
     * @param batchSize 每批次大小
     */
    @Transactional
    public void batchInsert(List<T> entities, int batchSize) {
        for (int i = 0; i < entities.size(); i++) {
            entityManager.persist(entities.get(i));

            // 每达到批次大小时刷新并清理上下文
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        // 插入剩余的数据
        entityManager.flush();
        entityManager.clear();
    }
}

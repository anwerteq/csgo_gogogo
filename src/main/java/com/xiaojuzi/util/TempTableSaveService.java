package com.xiaojuzi.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * {
 */
@Service
@Slf4j
public class TempTableSaveService {

    @Autowired
    private MtdtDbUtil mtdtDbUtil;






    public <T> void batchSave(List<T> savePos, String tempTableName) {
        if (CollectionUtil.isEmpty(savePos) || StrUtil.isEmpty(tempTableName)) {
            return;
        }
        List<List<Object>> objList = new ArrayList<>();
        GenerateSql<T> ci = new GenerateSql<>();
        for (T po : savePos) {
            objList.add(ci.getFiledsObject(po));
        }
        //生成sql语句
        String insertSql = ci.createInsert(savePos.get(0), tempTableName);
        mtdtDbUtil.batchInsert(insertSql, objList);
    }

    public <T> void save(T savePo, String tempTableName) {
        if (ObjectUtil.isNull(savePo) || StrUtil.isEmpty(tempTableName)) {
            return;
        }
        List<List<Object>> objList = new ArrayList<>();
        GenerateSql<T> ci = new GenerateSql<>();
        objList.add(ci.getFiledsObject(savePo));
        //生成sql语句
        String insertSql = ci.createInsert(savePo, tempTableName);
        mtdtDbUtil.batchInsert(insertSql, objList);
    }

    public <T> void save(T savePo, Class<T> clazz) {
        if ( savePo == null || clazz == null) {
            return;
        }
        String tableName = clazz.getAnnotation(Table.class).name();
        save(savePo, tableName);
    }

    public <T> void batchSave(List<T> savePos, Class<T> clazz) {
        if (CollectionUtil.isEmpty(savePos) || clazz == null) {
            return;
        }
        String tableName = clazz.getAnnotation(Table.class).name();
        batchSave(savePos, tableName);
    }


}

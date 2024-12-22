package com.xiaojuzi.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.Table;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;


/**
 * jdbc通用工具类
 *
 * @author chenhong
 * @date 2023/05/22
 */
@Slf4j
@Component
public class MtdtDbUtil {


    @Autowired
    DataSource dataSource;
    @Resource(name = "connectionProvider")
    private ObjectProvider<Connection> connectionProvider;


    /**
     * 批量新增
     *
     * @param sql
     * @param obj
     */
    public void batchInsert(String sql, List<List<Object>> obj) {
        if (ObjectUtil.isNull(obj)  || StrUtil.isEmpty(sql)) {
            return;
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            int count = 0;
            int total = 0;
            preparedStatement = connection.prepareStatement(sql);
            for (List<Object> objects : obj) {
                for (int j = 0; j < objects.size(); j++) {
                    preparedStatement.setObject((j + 1), objects.get(j));
                }
                preparedStatement.addBatch();
                count++;
                total++;
                if (count == 2000) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                    count = 0;
                    connection.commit();
                }
            }
            preparedStatement.executeBatch();
            preparedStatement.clearBatch();
            connection.commit();
            log.info("批量新增数据成功，入库sql为：{},数据总量为：{}", sql, total);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("批量新增异常，入库sql为：{},", sql,e);
                e1.printStackTrace();
            }
            log.error("批量新增异常，入库sql为：{}", sql,e);
            e.printStackTrace();
        } finally {
            //关闭连接
//            try {
//
//                if (preparedStatement != null) {
//                    preparedStatement.close();
//                }
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
            DataSourceUtils.releaseConnection(connection,dataSource);
        }

    }


    public List<Map<String, Object>> selectBysql(String sql) {

        //封装数据用
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();//声明返回的对象
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            con = connectionProvider.getObject();
            //执行查询
            st = con.createStatement();
            rs = st.executeQuery(sql);
            //分析结果集
            ResultSetMetaData rsmd = rs.getMetaData();
            //获取列数
            int cols = rsmd.getColumnCount();
            //遍历数据
            while (rs.next()) {
                //一行数据
                Map<String, Object> mm = new HashMap<String, Object>();
                //遍历列
                for (int i = 0; i < cols; i++) {
                    //获取列名
                    String colName = rsmd.getColumnName(i + 1);
                    //获取数据
                    Object val = rs.getObject(i + 1);
                    //封装到map
                    mm.put(colName, val);
                }
                //将这个map放到list
                list.add(mm);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public <T> void execSql(String sql,List<String> ids){
        StringJoiner sj = new StringJoiner("','");
        for (String s : ids) {
            sj.add(s);
        }
        execSql(sql + " in ('"+sj.toString()+"')");
    }

    public void execSql(String sql) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("sql执行报错，报错信息为：",e);
            }
        }
    }

}

package com.chenerzhu.crawler.proxy.pool.util;

import com.chenerzhu.crawler.proxy.pool.entity.SysDataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
  多数据源工具类
  @author parker
 */
public class MultiDBUtils {

    private SysDataSource sysDataSource = new SysDataSource();

	private JdbcTemplate jdbcTemplate;

	private static final MultiDBUtils _instance = new MultiDBUtils();

    /**
     * 获得实例对象
     * @return
     */
    public static MultiDBUtils getInstance(){
        _instance.setJdbcTemplate(_instance.parseDataSource());
	    return _instance;
    }


	/**
	 * 将数据库中的存储的dataSource对象转换成BasicDataSource
	 * @return
	 */
	private BasicDataSource parseDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(sysDataSource.getDbDriver());
		dataSource.setUrl(StringEscapeUtils.unescapeHtml4(sysDataSource.getDbUrl()));
		dataSource.setUsername(sysDataSource.getDbUserName());
		dataSource.setPassword(sysDataSource.getDbPassword());
		return dataSource;
	}


	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(BasicDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<Map<String, Object>> queryList(String sql, Object... param) {
		List<Map<String, Object>> list;
		if (ArrayUtils.isEmpty(param)) {
			list = jdbcTemplate.queryForList(sql);
		} else {
			list = jdbcTemplate.queryForList(sql, param);
		}
		return list;
	}

	public <T> List<T> queryList(String sql, Class<T> clazz, Object... param) {
		List<T> list;

		if (ArrayUtils.isEmpty(param)) {
			list = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<T>(clazz));
		} else {
			list = jdbcTemplate.query(sql.toString(), new Object[] {param}, new BeanPropertyRowMapper<T>(clazz));
		}
		return list;
	}

	public int update(String sql, Object... param){
		if (ArrayUtils.isEmpty(param)) {
			return jdbcTemplate.update(sql);
		} else {
			return jdbcTemplate.update(sql, param);
		}
	}

}

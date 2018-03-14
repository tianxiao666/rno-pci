package com.hgicreate.rno.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RnoHelper {

	private static Log log=LogFactory.getLog(RnoHelper.class);

	/**
	 * 同用查询方法
	 * @param stmt
	 * @param sql
	 * @return
	 * @author brightming
	 * 2014-2-17 下午3:51:02
	 */
	public static List<Map<String, Object>> commonQuery(Statement stmt, String sql) {
		ResultSet rs = null;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			rs = stmt.executeQuery(sql);
			ResultSetMetaData meta = rs.getMetaData();
			int columnCnt = meta.getColumnCount();
			List<String> labels = new ArrayList<String>();
			for (int i = 1; i <= columnCnt; i++) {
				labels.add(meta.getColumnLabel(i));
			}
			Map<String, Object> one = null;
			int i = 0;
			while (rs.next()) {
				one = new HashMap<String, Object>();
				for (i = 1; i <= columnCnt; i++) {
					one.put(labels.get(i-1), rs.getObject(i));
				}
				result.add(one);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}

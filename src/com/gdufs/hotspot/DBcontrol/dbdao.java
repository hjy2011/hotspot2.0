package com.gdufs.hotspot.DBcontrol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.gdufs.hotspot.DBcontrol.DBUtils;
import com.gdufs.hotspot.entity.News;


/**
 * @author Administrator
 * ���ݿ���ʲ�
 */
public class dbdao {
	
	/**
	 * @param source    ������Դ��վ
	 * @param dateStart ���ݿ�ʼʱ��
	 * @param dateEnd   ���ݽ���ʱ��
	 * @return  list    ���ظö�ʱ���ڵ������б�ÿ�����ŵ����Զ�������news��
	 */  
	public static ArrayList<News> selectCorpus(String source, String dateStart, String dateEnd)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<News> list = new ArrayList<News>();
		try {
			conn = DBUtils.getConnection();
			String sql = "select title, newstime, text from newsCom where newstime between ? and ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dateStart);
			pstmt.setString(2, dateEnd);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				News news = new News();
				news.setSource(source);
				news.setTitle(rs.getString("title"));
				news.setContent(rs.getString("text"));
				news.setDate(rs.getDate("newstime"));
				list.add(news);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBUtils.close(pstmt);
			DBUtils.close(conn);
		}
		return list;
	}

}

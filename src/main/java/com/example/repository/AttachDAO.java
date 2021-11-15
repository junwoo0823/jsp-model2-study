package com.example.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.example.domain.AttachVO;
import com.example.domain.BoardVO;

public class AttachDAO {

	private static AttachDAO instance;

	public static AttachDAO getInstance() {
		if (instance == null) {
			instance = new AttachDAO();
		}
		return instance;
	}

	private AttachDAO() {
	}

	public void addAttach(AttachVO attachVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "";
			sql = "INSERT INTO attach (uuid, uploadpath, filename, filetype, bno) ";
			sql += "VALUES (?, ?, ?, ?, ?) ";

			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, attachVO.getUuid());
			pstmt.setString(2, attachVO.getUploadpath());
			pstmt.setString(3, attachVO.getFilename());
			pstmt.setString(4, attachVO.getFiletype());
			pstmt.setInt(5, attachVO.getBno());

			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt);
		}
	} // addAttach

	// 특정 게시글에 포함된 첨부파일 정보 가져오기
	public List<AttachVO> getAttachesByBno(int bno) {
		List<AttachVO> list = new ArrayList<>();

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "";
			sql = "SELECT * ";
			sql += "FROM attach ";
			sql += "WHERE bno = ? ";
			sql += "ORDER BY filetype, filename ";

			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, bno);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				AttachVO attachVO = new AttachVO();
				attachVO.setUuid(rs.getString("uuid"));
				attachVO.setUploadpath(rs.getString("uploadpath"));
				attachVO.setFilename(rs.getString("filename"));
				attachVO.setFiletype(rs.getString("filetype"));
				attachVO.setBno(rs.getInt("bno"));

				list.add(attachVO);
			} // while
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt, rs);
		}
		return list;
	} // getAttachesByBno

	// 업로드 경로가 일치하는 첨부파일 정보 가져오기
	public List<AttachVO> getAttachesByUploadpath(String uploadpath) {
		List<AttachVO> list = new ArrayList<>();

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "";
			sql = "SELECT * ";
			sql += "FROM attach ";
			sql += "WHERE uploadpath = ? ";

			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, uploadpath);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				AttachVO attachVO = new AttachVO();
				attachVO.setUuid(rs.getString("uuid"));
				attachVO.setUploadpath(rs.getString("uploadpath"));
				attachVO.setFilename(rs.getString("filename"));
				attachVO.setFiletype(rs.getString("filetype"));
				attachVO.setBno(rs.getInt("bno"));

				list.add(attachVO);
			} // while
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt, rs);
		}
		return list;
	} // getAttachesByUploadpath

	// 첨부파일 한개 정보 가져오기
	public AttachVO getAttachByUuid(String uuid) {
		AttachVO attachVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "";
			sql = "SELECT * ";
			sql += "FROM attach ";
			sql += "WHERE uuid = ? ";

			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, uuid);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				attachVO = new AttachVO();
				attachVO.setUuid(rs.getString("uuid"));
				attachVO.setUploadpath(rs.getString("uploadpath"));
				attachVO.setFilename(rs.getString("filename"));
				attachVO.setFiletype(rs.getString("filetype"));
				attachVO.setBno(rs.getInt("bno"));
			} // if
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt, rs);
		}
		return attachVO;
	} // getAttachByUuid

	// 특정 게시글번호에 해당하는 첨부파일들 삭제하기
	public void deleteAttachesByBno(int bno) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "DELETE FROM attach WHERE bno = ? ";

			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, bno);

			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt);
		}
	} // deleteAttachesByBno

	// uuid에 해당하는 첨부파일 한개 삭제하기
	public void deleteAttachByUuid(String uuid) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "DELETE FROM attach WHERE uuid = ? ";

			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, uuid);

			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt);
		}
	} // deleteAttachByUuid

}

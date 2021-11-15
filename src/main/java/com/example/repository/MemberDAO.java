package com.example.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.domain.MemberVO;

// DAO(Data Access Object): 데이터 접근 오브젝트 역할

public class MemberDAO {
	// 싱글톤(singleton) 클래스 설계 : 객체 한개만 공유해서 사용하기
	private static MemberDAO instance;
	
	public static MemberDAO getInstance() {
		if (instance == null) {
			instance = new MemberDAO();
		}
		return instance;
	}

	// 생성자를 private로 외부로부터 숨김
	private MemberDAO() {
	}
	// ========= 싱글톤 설계 완료 =========
	
	
	public int insert(MemberVO memberVO) {
		int count = 0;
		
		Connection con = null;
		PreparedStatement pstmt = null; // sql문장객체 타입
		
		try {
			con = JdbcUtils.getConnection(); // 1단계, 2단계 수행 후 커넥션 가져옴
			// con.setAutoCommit(true); // 기본 커밋은 자동커밋으로 설정되있음.
			
			// 3단계. sql 생성
			String sql = "";
			sql  = "INSERT INTO member (id, passwd, name, birthday, gender, email, recv_email, reg_date) ";
			sql += "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";
			// sql문장객체 준비
			pstmt = con.prepareStatement(sql);
			
			// pstmt의 ? 자리에 값 설정
			pstmt.setString(1, memberVO.getId());
			pstmt.setString(2, memberVO.getPasswd());
			pstmt.setString(3, memberVO.getName());
			pstmt.setString(4, memberVO.getBirthday());
			pstmt.setString(5, memberVO.getGender());
			pstmt.setString(6, memberVO.getEmail());
			pstmt.setString(7, memberVO.getRecvEmail());
			pstmt.setTimestamp(8, memberVO.getRegDate());
			
			// 4단계. sql문 실행
			count = pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt);
		}
		return count;
	} // insert
	
	public int deleteAll() {
		int count = 0;
		
		// JDBC
		Connection con = null; // 접속
		PreparedStatement pstmt = null; // sql문장객체 타입
		
		try {
			con = JdbcUtils.getConnection();
			// sql문 준비
			String sql = "DELETE FROM member";
			// 3단계. pstmt 문장객체 생성
			pstmt = con.prepareStatement(sql);
			// 4단계. sql 문장 실행
			count = pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt);
		}
		return count;
	} // deleteAll
	
	
	// 특정 아이디 해당하는 회원정보 삭제하는 메소드
	// DELETE FROM member WHERE id = ?;
	public int deleteById(String id) {
		int count = 0;
		
		Connection con = null; // 접속
		PreparedStatement pstmt = null; // sql문장객체 타입
		
		try {
			con = JdbcUtils.getConnection();
			
			String sql = "DELETE FROM member WHERE id = ? ";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			
			count = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt);
		}
		return count;
	} // deleteById
	
	// 특정 아이디 해당하는 회원정보 수정하는 메소드
	// UPDATE member
	// SET passwd = ?, name = ?, email = ?, recv_email = ?, reg_date = ?
	// WHERE id = ?;
	public void updateById(MemberVO memberVO) {
		
		Connection con = null; // 접속
		PreparedStatement pstmt = null; // sql문장객체 타입
		
		try {
			con = JdbcUtils.getConnection();
			
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE member ");
			sb.append("SET name = ?, birthday = ?, gender = ?, email = ?, recv_email = ?, reg_date = ? ");
			sb.append("WHERE id = ? ");
			
			pstmt = con.prepareStatement(sb.toString());
			pstmt.setString(1, memberVO.getName());
			pstmt.setString(2, memberVO.getBirthday());
			pstmt.setString(3, memberVO.getGender());
			pstmt.setString(4, memberVO.getEmail());
			pstmt.setString(5, memberVO.getRecvEmail());
			pstmt.setTimestamp(6, memberVO.getRegDate());
			pstmt.setString(7, memberVO.getId());
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt);
		}
	} // updateById
	
	// 특정 아이디 해당하는 회원 레코드(행)의 개수 가져오기
	public int getCountById(String id) {
		int count = 0;
		
		Connection con = null; // 접속
		PreparedStatement pstmt = null; // sql문장객체 타입
		ResultSet rs = null;
		
		try {
			con = JdbcUtils.getConnection();
			
			String sql = "SELECT count(*) AS cnt FROM member WHERE id = ?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				count = rs.getInt(1); // rs.getInt("cnt")
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt, rs);
		}
		return count;
	} // getCountById
	
	
	// SELECT COUNT(*) AS cnt FROM member;
	public int getCountAll() {
		int count = 0;
	
		Connection con = null; // 접속
		PreparedStatement pstmt = null; // sql문장객체 타입
		ResultSet rs = null;
		
		try {
			con = JdbcUtils.getConnection();
			
			String sql = "SELECT count(*) AS cnt FROM member";
			
			pstmt = con.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				count = rs.getInt(1); // rs.getInt("cnt")
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt, rs);
		}
		return count;
	} // getCountAll
	
	
	// 기본키 컬럼값을 기준으로 레코드(행) 최대 1개 가져오기
	// SELECT * FROM member WHERE id = 'bbb';
	public MemberVO getMemberById(String id) {
		MemberVO memberVO = null;
		
		Connection con = null; // 접속
		PreparedStatement pstmt = null; // sql문장객체 타입
		ResultSet rs = null;
		
		try {
			con = JdbcUtils.getConnection();
			
			String sql = "SELECT * FROM member WHERE id = ?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				memberVO = new MemberVO();
				memberVO.setId(rs.getString("id"));
				memberVO.setPasswd(rs.getString("passwd"));
				memberVO.setName(rs.getString("name"));
				memberVO.setBirthday(rs.getString("birthday"));
				memberVO.setGender(rs.getString("gender"));
				memberVO.setEmail(rs.getString("email"));
				memberVO.setRecvEmail(rs.getString("recv_email"));
				memberVO.setRegDate(rs.getTimestamp("reg_date"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt, rs);
		}
		return memberVO;
	} // getMemberById
	
	
	// select * from member order by name;
	public List<MemberVO> getMembers() {
		List<MemberVO> list = new ArrayList<>();
		
		Connection con = null; // 접속
		PreparedStatement pstmt = null; // sql문장객체 타입
		ResultSet rs = null;
		
		try {
			con = JdbcUtils.getConnection();
			
			String sql = "SELECT * FROM member ORDER BY id";
			
			pstmt = con.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				MemberVO memberVO = new MemberVO();
				memberVO.setId(rs.getString("id"));
				memberVO.setPasswd(rs.getString("passwd"));
				memberVO.setName(rs.getString("name"));
				memberVO.setBirthday(rs.getString("birthday"));
				memberVO.setGender(rs.getString("gender"));
				memberVO.setEmail(rs.getString("email"));
				memberVO.setRecvEmail(rs.getString("recv_email"));
				memberVO.setRegDate(rs.getTimestamp("reg_date"));
				
				list.add(memberVO);
			} // while
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt, rs);
		}
		return list;
	} // getMembers
	
	
	
	
	public List<Map<String, Object>> getGenderPerCount() {
		List<Map<String, Object>> list = new ArrayList<>();
		
		Connection con = null; // 접속
		PreparedStatement pstmt = null; // sql문장객체 타입
		ResultSet rs = null;
		
		try {
			con = JdbcUtils.getConnection();
			
			String sql = "";
			sql  = " SELECT  case ";
			sql += " 			when gender = 'F' then '여성' ";
			sql += "            when gender = 'M' then '남성' ";
			sql += " 		end as gender_name, ";
			sql += " 		COUNT(*) AS cnt ";
			sql += " FROM member ";
			sql += " GROUP BY gender ";
			sql += " ORDER BY gender_name ";
			
			pstmt = con.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				Map<String, Object> map = new HashMap<>(); // VO 객체 대용으로 사용
				map.put("genderName", rs.getString("gender_name"));
				map.put("cnt", rs.getInt("cnt"));
				
				list.add(map);
			} // while
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt, rs);
		}
		return list;
	} // getGenderPerCount
	
}









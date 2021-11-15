package com.example.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.domain.BoardVO;
import com.example.domain.Criteria;

public class BoardDAO {

	private static BoardDAO instance;

	public static BoardDAO getInstance() {
		if (instance == null) {
			instance = new BoardDAO();
		}
		return instance;
	}

	private BoardDAO() {
	}

	// board 테이블 모든 레코드 삭제
	public void deleteAll() {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "DELETE FROM board";

			pstmt = con.prepareStatement(sql);

			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt);
		}
	} // deleteAll

	public void deleteBoardByNum(int num) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "DELETE FROM board WHERE num = ?";

			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);

			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt);
		}
	} // deleteBoardByNum

	// 전체글개수 가져오기
	public int getCountAll() {
		int count = 0;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "SELECT COUNT(*) AS cnt FROM board";

			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				count = rs.getInt("cnt");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt, rs);
		}
		return count;
	} // getCountAll

	// 전체글개수 가져오기
	public int getCountBySearch(Criteria cri) {
		int count = 0;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "";
			sql = "SELECT COUNT(*) AS cnt ";
			sql += "FROM board ";
			
			if (cri.getType().length() > 0) { // 검색어가 있으면
				sql += "WHERE " + cri.getType() + " LIKE ? ";
			}
			pstmt = con.prepareStatement(sql); // 문장객체 준비
			
			if (cri.getType().length() > 0) { // 검색어가 있으면
				pstmt.setString(1, "%" + cri.getKeyword() + "%");
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				count = rs.getInt("cnt");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt, rs);
		}
		return count;
	} // getCountAll

	// SELECT IFNULL(MAX(num), 0) + 1 AS nextnum FROM board
	public int getNextnum() {
		int num = 0;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "SELECT IFNULL(MAX(num), 0) + 1 AS nextnum FROM board";

			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				num = rs.getInt("nextnum");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt, rs);
		}
		return num;
	} // getNextnum

	// 주글쓰기
	public void addBoard(BoardVO boardVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "";
			sql = "INSERT INTO board (num, mid, subject, content, readcount, reg_date, ipaddr, re_ref, re_lev, re_seq) ";
			sql += "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boardVO.getNum());
			pstmt.setString(2, boardVO.getMid());
			pstmt.setString(3, boardVO.getSubject());
			pstmt.setString(4, boardVO.getContent());
			pstmt.setInt(5, boardVO.getReadcount());
			pstmt.setTimestamp(6, boardVO.getRegDate());
			pstmt.setString(7, boardVO.getIpaddr());
			pstmt.setInt(8, boardVO.getReRef());
			pstmt.setInt(9, boardVO.getReLev());
			pstmt.setInt(10, boardVO.getReSeq());
			// 실행
			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt);
		}
	} // addBoard

	public List<BoardVO> getBoards() {
		List<BoardVO> list = new ArrayList<>();

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "";
			sql = "SELECT * ";
			sql += "FROM board ";
			sql += "ORDER BY re_ref DESC, re_seq ASC ";

			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				BoardVO boardVO = new BoardVO();
				boardVO.setNum(rs.getInt("num"));
				boardVO.setMid(rs.getString("mid"));
				boardVO.setSubject(rs.getString("subject"));
				boardVO.setContent(rs.getString("content"));
				boardVO.setReadcount(rs.getInt("readcount"));
				boardVO.setRegDate(rs.getTimestamp("reg_date"));
				boardVO.setIpaddr(rs.getString("ipaddr"));
				boardVO.setReRef(rs.getInt("re_ref"));
				boardVO.setReLev(rs.getInt("re_lev"));
				boardVO.setReSeq(rs.getInt("re_seq"));

				list.add(boardVO);
			} // while
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt, rs);
		}
		return list;
	} // getBoards

	public List<BoardVO> getBoards(Criteria cri) {
		List<BoardVO> list = new ArrayList<>();

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// 시작 행번호 (MySQL의 LIMIT절의 시작행번호)

		// 한 페이지당 글개수가 10개씩일때
		// 1 페이지 -> 0
		// 2 페이지 -> 10
		// 3 페이지 -> 20
		// 4 페이지 -> 30
		int startRow = (cri.getPageNum() - 1) * cri.getAmount();

		try {
			con = JdbcUtils.getConnection();

			// 동적 sql문
			String sql = "";
			sql = "SELECT * ";
			sql += "FROM board ";

			if (cri.getType().length() > 0) { // cri.getType().equals("") == false
				sql += "WHERE " + cri.getType() + " LIKE ? ";
			}
			sql += "ORDER BY re_ref DESC, re_seq ASC ";
			sql += "LIMIT ?, ? ";

			pstmt = con.prepareStatement(sql);

			if (cri.getType().length() > 0) { // cri.getType().equals("") == false
				pstmt.setString(1, "%" + cri.getKeyword() + "%");
				pstmt.setInt(2, startRow);
				pstmt.setInt(3, cri.getAmount());
			} else {
				pstmt.setInt(1, startRow);
				pstmt.setInt(2, cri.getAmount());
			}

			rs = pstmt.executeQuery();

			while (rs.next()) {
				BoardVO boardVO = new BoardVO();
				boardVO.setNum(rs.getInt("num"));
				boardVO.setMid(rs.getString("mid"));
				boardVO.setSubject(rs.getString("subject"));
				boardVO.setContent(rs.getString("content"));
				boardVO.setReadcount(rs.getInt("readcount"));
				boardVO.setRegDate(rs.getTimestamp("reg_date"));
				boardVO.setIpaddr(rs.getString("ipaddr"));
				boardVO.setReRef(rs.getInt("re_ref"));
				boardVO.setReLev(rs.getInt("re_lev"));
				boardVO.setReSeq(rs.getInt("re_seq"));

				list.add(boardVO);
			} // while
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt, rs);
		}
		return list;
	} // getBoards

	public BoardVO getBoard(int num) {
		BoardVO boardVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "";
			sql = "SELECT * ";
			sql += "FROM board ";
			sql += "WHERE num = ? ";

			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				boardVO = new BoardVO();
				boardVO.setNum(rs.getInt("num"));
				boardVO.setMid(rs.getString("mid"));
				boardVO.setSubject(rs.getString("subject"));
				boardVO.setContent(rs.getString("content"));
				boardVO.setReadcount(rs.getInt("readcount"));
				boardVO.setRegDate(rs.getTimestamp("reg_date"));
				boardVO.setIpaddr(rs.getString("ipaddr"));
				boardVO.setReRef(rs.getInt("re_ref"));
				boardVO.setReLev(rs.getInt("re_lev"));
				boardVO.setReSeq(rs.getInt("re_seq"));
			} // if
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt, rs);
		}
		return boardVO;
	} // getBoard

	// 조회수 1 증가시키는 메소드
	public void updateReadcount(int num) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "";
			sql = "UPDATE board ";
			sql += "SET readcount = readcount + 1 ";
			sql += "WHERE num = ? ";

			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);

			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt);
		}
	} // updateReadcount

	// 게시글 수정하기
	public void updateBoard(BoardVO boardVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = JdbcUtils.getConnection();

			String sql = "";
			sql = "UPDATE board ";
			sql += "SET subject = ?, content = ?, ipaddr = ? ";
			sql += "WHERE num = ? ";

			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, boardVO.getSubject());
			pstmt.setString(2, boardVO.getContent());
			pstmt.setString(3, boardVO.getIpaddr());
			pstmt.setInt(4, boardVO.getNum());

			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con, pstmt);
		}
	} // updateBoard

	// 답글등록
	public void updateReSeqAndAddReply(BoardVO boardVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = JdbcUtils.getConnection();
			// 수동커밋으로 변경 (기본값은 자동커밋) - 트랜잭션 단위로 처리하기
			con.setAutoCommit(false);

			// 답글을 다는 대상글과 같은 글그룹 내에서
			// 답글을 다는 대상글의 그룹내 순번보다 큰 글들의 순번을 1씩 증가시키기
			String sql = "";
			sql = "UPDATE board ";
			sql += "SET re_seq = re_seq + 1 ";
			sql += "WHERE re_ref = ? ";
			sql += "AND re_seq > ? ";

			pstmt = con.prepareStatement(sql); // update문을 가진 문장객체 준비
			pstmt.setInt(1, boardVO.getReRef());
			pstmt.setInt(2, boardVO.getReSeq());
			pstmt.executeUpdate(); // update문을 가진 문장객체 실행하기
			pstmt.close(); // update문을 가진 문장객체 닫기

			// 답글쓰기
			sql = "INSERT INTO board (num, mid, subject, content, readcount, reg_date, ipaddr, re_ref, re_lev, re_seq) ";
			sql += "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boardVO.getNum());
			pstmt.setString(2, boardVO.getMid());
			pstmt.setString(3, boardVO.getSubject());
			pstmt.setString(4, boardVO.getContent());
			pstmt.setInt(5, boardVO.getReadcount());
			pstmt.setTimestamp(6, boardVO.getRegDate());
			pstmt.setString(7, boardVO.getIpaddr());
			// re값은 insert될 답글정보로 수정하기
			pstmt.setInt(8, boardVO.getReRef()); // 답글의 글그룹번호는 답글다는 대상글의 글그룹번호와 동일
			pstmt.setInt(9, boardVO.getReLev() + 1); // 답글의 레벨은 답글다는 대상글의 레벨값 + 1
			pstmt.setInt(10, boardVO.getReSeq() + 1); // 답글의 순번은 답글다는 대상글의 순번값 + 1
			// 실행
			pstmt.executeUpdate();

			con.commit(); // 커밋하기

			con.setAutoCommit(true); // 기본값이었던 자동커밋으로 설정 되돌리기

		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback(); // 트랜잭션 단위작업에 문제가 생기면 롤백하기
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			JdbcUtils.close(con, pstmt);
		}
	} // updateReSeqAndAddReply
	
	
	/*
	SELECT readcnt, COUNT(*) AS boardcnt
	FROM (
		-- 각 글에 대해서 조회수 범위 구하기
		SELECT case
					when readcount between 0 and 19 then '0~19'
					when readcount between 20 and 39 then '20~39'
					when readcount between 40 and 59 then '40~59'
					when readcount between 60 and 79 then '60~79'
					when readcount between 80 and 99 then '80~99'
					else '100이상'
				end as readcnt
		FROM board
	    WHERE date_format(reg_date, '%Y-%m-%d') = '2021-08-19'
	    ) AS b
	GROUP BY readcnt
	ORDER BY readcnt;
	*/
	public List<Map<String, Object>> getReadcntPerBoardcnt() {
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		
		
		
		return list;
	} // getReadcntPerBoardcnt

}









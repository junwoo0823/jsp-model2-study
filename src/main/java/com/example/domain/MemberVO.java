package com.example.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import lombok.Data;

@Data
public class MemberVO implements Serializable {

	private String id;
	private String passwd;
	private String name;
	private String birthday;
	private String gender;
	private String email;
	private String recvEmail;
	
	private Timestamp regDate;
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String strDate = sdf.format(regDate);
		
		StringBuilder builder = new StringBuilder();
		builder.append("MemberVO [id=")
			.append(id)
			.append(", passwd=")
			.append(passwd)
			.append(", name=")
			.append(name)
			.append(", birthday=")
			.append(birthday)
			.append(", gender=")
			.append(gender)
			.append(", email=")
			.append(email)
			.append(", recvEmail=")
			.append(recvEmail)
			.append(", regDate=")
			.append(strDate)
			.append("]");
		return builder.toString();
	}
}





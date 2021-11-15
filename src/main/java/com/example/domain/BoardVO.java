package com.example.domain;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class BoardVO {

	private Integer num;
	private String mid;
	private String subject;
	private String content;
	private Integer readcount;
	private Timestamp regDate;
	private String ipaddr;
	private Integer reRef;
	private Integer reLev;
	private Integer reSeq;
}





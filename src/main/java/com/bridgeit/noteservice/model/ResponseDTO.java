package com.bridgeit.noteservice.model;

import org.springframework.stereotype.Component;

/**
 * <p>
 * <b>DTO class for giving Response to user.</b>
 * </p>
 * 
 * @author : Vishal Dhar Dubey
 * @version : 1.0
 * @since : 15-07-2018
 */
@Component
public class ResponseDTO {
	private String message;
	private String code;

	public ResponseDTO() {
		super();
	}

	public ResponseDTO(String message, String code) {
		super();
		this.message = message;
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return "ResponseDTO [message=" + message + ", code=" + code + "]";
	}
	
}

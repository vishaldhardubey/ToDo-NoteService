package com.bridgeit.noteservice.exceptionhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bridgeit.noteservice.model.ResponseDTO;
import com.bridgeit.noteservice.utilservice.exceptions.ToDoException;
/**
 * <p>
 * <b>Global Exception Handler is to handle all the exception.</b>
 * </p>
 * @author  : Vishal Dhar Dubey
 * @version : 1.0
 * @since   : 14-07-2018
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	@Autowired
	private ResponseDTO responseDTO;
	
	/**
	 * <p> Function is to handle the exception by giving custom error message</p>
	 * @param toDoException
	 * @return ResponseEntity
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(ToDoException.class)
	public ResponseEntity<ResponseDTO> toDoException(ToDoException toDoException){
		ResponseDTO responseDT=new ResponseDTO();
		responseDT.setMessage(toDoException.getMessage());
		responseDT.setCode("-1");
		return new ResponseEntity(responseDT,HttpStatus.BAD_REQUEST);
	}
}

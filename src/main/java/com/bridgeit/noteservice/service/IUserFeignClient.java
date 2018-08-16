/*package com.bridgeit.noteservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="userservice",url="http://localhost:8091/user")
public interface IUserFeignClient {
	
	@RequestMapping(method = RequestMethod.GET, value = "/getalluserlist")
	public ResponseEntity<?> getAllUser();
}
*/
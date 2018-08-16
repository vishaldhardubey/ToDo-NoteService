package com.bridgeit.noteservice.repository;
/**
 * @author : Vishal Dhar Dubey
 * @version: 1.0
 *           <p>
 *           <b>Redis Repository Interface having function to get the token stored into database</b>
 *           </p>
 * @since : 27-07-2018
 */
public interface IRedisRepository {
	
	/**
	 * @param userId
	 * <p>Function is to get the user id from the token stored in redis repository</p>
	 * @return user
	 */
	public String getToken(String userId);
}

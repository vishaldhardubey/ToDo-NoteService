package com.bridgeit.noteservice.repository;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author : Vishal Dhar Dubey
 * @version: 1.0
 *           <p>
 *           <b>RedisRepository is to save the info about token.</b>
 *           </p>
 * @since : 29-07-2018
 */
@Repository
public class RedisRepository implements IRedisRepository {
	private final static Logger LOGGER = LoggerFactory.getLogger(RedisRepository.class);
	private RedisTemplate<String, Object> redisTemplate;
	
	private HashOperations<String,String,String> hashOperation;

	/**
	 * <p>
	 * Constructor is to initialize the redistemplate
	 * </p>
	 * 
	 * @param redisTemplate
	 */
	public RedisRepository(RedisTemplate<String, Object> redisTemplate) {
		super();
		this.redisTemplate = redisTemplate;
	}
	/**
	 * Function is to initialize the redis Template.
	 */
	@PostConstruct
	private void init() {
		LOGGER.info("inside init of redisRepository ");
		hashOperation=redisTemplate.opsForHash();
	}

	/**
	 * <p>
	 * Function is to get the token from redis repository
	 * </p>
	 * 
	 * @param user id.
	 */
	@Override
	public String getToken(String userId) {
		LOGGER.info("inside getToken of redisRepository ");
		return hashOperation.get("TOKEN", userId);
	}
	
}

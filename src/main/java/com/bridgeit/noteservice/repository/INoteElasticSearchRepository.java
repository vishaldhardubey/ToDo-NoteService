package com.bridgeit.noteservice.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bridgeit.noteservice.model.Note;

/**
 * <p>
 * <b>INoteElasticSearchRepository extending to ElasticSearchRepository</b>
 * </p>
 * 
 * @author : Vishal Dhar Dubey
 * @version : 1.0
 * @since : 15-07-2018
 */
public interface INoteElasticSearchRepository extends ElasticsearchRepository<Note, String> {
	/**
	 * Function is to search List of notes using note Id.
	 * 
	 * @param noteId
	 * @return List of all Notes
	 */
	public List<Note> getById(String noteId);

	/**
	 * <p>
	 * Function is to search for the list of note by user Id
	 * </p>
	 * 
	 * @param userId
	 * @return List of all notes
	 */
	public List<Note> findAllByUserId(String userId);
}

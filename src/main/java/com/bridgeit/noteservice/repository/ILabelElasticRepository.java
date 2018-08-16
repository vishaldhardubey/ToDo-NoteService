package com.bridgeit.noteservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.bridgeit.noteservice.model.Label;
import com.bridgeit.noteservice.model.LabelDTO;

@Repository
public interface ILabelElasticRepository extends ElasticsearchRepository<Label, String>{
	/**
	 * <p>
	 * Function is to search List of label using lableName.
	 * </p>
	 * 
	 * @param list
	 * @return boolean
	 */
	public boolean existsByLabelName(List<String> list);

	/**
	 * <p>
	 * Function is to find the object of label using labelName
	 * </p>
	 * 
	 * @param list
	 * @return label object
	 */
	public Optional<Label> findByLabelName(List<String> list);

	/**
	 * <p>
	 * Function is to insert string values in the database.
	 * </p>
	 * 
	 * @param string
	 */
	public void save(String string);

	/**
	 * <p>
	 * Function is to find the label object using labelName and user Id
	 * </p>
	 * 
	 * @param labelName
	 * @param userId
	 * @return label object
	 */
	public Label findByLabelNameAndUserId(String labelName, String userId);

	/**
	 * <p>
	 * Function is to return list of labels by searching it using userId
	 * </p>
	 * 
	 * @param userId
	 * @return
	 */
	public List<LabelDTO> findAllByUserId(String userId);

	/**
	 * @param labelName
	 * @param userId
	 *            <p>
	 *            Function is to check whether the given label name exists in
	 *            database or not
	 *            </p>
	 * @return
	 */
	public boolean existsByUserIdAndLabelName(String labelName, String userId);
}

package com.bridgeit.noteservice.service;

import java.io.IOException;
import java.util.List;

import com.bridgeit.noteservice.model.Description;
import com.bridgeit.noteservice.model.LabelDTO;
import com.bridgeit.noteservice.model.Note;
import com.bridgeit.noteservice.model.NoteDTO;
import com.bridgeit.noteservice.model.RemindMe;
import com.bridgeit.noteservice.utilservice.exceptions.ToDoException;

/**
 * Purpose : Note Service Interface.
 * 
 * @author : Vishal Dhar Dubey
 * @version : 1.0
 * @since : 16-07-2018
 */
public interface INoteService {
	/*****************************************************************************************************************************
	 * <p>
	 * Function is to create Note for the authorised and logined user.
	 * </p>
	 * 
	 * @param note
	 * @param req
	 * @throws ToDoException
	 * @throws IOException
	 */
	public void createNote(NoteDTO note, String userId) throws ToDoException, IOException;

	/*****************************************************************************************************************************
	 * <p>
	 * Function is to update Note for the authorised and logined user.
	 * </p>
	 * 
	 * @param note
	 * @param req
	 */
	public void updateNote(NoteDTO note, String userId) throws ToDoException;

	/*****************************************************************************************************************************
	 * <p>
	 * Function is to delete Note for the authorised and logined user.
	 * </p>
	 * 
	 * @param noteId
	 * @param userId
	 * @return List
	 */
	public List<Note> getNoteById(Note note, String userId) throws ToDoException;

	/*****************************************************************************************************************************
	 * <p>
	 * Function is to read note by ID for the authorised and logined user.
	 * </p>
	 * 
	 * @param userId
	 * @return List
	 * @throws ToDoException
	 */
	public List<Note> getAllNotesById(String userId) throws ToDoException;

	/*****************************************************************************************************************************
	 * <p>
	 * Function is to get all note by ID for the authorised and logined user.
	 * </p>
	 * 
	 * @param noteId
	 * @param userId
	 */
	public void deleteNote(String noteId, String userId) throws ToDoException;

	/*****************************************************************************************************************************
	 * <p>
	 * Function is to move note by note Id to Archieve.
	 * </p>
	 * 
	 * @param noteId
	 * @param userId
	 * @throws ToDoException
	 */
	public void moveToArchieve(String noteId, String userId) throws ToDoException;

	/*****************************************************************************************************************************
	 * <p>
	 * Function is to move note by note Id to note database.
	 * </p>
	 * 
	 * @param noteId
	 * @param userId
	 * @throws ToDoException
	 */
	public void restoreFromArchieve(String noteId, String userId) throws ToDoException;

	/****************************************************************************************************************************
	 * @param token
	 * @param noteId
	 *            <p>
	 *            <b>To pin the particular note</b>
	 *            </p>
	 * @throws ToDoException
	 * @throws ToDoExceptions
	 **/
	public void pinNote(String noteId, String userId) throws ToDoException;

	/****************************************************************************************************************************
	 * @param userId
	 * @param noteId
	 *            <p>
	 *            <b>To unpin the particular note</b>
	 *            </p>
	 * @throws ToDoException
	 */
	public void unPinNote(String userId, String noteId) throws ToDoException;

	/****************************************************************************************************************************
	 * @param token
	 * @param noteId
	 *            <p>
	 *            <b>To add the label created separately</b>
	 *            </p>
	 * @throws ToDoException
	 * @throws ToDoExceptions
	 **/

	public void addLabel(String token, String noteId, String labelNames) throws ToDoException;

	/**
	 * @param labelName
	 * @param userId
	 *            <p>
	 *            <b>Function is to create new label </b>
	 *            </p>
	 * @throws ToDoException
	 */
	public void createLabel(String labelName, String userId) throws ToDoException;

	/*****************************************************************************************************************************
	 * @param reminder
	 * @param userId
	 *            <p>
	 *            <b>To add the reminder for given noteId</b>
	 *            </p>
	 * @param userId2
	 * @throws ToDoException
	 */
	public void addReminder(RemindMe reminder, String userId, String userId2) throws ToDoException;

	/**
	 * @param noteId
	 * @param userId
	 *            <p>
	 *            <b>To remove the reminder from the given note</b>
	 *            </p>
	 * @throws ToDoException
	 */
	public void removeReminder(String noteId, String userId) throws ToDoException;

	/****************************************************************************************************************************
	 * @param req
	 *            <p>
	 *            Function is to read notes from Archive.
	 *            </p>
	 * @return
	 * @throws ToDoException
	 */
	public List<Note> readArchive(String userId) throws ToDoException;

	/**
	 * @param noteId
	 * @param userId
	 * @param labelName
	 *            <p>
	 *            Function is to delete label from repository.
	 *            </p>
	 * @throws ToDoException
	 */
	public void deleteLabel(String noteId, String userId, String labelName) throws ToDoException;

	/**
	 * @param noteId
	 * @param userId
	 * @param labelName
	 *            <p>
	 *            Function is to delete label from repository.
	 *            </p>
	 * @throws ToDoException
	 */
	public void deleteLabelFromNote(String noteId, String userId, String labelName) throws ToDoException;

	/****************************************************************************************************************************
	 * @param userId
	 *            <p>
	 *            Function is to read all the labels.
	 *            </p>
	 * @return list
	 * @throws ToDoException
	 */
	public List<LabelDTO> readAllLabels(String userId) throws ToDoException;

	/****************************************************************************************************************************
	 * @param list
	 *            <p>
	 *            Function is to add the links.
	 *            </p>
	 * @return list
	 * @throws IOException
	 */
	public List<Description> noteLink(List<String> list) throws IOException;

	/****************************************************************************************************************************
	 * @param userId
	 *            <p>
	 *            Function is to sort by Name.
	 *            </p>
	 * @return list
	 * @throws ToDoException
	 */
	List<Note> sortByName(String userId) throws ToDoException;

	/***************************************************************************************************************************
	 * @param userId
	 *            <p>
	 *            Function is to sort by Date.
	 *            </p>
	 * @return list
	 * @throws ToDoException
	 */
	List<Note> sortByDate(String userId) throws ToDoException;

}

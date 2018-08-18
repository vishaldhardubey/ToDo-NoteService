package com.bridgeit.noteservice.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgeit.noteservice.model.LabelDTO;
import com.bridgeit.noteservice.model.Note;
import com.bridgeit.noteservice.model.NoteDTO;
import com.bridgeit.noteservice.model.RemindMe;
import com.bridgeit.noteservice.model.ResponseDTO;
import com.bridgeit.noteservice.repository.IRedisRepository;
import com.bridgeit.noteservice.service.INoteService;
import com.bridgeit.noteservice.utilservice.exceptions.RestPreconditions;
import com.bridgeit.noteservice.utilservice.exceptions.ToDoException;
import com.bridgeit.noteservice.utilservice.messageaccessor.MessageAccessor;
import com.google.common.base.Preconditions;

import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * <b>Note Controller class is resposible for getting request and giving
 * response to Swagger API</b>
 * </p>
 * 
 * @author : Vishal Dhar Dubey
 * @version : 1.0
 * @since : 14-07-2018
 */
@RestController
@RequestMapping("/notes")
public class NoteController {

	@Autowired
	INoteService iNoteService;
	private static final Logger LOGGER = LoggerFactory.getLogger(NoteController.class);

	@Autowired
	IRedisRepository iRedisRepository;

	@Autowired
	private MessageAccessor messageAccessor;

	@Autowired
	private ResponseDTO response;
	
	/*@Autowired
	IUserFeignClient iUserFeign;*/

	/****************************************************************************************************************************
	 * <p>
	 * Function is to create Note for the authorised and logined user.
	 * </p>
	 * 
	 * @param note
	 * @param req
	 * @return response
	 * @throws ToDoException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "asdfgh" )
	@RequestMapping(method = RequestMethod.POST, value = "/createnote")
	
	public ResponseEntity<ResponseDTO> createNote(@RequestBody NoteDTO noteDto, HttpServletRequest req)
			throws ToDoException, IOException {
		LOGGER.info("Inside CreateNote Function of Controller" + req.getRequestURI());
		
		String userId = req.getHeader("userId");
		
		RestPreconditions.checkNotNull(noteDto, new ResponseDTO(messageAccessor.getMessage("311"), "311"));
		RestPreconditions.checkNotNull(noteDto.getId(), "NullPointerException : Note Id must not be Null");
		RestPreconditions.checkNotNull(noteDto.getDescription(),
				"NullPointerException : Description can be empty string but not null");
		RestPreconditions.checkNotNull(noteDto.getTitle(),
				"NullPointerException : Title can be empty string but not null");
		
		if (noteDto.getDescription() == null && noteDto.getTitle() == null && noteDto.getDescription() == null) {
			throw new ToDoException("NullPointerException : Title and Description both together cann't be null");
		}
		// String token=req.getHeader("token");
		System.out.println(userId);
		RestPreconditions.checkNotNull(userId, "Invalid Link");
		
		iNoteService.createNote(noteDto, userId);
		LOGGER.info("End with createNote in controller" + req.getRequestURI());
		
		return new ResponseEntity(new ResponseDTO(messageAccessor.getMessage("106"), "106"), HttpStatus.OK);

	}

	/****************************************************************************************************************************
	 * <p>
	 * Function is to update Note for the authorised and logined user.
	 * </p>
	 * 
	 * @param note
	 * @param req
	 * @return response to swagger
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(method = RequestMethod.POST, value = "/updatenote")
	public ResponseEntity<Note> updateNote(@RequestBody NoteDTO note, HttpServletRequest req) throws ToDoException {
		LOGGER.info("Inside updateNote Function of Controller" + req.getRequestURI());
		
		String userId = req.getHeader("userId");
		
		iNoteService.updateNote(note, userId);
		LOGGER.info("End with updateNote in controller" + req.getRequestURI());
		return new ResponseEntity(new ResponseDTO(messageAccessor.getMessage("107"), "107"), HttpStatus.OK);
	}

	/****************************************************************************************************************************
	 * <p>
	 * Function is to delete Note for the authorised and logined user.
	 * </p>
	 * 
	 * @param noteId
	 * @param req
	 * @return response
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(method = RequestMethod.POST, value = "/deletenote")
	public ResponseEntity<Note> deleteNote(@RequestParam String noteId, HttpServletRequest req) {
		LOGGER.info("Inside deleteNote Function of Controller" + req.getRequestURI());
		// RestPreconditions.checkNotNull(reference, errorMessage)
		try {
			iNoteService.deleteNote(noteId, req.getHeader("userId"));
			LOGGER.info("End with updateNote in controller by giving required response" + req.getRequestURI());
			return new ResponseEntity(new ResponseDTO(messageAccessor.getMessage("108"), "108"), HttpStatus.OK);
		} catch (ToDoException exception) {
			response.setCode("-3");
			response.setMessage(exception.getMessage());
			LOGGER.info("End with updateNote in controller by giving exception" + req.getRequestURI());
			return new ResponseEntity(response, HttpStatus.OK);
		}

	}

	/****************************************************************************************************************************
	 * <p>
	 * Function is to read note by ID for the authorised and logined user.
	 * </p>
	 * 
	 * @param note
	 * @param token
	 * @return response
	 * @throws ToDoException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(method = RequestMethod.POST, value = "/getnotebyid")
	public ResponseEntity<Note> getNoteById(@RequestBody Note note, HttpServletRequest req) throws ToDoException {
		LOGGER.info("Inside the getNoteById in controller" + req.getRequestURI());
		// userService.loginUser(user);
		RestPreconditions.checkNotNull(note, "Fields cann't be null");
		String userId = req.getHeader("userId");
		List<Note> noteInfo = iNoteService.getNoteById(note, userId);
		RestPreconditions.checkNotNull(noteInfo, "No any note found exception");
		LOGGER.info("End with getNoteById in controller" + req.getRequestURI());
		return new ResponseEntity(noteInfo, HttpStatus.OK);
	}

	/*****************************************************************************************************************************
	 * <p>
	 * Function is to get all note by ID for the authorised and logined user.
	 * </p>
	 * 
	 * @param token
	 * @return response
	 * @throws ToDoException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(method = RequestMethod.GET, value = "/getallnote")
	public ResponseEntity<List<Note>> getAllNotes(HttpServletRequest req)
			throws ToDoException {
		LOGGER.info("Inside the getAllNoteById function in controller" + req.getRequestURI());

		Preconditions.checkNotNull(req.getHeader("userId"),
				new ResponseDTO(messageAccessor.getMessage("318"), "318"));
		
		List<Note> noteInfo = iNoteService.getAllNotesById(req.getHeader("userId"));
		
		RestPreconditions.checkNotNull(noteInfo, "No any note found exception");
		
		LOGGER.info("End with getAllNoteById function in controller" + req.getRequestURI());
		return new ResponseEntity(noteInfo, HttpStatus.OK);
	}

	/****************************************************************************************************************************
	 * <p>
	 * Function is to move note into trash after deletion
	 * </p>
	 * 
	 * @param noteId
	 * @param req
	 * @return response
	 * @throws ToDoException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(method = RequestMethod.POST, value = "/movetoarchieve")
	public ResponseEntity<?> moveToArchieve(@RequestParam String noteId, HttpServletRequest req) throws ToDoException {
		LOGGER.info("Inside the moveToArchieve in controller" + req.getRequestURI());
		String userId = req.getHeader("userId");
		RestPreconditions.checkNotNull(userId, new ResponseDTO(messageAccessor.getMessage("316"), "316"));
		RestPreconditions.checkNotNull(noteId, new ResponseDTO(messageAccessor.getMessage("317"), "317"));
		iNoteService.moveToArchieve(noteId, userId);
		LOGGER.info("End with moveToArchieve in controller" + req.getRequestURI());
		return new ResponseEntity(new ResponseDTO(messageAccessor.getMessage("121"), "121"), HttpStatus.OK);
	}

	/****************************************************************************************************************************
	 * @param token
	 * @param noteId
	 *            <p>
	 *            <b>To pin the particular note</b>
	 *            </p>
	 * @throws ToDoExceptions
	 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(method = RequestMethod.POST, value = "/pinnote")
	public ResponseEntity<?> pinNote(@RequestParam String noteId, HttpServletRequest req) throws ToDoException {
		LOGGER.info("Inside the pinNote in controller" + req.getRequestURI());
		String userId = req.getHeader("userId");
		RestPreconditions.checkNotNull(userId, new ResponseDTO(messageAccessor.getMessage("316"), "316"));
		RestPreconditions.checkNotNull(noteId, new ResponseDTO(messageAccessor.getMessage("317"), "317"));
		iNoteService.pinNote(noteId, userId);
		LOGGER.info("End with moveToArchieve in controller" + req.getRequestURI());
		return new ResponseEntity(new ResponseDTO(messageAccessor.getMessage("119"), "119"), HttpStatus.OK);
	}

	/****************************************************************************************************************************
	 * @param token
	 * @param noteId
	 *            <p>
	 *            <b>To pin the particular note</b>
	 *            </p>
	 * @throws ToDoExceptions
	 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(method = RequestMethod.POST, value = "/unpinnote")
	public ResponseEntity<?> unPinNote(@RequestParam String noteId, HttpServletRequest req) throws ToDoException {
		LOGGER.info("Inside the pinNote in controller" + req.getRequestURI());
		String userId = req.getHeader("userId");
		RestPreconditions.checkNotNull(userId, new ResponseDTO(messageAccessor.getMessage("316"), "316"));
		RestPreconditions.checkNotNull(noteId, new ResponseDTO(messageAccessor.getMessage("317"), "317"));
		iNoteService.unPinNote(noteId, userId);
		LOGGER.info("End with unpin of controller" + req.getRequestURI());
		return new ResponseEntity(new ResponseDTO(messageAccessor.getMessage("120"), "120"), HttpStatus.OK);
	}

	/****************************************************************************************************************************
	 * <p>
	 * Function is to move note into trash after deletion
	 * </p>
	 * 
	 * @param noteId
	 * @param req
	 * @return response
	 * @throws ToDoException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(method = RequestMethod.POST, value = "/restorefromarchieve")
	public ResponseEntity<?> restoreFromArchieve(@RequestParam String noteId, HttpServletRequest req)
			throws ToDoException {
		LOGGER.info("Inside the moveToArchieve in controller" + req.getRequestURI());
		String userId = req.getHeader("userId");
		RestPreconditions.checkNotNull(userId, new ResponseDTO(messageAccessor.getMessage("316"), "316"));
		RestPreconditions.checkNotNull(noteId, new ResponseDTO(messageAccessor.getMessage("317"), "317"));
		iNoteService.restoreFromArchieve(noteId, userId);
		LOGGER.info("End with moveToArchieve in controller" + req.getRequestURI());
		return new ResponseEntity(new ResponseDTO(messageAccessor.getMessage("122"), "122"), HttpStatus.OK);
	}

	/****************************************************************************************************************************
	 * <p>
	 * Function is to create new label
	 * </p>
	 * 
	 * @param noteId
	 * @param req
	 * @return response
	 * @throws ToDoException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })

	@RequestMapping(method = RequestMethod.POST, value = "/createlabel")
	public ResponseEntity<?> createLabel(@RequestParam String labelName, HttpServletRequest req) throws ToDoException {
		LOGGER.info("Inside the createlabelfromNote in controller" + req.getRequestURI());
		RestPreconditions.checkNotNull(req.getHeader("token"), "NullPointerExcetion : token is not present");
		String userId =req.getHeader("userId");
		iNoteService.createLabel(labelName, userId);
		return new ResponseEntity(new ResponseDTO(messageAccessor.getMessage("112"), "112"), HttpStatus.OK);
	}

	/****************************************************************************************************************************
	 * <p>
	 * Function is to move note into trash after deletion
	 * </p>
	 * 
	 * @param noteId
	 * @param req
	 * @return response
	 * @throws ToDoException
	 */
	@RequestMapping(value = "/addlabel", method = RequestMethod.POST)
	public ResponseEntity<ResponseDTO> addLabel(HttpServletRequest req, @RequestParam String noteId,
			@RequestParam String labels) throws ToDoException {
		LOGGER.info("Inside add label function " + req.getRequestURI());
		iNoteService.addLabel(req.getHeader("userId"), noteId, labels);
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setMessage("Labels added successfully");
		responseDTO.setCode("1");
		return new ResponseEntity<>(responseDTO, HttpStatus.OK);
	}

	/****************************************************************************************************************************
	 * @param reminder
	 * @param noteId
	 * @param req
	 *            <p>
	 *            Function is to add reminder for the given note Id
	 *            </p>
	 * @return
	 * @throws ToDoException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(method = RequestMethod.POST, value = "/addreminder")
	public ResponseEntity<String> addReminder(@RequestBody RemindMe reminder, @RequestParam String noteId,
			HttpServletRequest req) throws ToDoException {
		LOGGER.info("Inside reminder function " + req.getRequestURI());
		LOGGER.info("Null checker for request body remindeMe");
		RestPreconditions.checkNotNull(reminder, "NullPointerException : reminder body must not be null");
		LOGGER.info("Null checker for token");
		RestPreconditions.checkNotNull(req.getHeader("token"), "NullPointerException : token must not be null");
		String userId = req.getHeader("userId");
		iNoteService.addReminder(reminder, noteId, userId);
		LOGGER.info("Sending response of reminder");
		return new ResponseEntity(new ResponseDTO(messageAccessor.getMessage("111"), "111"), HttpStatus.OK);
	}

	/****************************************************************************************************************************
	 * @param noteId
	 * @param req
	 *            <p>
	 *            Function is to remove reminder from the given note Id
	 *            </p>
	 * @return response
	 * @throws ToDoException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(method = RequestMethod.DELETE, value = "/removereminder")
	public ResponseEntity<String> removeReminder(@RequestParam String noteId, HttpServletRequest req)
			throws ToDoException {
		LOGGER.info("Inside reminder function " + req.getRequestURI());
		LOGGER.info("Null checker for request body remindeMe");
		RestPreconditions.checkNotNull(noteId,
				"NoValuePresentException : Value must be present to remove the reminder");
		LOGGER.info("Null checker for token");
		RestPreconditions.checkNotNull(req.getHeader("token"), "NullPointerException : token must not be null");
		String userId = req.getHeader("userId");
		iNoteService.removeReminder(noteId, userId);
		LOGGER.info("Sending response of reminder");
		return new ResponseEntity(new ResponseDTO(messageAccessor.getMessage("110"), "110"), HttpStatus.OK);

	}

	/****************************************************************************************************************************
	 * @param req
	 *            <p>
	 *            Function is to read notes from Archive.
	 *            </p>
	 * @return
	 * @throws ToDoException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(method = RequestMethod.POST, value = "/readarchive")
	public ResponseEntity<List<Note>> readArchive(HttpServletRequest req) throws ToDoException {
		LOGGER.info("Inside reminder function " + req.getRequestURI());
		LOGGER.info("Null checker for request body remindeMe");
		RestPreconditions.checkNotNull(req.getHeader("token"), "NullPointerException : token must not be null");
		LOGGER.info("Null checker for token");
		String userId = req.getHeader("userId");
		List<Note> archiveList = iNoteService.readArchive(userId);
		LOGGER.info("Sending response of reminder");
		return new ResponseEntity(archiveList, HttpStatus.OK);
	}

	/****************************************************************************************************************************
	 * <p>
	 * Function is to delete the label from note and label repository.
	 * </p>
	 * 
	 * @param noteId
	 * @param req
	 * @return response
	 * @throws ToDoException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(method = RequestMethod.DELETE, value = "/deletelabel")
	public ResponseEntity<?> deleteLabel(@RequestParam String noteId, @RequestParam String labelName,
			HttpServletRequest req) throws ToDoException {
		LOGGER.info("Inside the createlabelfromNote in controller" + req.getRequestURI());
		RestPreconditions.checkNotNull(req.getHeader("token"), "NullPointerException : token must not be null");
		String userId = req.getHeader("userId");
		LOGGER.info("calling delete label function of note service");
		iNoteService.deleteLabel(noteId, userId, labelName);
		return new ResponseEntity(new ResponseDTO(messageAccessor.getMessage("113"), "113"), HttpStatus.OK);
	}

	/****************************************************************************************************************************
	 * @param noteId
	 * @param req
	 * @param labelName
	 *            <p>
	 *            Function is to delete label from Note.
	 *            </p>
	 * @throws ToDoException
	 * @return response
	 *
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(method = RequestMethod.DELETE, value = "/deletelabelfromnote")
	public ResponseEntity<?> deleteLabelFromNote(@RequestParam String noteId, @RequestParam String labelName,
			HttpServletRequest req) throws ToDoException {
		LOGGER.info("Inside the deleteLabelFromNote in controller" + req.getRequestURI());
		RestPreconditions.checkNotNull(req.getHeader("token"), "NullPointerException : token must not be null");
		String userId = req.getHeader("userId");
		LOGGER.info("calling delete label function of note service");
		iNoteService.deleteLabelFromNote(noteId, userId, labelName);
		return new ResponseEntity(new ResponseDTO(messageAccessor.getMessage("116"), "116"), HttpStatus.OK);
	}

	/****************************************************************************************************************************
	 * @param req
	 *            <p>
	 *            Function is to read all the labels.
	 *            </p>
	 * @return response
	 * @throws ToDoException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(method = RequestMethod.GET, value = "/readlabels")
	public ResponseEntity<List<LabelDTO>> readAllLabels(HttpServletRequest req) throws ToDoException {

		LOGGER.info("Inside the read all labels in controller" + req.getRequestURI());
		RestPreconditions.checkNotNull(req.getHeader("token"), "NullPointerException : token must not be null");
		String userId = req.getHeader("userId");
		LOGGER.info("calling deleteLabelFromNote function of note service");
		List<LabelDTO> labelList = iNoteService.readAllLabels(userId);
		return new ResponseEntity(labelList, HttpStatus.OK);
	}
	
	/****************************************************************************************************************************
	 * @param req
	 *            <p>
	 *            Function is to read all the details of user.
	 *            </p>
	 * @return response
	 * @throws ToDoException
	 */
	/*@RequestMapping(method = RequestMethod.GET, value = "/getalluserlist")
	public ResponseEntity<?> getAllUserfromNote() {
		System.out.println("Inside feign client note service");
		return iUserFeign.getAllUser();
	}*/
	@RequestMapping(method = RequestMethod.POST, value = "/sortbyname")
	public ResponseEntity<List<Note>> sortByName(HttpServletRequest req) throws ToDoException {
		RestPreconditions.checkNotNull(req.getHeader("userId"), messageAccessor.getMessage("319"));
		iNoteService.sortByName(req.getHeader("userId"));
		return null;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/sortbydate")
	public ResponseEntity<List<Note>> sortByDate(HttpServletRequest req) throws ToDoException {
		RestPreconditions.checkNotNull(req.getHeader("userId"), messageAccessor.getMessage("319"));
		iNoteService.sortByDate(req.getHeader("userId"));
		return null;
	}
}
package com.bridgeit.noteservice.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bridgeit.noteservice.model.Description;
import com.bridgeit.noteservice.model.Label;
import com.bridgeit.noteservice.model.LabelDTO;
import com.bridgeit.noteservice.model.Note;
import com.bridgeit.noteservice.model.NoteArchieveDTO;
import com.bridgeit.noteservice.model.NoteDTO;
import com.bridgeit.noteservice.model.RemindMe;
import com.bridgeit.noteservice.repository.ILabelElasticRepository;
import com.bridgeit.noteservice.repository.INoteArchiveRepository;
import com.bridgeit.noteservice.repository.INoteElasticSearchRepository;
import com.bridgeit.noteservice.repository.INoteLabelRepository;
import com.bridgeit.noteservice.repository.INoteRepository;
import com.bridgeit.noteservice.utilservice.exceptions.RestPreconditions;
import com.bridgeit.noteservice.utilservice.exceptions.ToDoException;
import com.bridgeit.noteservice.utilservice.modelmapperservice.ModelMapperService;

/**
 * <p>
 * <b>Note Service Implementation for the Note Service interface.</b>
 * </p>
 * 
 * @author : Vishal Dhar Dubey
 * @version : 1.0
 * @since : 16-07-2018
 */

@Service
public class NoteServiceImpl implements INoteService {
	private static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);
	@Autowired
	private INoteRepository iNoteRepository;

	@Autowired
	private INoteLabelRepository iNoteLabelRepository;

	@Autowired
	private INoteElasticSearchRepository iNoteElasticSearchRepository;

	@Autowired
	private ILabelElasticRepository iLabelElasticRepository;

	@Autowired
	private INoteArchiveRepository iNoteArchiveRepository;

	@Autowired
	ModelMapperService modelMapperService;

	static List<String> labelList = new ArrayList<>();

	/***********************************************************************************************************************************
	 * <p>
	 * Function is to create Note for the authorised and logined user.
	 * </p>
	 * 
	 * @param notedto
	 * @param userId
	 * @throws ToDoException
	 * @throws IOException 
	 */
	@Override
	public void createNote(NoteDTO noteDto, String userId) throws ToDoException, IOException {
		logger.info("Inside Create Note method");
		
		RestPreconditions.checkNotNull(noteDto, "NullPointerException : noteDto must contain some value");
		
		Note note = modelMapperService.map(noteDto, Note.class);
		//calling noteLink method
		List<Description> contentscrappinglist=noteLink(noteDto.getContentScrapingURL());
		
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		note.setUserId(userId);
		note.setCreatedDate(timeStamp);
		note.setLastDatModified(timeStamp);
		note.setContentDescription(contentscrappinglist);
		List<Label> labelExists = new ArrayList<Label>();
		List<Label> list = new ArrayList<Label>();
		
		for (int i = 0; i < noteDto.getLabelNameList().size(); i++) {
			Label labelName = iLabelElasticRepository.findByLabelNameAndUserId(noteDto.getLabelNameList().get(i),
					userId);
			if (labelName != null) {
				Label label = new Label();
				label.setLabelName(labelName.getLabelName());
				label.setUserId(userId);
				labelExists.add(label);
				note.setLabel(labelExists);
			} else if (labelName == null) {
				Label label = new Label();
				label.setLabelName(noteDto.getLabelNameList().get(i));
				label.setUserId(userId);
				iNoteLabelRepository.insert(label);
				iLabelElasticRepository.save(label);
				list.add(label);
				note.setLabel(list);
			}
		}

		logger.info("Ended after Creating a Note");
		iNoteRepository.insert(note);
		iNoteElasticSearchRepository.save(note);
	}

	/***********************************************************************************************************************************
	 * <p>
	 * Function is to get note by note Id.
	 * </p>
	 * 
	 * @param note
	 * @param userId
	 * @throws ToDoException
	 */
	@Override
	public List<Note> getNoteById(Note note, String userId) throws ToDoException {
		logger.info("Inside getNoteById method");
		String noteId = note.getId();
		RestPreconditions.checkNotNull(iNoteElasticSearchRepository.existsById(note.getId()),
				"No note is available for this note ID");
		List<Note> fullNote = iNoteElasticSearchRepository.getById(noteId);
		logger.info("Ended after getting a Note");
		return fullNote;
	}

	/*********************************************************************************************************************
	 * <p>
	 * Function is to get all notes by Id
	 * </p>
	 * 
	 * @param userId
	 * @throws ToDoException
	 */
	@Override
	public List<Note> getAllNotesById(String userId) throws ToDoException {
		logger.info("Inside getAllNoteById method");
		List<Note> list = iNoteElasticSearchRepository.findAllByUserId(userId);
		RestPreconditions.checkNotNull(list, "NoValueException : No Any Note is present for the given User Id");
		List<Note> noteList = new ArrayList<Note>();
		//stream api
		noteList=list.stream().filter(streamList->streamList.isTrashStatus()==false).collect(Collectors.toList());
		
		RestPreconditions.checkNotNull(noteList, "No any List is present for given user ID");
		System.out.println(noteList);
		logger.info("Ended after getting all Note");
		return noteList;
	}

	/*********************************************************************************************************************
	 * <p>
	 * Function is to update Note for the authorised and logined user.
	 * </p>
	 * 
	 * @param note
	 * @param userId
	 */
	@Override
	public void updateNote(NoteDTO noteDto, String userId) throws ToDoException {
		logger.info("Inside updateNote method");
		RestPreconditions.checkNotNull(noteDto.getId(), "NullPointerException : noteId must not be null.");
		RestPreconditions.checkNotNull(noteDto.getTitle(),
				"NullPointerException : Title can be empty string but not null.");
		RestPreconditions.checkNotNull(noteDto.getDescription(),
				"Null Exception : Description can be empty string but not null");
		Optional<Note> repositoryNote = iNoteElasticSearchRepository.findById(noteDto.getId());
		RestPreconditions.checkNotNull(repositoryNote, "repositoryNote is not present");
		if (!repositoryNote.isPresent()) {
			throw new ToDoException("note is not present for the given Note Id");
		}
		if (!noteDto.getTitle().equals("")) {
			repositoryNote.get().setTitle(noteDto.getTitle());
		}
		if (!noteDto.getDescription().equals("")) {
			repositoryNote.get().setTitle(noteDto.getTitle());
		}
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		repositoryNote.get().setLastDatModified(timeStamp);
		Note updated = repositoryNote.get();
		iNoteRepository.save(updated);
		iNoteElasticSearchRepository.save(updated);
		logger.info("Ended after updating a Note");
	}

	/*********************************************************************************************************************
	 * <p>
	 * Function is to delete note by Id.
	 * </p>
	 * 
	 * @param notedId
	 * @param userId
	 * @throws ToDoException
	 */
	@Override
	public void deleteNote(String noteId, String userId) throws ToDoException {
		logger.info("Inside deleteNote method");
		if (!iNoteElasticSearchRepository.existsById(userId)) {
			throw new ToDoException("Note is not Available for this Note Id");
		}
		if (iNoteElasticSearchRepository.existsById(noteId)) {
			Optional<Note> note = iNoteElasticSearchRepository.findById(noteId);
			note.get().setTrashStatus(true);
			iNoteRepository.save(note.get());
			iNoteElasticSearchRepository.save(note.get());
		}
		logger.info("Ended after deleting a Note");
	}

	/************************************************************************************************************************
	 * <p>
	 * Function is to move note to the Archieve by Id.
	 * </p>
	 * 
	 * @param notedId
	 * @param userId
	 * @throws ToDoException
	 */
	@Override
	public void moveToArchieve(String noteId, String userId) throws ToDoException {
		logger.info("Inside moveToArchieve method");
		RestPreconditions.isPresentInDB(iNoteElasticSearchRepository.existsById(userId),
				"No Value Present : Given User Id doesn't exists in our database");
		RestPreconditions.isPresentInDB(iNoteElasticSearchRepository.existsById(noteId),
				"No Value Present : Given Note Id doesn't exists in our database");

		Optional<Note> noteObject = iNoteElasticSearchRepository.findById(noteId);
		RestPreconditions.checkNotNull(noteObject, "NullPointerException : Note is not present for this note Id");
		NoteArchieveDTO note = modelMapperService.map(noteObject.get(), NoteArchieveDTO.class);
		iNoteArchiveRepository.insert(note);
		iNoteRepository.deleteById(noteId);
		iNoteElasticSearchRepository.deleteById(noteId);
		logger.info("Ended after deleting a Note and stored in trash memory");
	}

	/************************************************************************************************************************
	 * <p>
	 * Function is to restore note from the Archieve by note Id.
	 * </p>
	 * 
	 * @param notedId
	 * @param userId
	 * @throws ToDoException
	 */
	@Override
	public void restoreFromArchieve(String noteId, String userId) throws ToDoException {
		logger.info("Inside moveToArchieve method");
		RestPreconditions.isPresentInDB(iNoteRepository.existsById(userId),
				"No Value Present : Given User Id doesn't exists in our database");
		RestPreconditions.isPresentInDB(iNoteRepository.existsById(noteId),
				"No Value Present : Given Note Id doesn't exists in our database");

		Optional<NoteArchieveDTO> noteObject = iNoteArchiveRepository.findById(noteId);
		RestPreconditions.checkNotNull(noteObject, "NullPointerException : Note is not present for this note Id");
		Note note = modelMapperService.map(noteObject.get(), Note.class);
		iNoteRepository.insert(note);
		iNoteArchiveRepository.deleteById(noteId);
		iNoteElasticSearchRepository.deleteById(noteId);
		logger.info("Ended after restoring a Note.");

	}

	/**
	 * @param token
	 * @param noteId
	 *            <p>
	 *            <b>To pin the particular note</b>
	 *            </p>
	 * @throws ToDoExceptions
	 **/
	@Override
	public void pinNote(String noteId, String userId) throws ToDoException {
		RestPreconditions.checkNotNull(userId, "NULLPointerException :token is null");
		RestPreconditions.checkNotNull(noteId, "NULLPointerException :noteId is null");

		Optional<Note> optionalNote = iNoteElasticSearchRepository.findById(noteId);
		RestPreconditions.isPresentInDB(!optionalNote.isPresent(),
				"NullPointerException: Note id not present in database");

		Note note = optionalNote.get();
		RestPreconditions.isPresentInDB(optionalNote.get().isTrashStatus(), "DatabaseException : Note is in trash");
		RestPreconditions.isPresentInDB(note.isPinNote(), "DatabaseException: Note is already pinned");
		if (iNoteArchiveRepository.existsById(noteId)) {
			restoreFromArchieve(noteId, userId);
		}
		note.setPinNote(true);
		iNoteRepository.save(note);
		iNoteElasticSearchRepository.save(note);
	}

	@Override
	public void unPinNote(String noteId, String userId) throws ToDoException {
		RestPreconditions.checkNotNull(noteId, "NULLPointerException :noteId is null");
		RestPreconditions.checkNotNull(userId, "NULLPointerException :User id not present in database");

		Optional<Note> optionalNote = iNoteElasticSearchRepository.findById(noteId);
		RestPreconditions.checkNotNull(optionalNote, "NullPointerException: Note id not present in database");

		Note note = optionalNote.get();
		if (optionalNote.get().isTrashStatus()) {
			throw new ToDoException("DatabaseException : Note is in trash");
		}
		if (!note.isPinNote()) {
			throw new ToDoException("DatabaseException: Note is not pinned");
		}
		if (note.isPinNote()) {
			note.setPinNote(false);
		}
		iNoteRepository.save(note);
		iNoteElasticSearchRepository.save(note);
	}

	/****************************************************************************************************************************
	 * <p>
	 * Function is to addLabel
	 * </p>
	 * 
	 * @param noteId
	 * @param req
	 * @throws ToDoException
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void addLabel(String userId, String noteId, String labelName) throws ToDoException {

		RestPreconditions.isPresentInDB(!iNoteElasticSearchRepository.existsById(noteId),
				"NoValuePresentException : Note is not present for the given note Id");
		Optional<Note> optionalNote = iNoteElasticSearchRepository.findById(noteId);
		List<Label> list = optionalNote.get().getLabel();
		if (iLabelElasticRepository.existsByUserIdAndLabelName(userId, labelName)) {
			//stream api
			List<Label>labelList=list.stream().filter(filterList->filterList.equals(labelName)==true).collect(Collectors.toList());
			
			if(labelList!=null) {
				throw new ToDoException("Label Name Already Exist");
			}		
			Label label = new Label();
			label.setLabelName(labelName);
			list.add(label);
			optionalNote.get().setLabel(list);
		} else {
			Label label = new Label();
			label.setLabelName(labelName);
			label.setUserId(userId);
			iNoteLabelRepository.insert(label);
			iLabelElasticRepository.save(label);
			list.add(label);
			optionalNote.get().setLabel(list);
		}
		iNoteRepository.save(optionalNote.get());
		iNoteElasticSearchRepository.save(optionalNote.get());
	}

	/**
	 * @param labelName
	 * @param userId
	 *            <p>
	 *            <b>Function is to create new label </b>
	 *            </p>
	 * @throws ToDoException
	 */
	@Override
	public void createLabel(String labelName, String userId) throws ToDoException {
		RestPreconditions.checkNotNull(labelName, "NoValueException : Label Name cann't be empty");
		Label labelObject = iNoteLabelRepository.findByLabelNameAndUserId(labelName, userId);
		if (labelObject == null) {
			Label label = new Label();
			label.setLabelName(labelName);
			label.setUserId(userId);
			iNoteLabelRepository.insert(label);
			iLabelElasticRepository.save(label);
		} else {
			throw new ToDoException("Exception : Label Already Exists");
		}
	}

	/**************************************************************************************************************************
	 * @param reminder
	 * @param noteId
	 * @param userId
	 *            <p>
	 *            Function is to add reminder for the given note Id
	 *            </p>
	 * @throws ToDoException
	 */
	@Override
	public void addReminder(RemindMe reminder, String noteId, String userId) throws ToDoException {
		if (!iNoteElasticSearchRepository.existsById(noteId)) {
			throw new ToDoException("NullPointerException : noteId Doesn't Exists in our database");
		}
		// RestPreconditions.isPresentInDB(iNoteRepository.existsById(noteId),
		// "NullPointerException : noteId Doesn't Exists in our database");
		Optional<Note> noteObject = iNoteElasticSearchRepository.findById(noteId);
		noteObject.get().setRemindMe(reminder);
		iNoteRepository.save(noteObject.get());
		iNoteElasticSearchRepository.save(noteObject.get());
	}

	/**************************************************************************************************************************
	 * @param noteId
	 * @param userId
	 *            <p>
	 *            Function is to remove reminder from the given note Id
	 *            </p>
	 * @throws ToDoException
	 */
	@Override
	public void removeReminder(String noteId, String userId) throws ToDoException {
		RestPreconditions.isPresentInDB(!iNoteElasticSearchRepository.existsById(noteId),
				"NullPointerException : noteId Doesn't Exists in our database");
		Optional<Note> noteObject = iNoteElasticSearchRepository.findById(noteId);
		noteObject.get().setRemindMe(null);
		iNoteRepository.save(noteObject.get());
		iNoteElasticSearchRepository.save(noteObject.get());
	}

	/****************************************************************************************************************************
	 * @param req
	 *            <p>
	 *            Function is to read notes from Archive.
	 *            </p>
	 * @return
	 * @throws ToDoException
	 */
	@Override
	public List<Note> readArchive(String userId) throws ToDoException {
		RestPreconditions.isPresentInDB(iNoteArchiveRepository.existsById(userId),
				"NullPointerException : Note is not present for the given user Id");
		List<Note> archiveList = iNoteArchiveRepository.findAllByUserId(userId);
		return archiveList;
	}

	/****************************************************************************************************************************
	 * @param noteId
	 * @param userId
	 * @param labelName
	 *            <p>
	 *            Function is to delete label from repository.
	 *            </p>
	 * @throws ToDoException
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void deleteLabel(String noteId, String userId, String labelName) throws ToDoException {
		RestPreconditions.checkNotNull(iNoteElasticSearchRepository.existsById(userId),
				"NullPointerException : userId Doesn't Exists in our database");
		RestPreconditions.checkNotNull(iNoteElasticSearchRepository.existsById(noteId),
				"NullPointerException : noteId Doesn't Exists in our database");
		List<Note> listNote = iNoteElasticSearchRepository.findAllByUserId(userId);
		for (int i = 0; i < listNote.size(); i++) {
			Note note = listNote.get(i);
			List<Label> list = note.getLabel();
			for (int j = 0; j < list.size(); j++) {
				if (labelName.equals(list.get(j))) {
					list.get(j).setLabelName(null);
					list.get(j).setLabelId(null);
				}
			}
			note.setLabel(list);
			iNoteRepository.save(note);
			iNoteElasticSearchRepository.save(note);
		}

		Label label = iLabelElasticRepository.findByLabelNameAndUserId(labelName, userId);
		iNoteLabelRepository.delete(label);
		iLabelElasticRepository.delete(label);
	}

	/********************************************************************************************************************************
	 * @param noteId
	 * @param userId
	 * @param labelName
	 *            <p>
	 *            Function is to delete label from note.
	 *            </p>
	 * @throws ToDoException
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void deleteLabelFromNote(String noteId, String userId, String labelName) throws ToDoException {
		RestPreconditions.checkNotNull(noteId, "NullPointerException : noteId Doesn't Exists in our database");
		Optional<Note> noteObject = iNoteElasticSearchRepository.findById(noteId);
		if (noteObject.get().getLabel() == null) {
			throw new ToDoException("No Label is present");
		}
		List<Label> list = noteObject.get().getLabel();
		//List<Label> finalList=list.stream().filter(str->labelName.equals(str.getLabelName())==true).collect(Collectors.toList());
		//finalList.stream().filter(filterList->finalList.setLabelName(null)).collect(Collectors.toList());
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(labelName)) {
				list.get(i).setLabelName(null);
			}
		}
		noteObject.get().setLabel(list);
		iNoteRepository.save(noteObject.get());
		iNoteElasticSearchRepository.save(noteObject.get());
	}

	/********************************************************************************************************************************
	 * @param userId
	 *            <p>
	 *            Function is to read all the labels.
	 *            </p>
	 * @return list
	 * @throws ToDoException
	 */
	@Override
	public List<LabelDTO> readAllLabels(String userId) throws ToDoException {
		// RestPreconditions.isPresentInDB(iNoteRepository.existsById(userId),
		// "NullPointerException : noteId Doesn't Exists in our database");
		RestPreconditions.isPresentInDB(iLabelElasticRepository.existsById(userId),
				"NullPointerException : User Id Doesn't Exists in our database");
		List<LabelDTO> list = iLabelElasticRepository.findAllByUserId(userId);
		return list;
	}

	@Override
	public List<Description> noteLink(List<String> list) throws IOException {
		List<Description> lists=new ArrayList<>();
		if(list==null) {
			return null;
		}
		Description description=new Description();
		for (int i = 0; i < list.size(); i++) {
			Document doc = Jsoup.connect(list.get(0).toString()).get();
			@SuppressWarnings("unused")
			Elements element = doc.select("title");
			String title = doc.select("title").text();
			description.setTitle(title);
			Element method = doc.select("img").first();
			String img = method.attr("src");
			description.setImage(img);
			lists.add(description);
		}
		return lists;
	}
}
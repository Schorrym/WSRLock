package de.mariokramer.wsrlock.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.persistence.DocumentDao;

/**
 * This Controller is for HTTP related AJAX Requests only.
 * No WebSocket implementation here
 * @author Mario Kramer
 *
 */
@Controller
public class MainController {
	
	private static final Logger log = LoggerFactory.getLogger(MainController.class);

	@Autowired
	private DocumentDao docDao;
	
	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public String redirectStart(Map<String, Object> model){ 
		//All Documents will be transmitted to the start.jsp page
		model.put("documents", docDao.findAll());
		
		return "start";
	}
	
	@RequestMapping(value = "/readDoc", method = RequestMethod.GET)
	public String redirectReadDoc(Map<String, Object> model,
			@RequestParam(value="docId", required=true) Long docId) {
		model.put("currentDoc", docDao.findOne(docId));
		return "readdoc";
	}
	
	@RequestMapping(value = "/changeDoc", method = RequestMethod.GET)
	public @ResponseBody Document deleteDoc(Map<String, Object> model,
			@RequestParam(value="task", required=true) String task,
			@RequestParam(value="delDocId", required=false) Long docId,
			@RequestParam(value="addDocName", required=false) String docName,
			@RequestParam(value="addDocValue", required=false) String docValue){
		
		//Adding a new Document from Start.jsp page, to Database
		if(task.equals("addDocument")){
			Document doc = new Document(docName);
			doc.setDocValue(docValue);
			docDao.save(doc);
			model.put("newDoc", docDao.findByDocName(docName));
			log.info("New Document added to database");
			return docDao.findByDocName(docName);
		//Deleting a Document by given docId from the database
		}else if (task.equals("deleteDocument")) {
			if(docDao.findOne(docId) != null) {
				docDao.delete(docId);
				log.info("Document with ID-"+docId+" was deleted");
			}else{
				log.error("Document with ID-"+docId+" was not found in Database");
			}
		}else{
			log.info("No task was given, nothing to do");
		}
		return null;
	}
}

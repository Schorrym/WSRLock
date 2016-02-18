package de.mariokramer.wsrlock.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.persistence.DocumentDao;

@Controller
public class MainController {
	
	private static final Logger log = LoggerFactory.getLogger(MainController.class);

	@Autowired
	private DocumentDao docDao;
	
	//HTTP URL Mapping
	
	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public String redirectStart(Map<String, Object> model){ 
		model.put("documents", docDao.findAll());
		
		return "start";
	}
	
	@RequestMapping(value = "/readdoc", method = RequestMethod.GET)
	public String redirectReadDoc() {
		
		return "readdoc";
	}
	
	@RequestMapping(value = "/changeDoc", method = RequestMethod.GET)
	@ResponseBody
	public void deleteDoc(Map<String, Object> model,
			@RequestParam(value="task", required=true) String task,
			@RequestParam(value="delDocId", required=false) Long docId,
			@RequestParam(value="addDocName", required=false) String docName,
			@RequestParam(value="addDocValue", required=false) String docValue){
		
		//Adding a new Document from Start.jsp page, to Database
		if(task.equals("addDocument")){
			Document doc = new Document(docName);
			doc.setDocValue(docValue);
			docDao.save(doc);
//			Long newId = docDao.findByDocName(docName).getDocId();
//			model.put("newDocId", newId);
			log.info("New Document added to database");
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
	}
	
	//WebSocket URL Mapping
	
	@SubscribeMapping("/marco")
	public Shout handleSubscription(){
		Shout outgoing = new Shout();
		outgoing.setMessage("Polo!");
		return outgoing;
	}
	
	@MessageMapping("/marco")
	@SendTo("/topic/shout")
	public Shout handleSubscription(Shout incoming) {
		log.info("Received Message: " + incoming.getMessage());
		Shout outgoing = new Shout();
		outgoing.setMessage("Polo!");
		return outgoing;
	}
}

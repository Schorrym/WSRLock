package de.mariokramer.wsrlock.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.persistence.DocumentDao;

/**
 * This Controller is for HTTP related redirect Requests only.
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
		//Shorten the documents value for the preview
		Iterable<Document> docs = docDao.findAll();
		for(Document doc : docs){
			if(doc.getDocValue().length() > 64){
				String docValue = doc.getDocValue().substring(0, 32);
				doc.setDocValue(docValue + "...");
			}			
		}
		//All Documents will be transmitted to the start.jsp page
		model.put("documents", docs);
		
		return "start";
	}
	
	@RequestMapping(value = "/readDoc", method = RequestMethod.GET)
	public String redirectReadDoc(Map<String, Object> model,
			@RequestParam(value="docId", required=true) Long docId) {
		model.put("currentDoc", docDao.findOne(docId));
		return "readdoc";
	}
	
}

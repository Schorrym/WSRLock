package de.mariokramer.wsrlock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.persistence.DocumentDao;

/**
 * This Controller is for HTTP related redirect Requests only.
 * No WebSocket implementation here
 * @author Mario Kramer
 *
 */
@RestController
public class MainController {

	@Autowired
	private DocumentDao docDao;
	
	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public ModelAndView redirectStart(ModelAndView model){ 		
		//Shorten the documents value for the preview
		Iterable<Document> docs = docDao.findAll();
		for(Document doc : docs){
			if(doc.getDocValue().length() > 64){
				String docValue = doc.getDocValue().substring(0, 32);
				doc.setDocValue(docValue + "...");
			}			
		}
		//All Documents will be transmitted to the start.jsp page
		model.addObject("documents", docs);
		model.setViewName("start");
		
		return model;
	}
	
	@RequestMapping(value = "/readDoc", method = RequestMethod.GET)
	public ModelAndView redirectReadDoc(ModelAndView model,
			@RequestParam(value="docId", required=true) Long docId) {

		model.addObject("currentDoc", docDao.findOne(docId));
		model.setViewName("readdoc");
		return model;
	}
	
}

package de.mariokramer.wsrlock.persistence;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.model.Message;
import de.mariokramer.wsrlock.model.Users;

@Service
public class DocumentFeedServiceImpl implements DocumentFeedService {

	private SimpMessageSendingOperations messaging;
	
	@Autowired
	public DocumentFeedServiceImpl(SimpMessageSendingOperations messaging) {
		this.messaging = messaging;
	}

	@Override
	public void broadcastDocument(Document doc) {
		messaging.convertAndSend("/topic/addDoc", doc);
	}

	@Override
	public void deleteDocument(Long docId) {
		messaging.convertAndSend("/topic/delDoc", docId);
	}
	
	@Override
	public void broadcastUsers(Long docId, List<Users> users, String task) {
		messaging.convertAndSend("/topic/doc"+docId, users);
	}

	@Override
	public void lockDockument(Long docId, Message msg, String task) {
		messaging.convertAndSend("/topic/doc"+docId, msg);
	}

	@Override
	public void saveDocument(Long docId, Document newDoc, String task) {
		messaging.convertAndSend("/topic/doc"+docId, new GenericMessage<Document>(newDoc, setHeader(task)));		
	}
	
	//Set manual header
	private Map<String,Object> setHeader(String value){
		Map<String, List<String>> nativeHeaders = new HashMap<>();
		nativeHeaders.put("task", Collections.singletonList(value));

		Map<String,Object> headers = new HashMap<>();
		headers.put(NativeMessageHeaderAccessor.NATIVE_HEADERS, nativeHeaders);
		
		return headers;
	}
}
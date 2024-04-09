package com.norbu.note;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.norbu.note.service.NoteService;

public class NoteLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


    private NoteService noteService;
    public NoteLambda(NoteService noteService){
        if(noteService==null){
            this.noteService= new NoteService();
        }else{
            this.noteService=noteService;
        }
    }

    public NoteLambda(){
        this(null);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {


        LambdaLogger logger = context.getLogger();
        String userId = event.getRequestContext().getAccountId();
        logger.log("Running in region: "+System.getenv("VAR_AWS_REGION"));
        logger.log("UserId: "+userId);
        logger.log("CognitoIdentityId: "+event.getRequestContext().getIdentity().getCognitoIdentityId());
        logger.log("path: "+event.getPath());
        logger.log("parameters: "+event.getPathParameters());
        switch (event.getHttpMethod()) {

            case "POST":
                logger.log("event ::"+event);
                return noteService.saveNote(event, userId, context);

            case "GET":
                logger.log("event ::"+event);
                if (event.getPathParameters() != null && event.getPathParameters().get("noteId")!=null) {
                    context.getLogger().log("method called: noteService.getNoteById");
                    return noteService.getNoteById(event, userId, context);
                }else {
                    context.getLogger().log("method called: noteService.findAllNote");
                     return noteService.findAllNote(event, userId,context);
                }
            case "DELETE":
                logger.log("event ::"+event);
               if (event.getPathParameters() != null && event.getPathParameters().get("noteId")!=null) {
                  return noteService.deleteNote(event, userId,context);
               }
            default:
                throw new RuntimeException("Unsupported Methods:::" + event.getHttpMethod());

        }
    }
}

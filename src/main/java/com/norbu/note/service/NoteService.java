package com.norbu.note.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.norbu.note.model.Note;
import com.norbu.note.Mapper;

import java.util.List;
import java.util.Map;

public class NoteService {

    private DynamoDBMapper dynamoDBMapper;


    public NoteService(DynamoDBMapper dynamoDBMapper){
        this.dynamoDBMapper=dynamoDBMapper;
    }

    public NoteService(){
        initDynamoDB();
    }

    public APIGatewayProxyResponseEvent saveNote(APIGatewayProxyRequestEvent event,String userId, Context context) {


        Note note = Mapper.convertStringToObj(event.getBody(),context, Note.class);
        note.setUserId(userId);
        //String userId= event.getRequestContext().getIdentity().getCognitoIdentityId();
        context.getLogger().log("started to save in dynamodb:::"+note.toString() );
        dynamoDBMapper.save(note);
        String jsonBody = Mapper.convertObjToString(note,context) ;
        context.getLogger().log("data saved successfully to dynamodb:::" + jsonBody);
        return createAPIResponse(jsonBody,201,Mapper.createHeaders());
    }


    public APIGatewayProxyResponseEvent getNoteById(APIGatewayProxyRequestEvent apiGatewayRequest,String userId, Context context){

        String noteId = apiGatewayRequest.getPathParameters().get("noteId");
        Note note =   dynamoDBMapper.load(Note.class,userId,noteId) ;
        String jsonBody=null;
        if(note!=null) {
            jsonBody = Mapper.convertObjToString(note, context);
            context.getLogger().log("fetch Note By ID:::" + jsonBody);
            return createAPIResponse(jsonBody,200,Mapper.createHeaders());
        }else{
            jsonBody = "Note Not Found Exception :" + noteId;
            return createAPIResponse(jsonBody,400,Mapper.createHeaders());
        }

    }


    public APIGatewayProxyResponseEvent findAllNote(APIGatewayProxyRequestEvent apiGatewayRequest,String userId, Context context){
        initDynamoDB();
        Note findNote= new Note();
        findNote.setUserId(userId);
        DynamoDBQueryExpression<Note> queryExpression = new DynamoDBQueryExpression<Note>()
                .withHashKeyValues(findNote);
        List<Note> Notes = dynamoDBMapper.query(Note.class,queryExpression);
      //  List<Note> Notes = dynamoDBMapper.scan(Note.class,new DynamoDBScanExpression());
        String jsonBody =  Mapper.convertListOfObjToString(Notes,context);
        context.getLogger().log("fetch Note List:::" + jsonBody);
        return createAPIResponse(jsonBody,200,Mapper.createHeaders());
    }


    public APIGatewayProxyResponseEvent deleteNote(APIGatewayProxyRequestEvent event,String userId, Context context){

        String noteId = event.getPathParameters().get("noteId");
        Note note =  dynamoDBMapper.load(Note.class,userId,noteId)  ;
        if(note!=null) {
            dynamoDBMapper.delete(note);
            context.getLogger().log("data deleted successfully :::" + noteId);
            return createAPIResponse("data deleted successfully." + noteId,200,Mapper.createHeaders());
        }else{
           String jsonBody = "Note Not Found Exception :" + noteId;
            return createAPIResponse(jsonBody,400,Mapper.createHeaders());
        }
    }




    private void initDynamoDB(){
        if( dynamoDBMapper==null) {
            String region = System.getenv("VAR_AWS_REGION");

            AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.fromName(region)).build();
            dynamoDBMapper = new DynamoDBMapper(client);
        }

    }
    private APIGatewayProxyResponseEvent createAPIResponse(String body, int statusCode, Map<String,String> headers ){
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setBody(body);
        responseEvent.setHeaders(headers);
        responseEvent.setStatusCode(statusCode);
        return responseEvent;
    }


}

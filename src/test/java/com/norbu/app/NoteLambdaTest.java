package com.norbu.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.norbu.note.NoteLambda;
import com.norbu.note.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import com.amazonaws.services.lambda.runtime.tests.annotations.Event;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NoteLambdaTest {

@Mock
private transient NoteService service;
    private NoteLambda lambdaFunction;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        lambdaFunction= new NoteLambda(service);
    }

    @ParameterizedTest
    @Event(value = "save_note.json", type = APIGatewayProxyRequestEvent.class)
    void testPostNote(APIGatewayProxyRequestEvent event) {

        Context context = new TestContext();
        String body="{ \"userId\": \"339712700280\", \"noteId\": \"f0a27b43-b3a5-4d26-bf13-87a4322528d7\", \"title\": \"Biology 1\",\"body\": \"Biology 1 123\"}";
        Mockito.when(service.saveNote(Mockito.any(APIGatewayProxyRequestEvent.class),Mockito.any(String.class),Mockito.any(Context.class)))
                .thenReturn(createAPIResponse(body,201));
        APIGatewayProxyResponseEvent response = lambdaFunction.handleRequest(event, context);

        assertEquals(body, response.getBody());
    }

    @ParameterizedTest
    @Event(value = "get_note_by_id.json", type = APIGatewayProxyRequestEvent.class)
    void testGetNote(APIGatewayProxyRequestEvent event) {

        Context context = new TestContext();
        String body="{ \"userId\": \"339712700280\", \"noteId\": \"f0a27b43-b3a5-4d26-bf13-87a4322528d7\", \"title\": \"Biology 1\",\"body\": \"Biology 1 123\"}";
        Mockito.when(service.getNoteById(Mockito.any(APIGatewayProxyRequestEvent.class),Mockito.any(String.class),Mockito.any(Context.class)))
                .thenReturn(createAPIResponse(body,200));
        APIGatewayProxyResponseEvent response = lambdaFunction.handleRequest(event, context);

        assertEquals(body, response.getBody());
    }

    @ParameterizedTest
    @Event(value = "get_all_notes.json", type = APIGatewayProxyRequestEvent.class)
    void testGetNoteList(APIGatewayProxyRequestEvent event) {

        Context context = new TestContext();
        String body="[{ \"userId\": \"339712700280\", \"noteId\": \"f0a27b43-b3a5-4d26-bf13-87a4322528d7\", \"title\": \"Biology 1\",\"body\": \"Biology 1 123\"}," +
                "{ \"userId\": \"339712700280\", \"noteId\": \"66a27b43-b3a5-4d26-bf13-87a4322528d7\", \"title\": \"Biology 2\",\"body\": \"Biology 2 123\"}";
        Mockito.when(service.findAllNote(Mockito.any(APIGatewayProxyRequestEvent.class),Mockito.any(String.class),Mockito.any(Context.class)))
                .thenReturn(createAPIResponse(body,200));
        APIGatewayProxyResponseEvent response = lambdaFunction.handleRequest(event, context);

        assertEquals(body, response.getBody());
    }

    @ParameterizedTest
    @Event(value = "delete_note.json", type = APIGatewayProxyRequestEvent.class)
    void testDeleteNote(APIGatewayProxyRequestEvent event) {

        Context context = new TestContext();
        String body="data deleted successfully.3d9b0e56-3747-4f5a-9917-ab3acbd3103e";
        Mockito.when(service.deleteNote(Mockito.any(APIGatewayProxyRequestEvent.class),Mockito.any(String.class),Mockito.any(Context.class)))
                .thenReturn(createAPIResponse(body,200));
        APIGatewayProxyResponseEvent response = lambdaFunction.handleRequest(event, context);

        assertEquals(body, response.getBody());
    }


    @ParameterizedTest
    @Event(value = "invalid_http_method.json", type = APIGatewayProxyRequestEvent.class)
    void testInvalidHttpMethodException(APIGatewayProxyRequestEvent event) {

        Context context = new TestContext();
        assertThrows(RuntimeException.class, ()->lambdaFunction.handleRequest(event, context));


    }


    private APIGatewayProxyResponseEvent createAPIResponse(String body, int statusCode){
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        headers.put("X-amazon-author","nt");
        headers.put("X-amazon-apiVersion","v1");
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setBody(body);
        responseEvent.setHeaders(headers);
        responseEvent.setStatusCode(statusCode);
        return responseEvent;
    }
}

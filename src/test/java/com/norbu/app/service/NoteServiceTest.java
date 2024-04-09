package com.norbu.app.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.tests.annotations.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.norbu.app.TestContext;
import com.norbu.note.model.Note;
import com.norbu.note.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NoteServiceTest {

    @Mock
    private transient DynamoDBMapper dynamoDBMapper;

    private NoteService service;


    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        service= new NoteService(dynamoDBMapper);
    }


    @ParameterizedTest
    @Event(value = "save_note.json", type = APIGatewayProxyRequestEvent.class)
    public void testSaveNote(APIGatewayProxyRequestEvent event) throws JsonProcessingException {
        Context context = new TestContext();

        Mockito.doNothing().when(dynamoDBMapper).save(Mockito.any(Note.class));
        Note note=new ObjectMapper().readValue(event.getBody(),Note.class);
        note.setUserId("123");

        APIGatewayProxyResponseEvent response=service.saveNote(event,"123",context);

        assertEquals(new ObjectMapper().writeValueAsString(note), response.getBody());
    }


}

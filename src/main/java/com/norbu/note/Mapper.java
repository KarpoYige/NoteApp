package com.norbu.note;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.norbu.note.model.Note;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapper {


    public static Map<String,String> createHeaders(){
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        headers.put("X-amazon-author","nt");
        headers.put("X-amazon-apiVersion","v1");
        return  headers ;
    }

    public static <T> T convertStringToObj(String jsonBody, Context context,Class<T> valueType){
        try {
           return new ObjectMapper().readValue(jsonBody,valueType);
        } catch (JsonProcessingException e) {
            context.getLogger().log( "Error while converting string to obj:::" + e.getMessage());
        }

        return null;

    }

    public static String convertObjToString(Note note, Context context){
        String jsonBody = null;
        try {
            jsonBody =   new ObjectMapper().writeValueAsString(note);
        } catch (JsonProcessingException e) {
            context.getLogger().log( "Error while converting obj to string:::" + e.getMessage());
        }
        return jsonBody;
    }


    public static String convertListOfObjToString(List<Note> noteList, Context context){
        String jsonBody = null;
        try {
            jsonBody =   new ObjectMapper().writeValueAsString(noteList);
        } catch (JsonProcessingException e) {
            context.getLogger().log( "Error while converting obj to string:::" + e.getMessage());
        }
        return jsonBody;
    }



}

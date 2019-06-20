package com.payeezy.client.domain.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;

@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class Error implements Cloneable {
    @Override
    protected Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }


    @JsonProperty("messages")
    private ArrayList<String> messages;


    public ArrayList<String> getMessages() {
        return messages;
    }


    public void setMessage(ArrayList<String> message) {
        this.messages = message;
    }
}

package org.exampl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseBean {

    private Boolean status;
    private String message;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ResponseBean toError(String message){
        ResponseBean responseBean = new ResponseBean();
        responseBean.setStatus(false);
        responseBean.setMessage(message);
        return responseBean;
    }

    public static ResponseBean toSuccess(){
        ResponseBean responseBean = new ResponseBean();
        responseBean.setStatus(true);
        responseBean.setMessage("Request processed successfully");
        return responseBean;
    }
    @Override
    public String toString() {

        String value = null;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            value = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return value;

    }

}

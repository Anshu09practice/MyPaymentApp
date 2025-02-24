package com.mypayment.withdrawService.DTO;


public class responseDTO {
    private String statusCode;
    private String statusMessage;
    private String responseMessage;

    public responseDTO() {
    }

    public responseDTO(String statusCode, String statusMessage, String responseMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.responseMessage = responseMessage;
    }


    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}

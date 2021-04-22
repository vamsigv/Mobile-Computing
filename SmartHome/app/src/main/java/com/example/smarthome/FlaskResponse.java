package com.example.smarthome;

import com.google.gson.annotations.SerializedName;

class FlaskResponse {

    @SerializedName("success")
    boolean success;
    @SerializedName("message")
    String message;

    String getMessage() {
        return message;
    }

    boolean getSuccess() {
        return success;
    }

}
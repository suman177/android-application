package com.example.appstone;

class Messages {
    private String from, message, timeStamp, key;

    public Messages()
    {

    }

    public Messages( String from, String message, String timeStamp,String key) {
        this.message = message;
        this.from = from;
        this.key=key;
       this.timeStamp=timeStamp;

    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getKey(){return key; }

    public void setKey(String key){this.key=key;}

}

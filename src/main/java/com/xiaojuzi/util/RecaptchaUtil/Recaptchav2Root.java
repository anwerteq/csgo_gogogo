package com.xiaojuzi.util.RecaptchaUtil;


public class Recaptchav2Root {


    public static Recaptchav2Root getRecaptchav2Root(){
        Recaptchav2Root recaptchav2Root = new Recaptchav2Root();
        recaptchav2Root.setTask(new Task());
        return recaptchav2Root;
    }
    private String clientKey = "f7a920a4720b17f65b77c325d6c0f0f7";

    private Task task;

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientKey() {
        return this.clientKey;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return this.task;
    }
}



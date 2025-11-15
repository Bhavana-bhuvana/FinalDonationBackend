package com.komal.template_backend.model;

public class MessageItem {
    private String text;

    public MessageItem() {}

    public MessageItem(String text) {
        this.text = text;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}

// src/main/java/com/hsnr/webshop/gui/dto/FeedbackDTO.java
package com.hsnr.webshop.gui.dto;

import java.io.Serializable;

public class FeedbackDTO implements Serializable {
    private String text;
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
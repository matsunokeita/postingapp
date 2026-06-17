package com.example.postingapp.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostRegisterForm {
    @NotBlank(message = "タイトルを入力してください。")
    private String title;

    @NotBlank(message = "本文を入力してください。")
    private String content;
}
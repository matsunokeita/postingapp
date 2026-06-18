package com.example.postingapp.form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostEditForm {
    @NotBlank(message = "タイトルを入力してください。")
    private String title;

    @NotBlank(message = "本文を入力してください。")
    private String content;
}
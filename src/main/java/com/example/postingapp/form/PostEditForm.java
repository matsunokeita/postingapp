package com.example.postingapp.form;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class PostEditForm {
    @NotBlank(message = "タイトルを入力してください。")
    @Size(max = 40, message = "タイトルは40文字以内で入力してください。")
    private String title;
    @NotBlank(message = "本文を入力してください。")
    @Size(max = 200, message = "本文は200文字以内で入力してください。")
    private String content;
}
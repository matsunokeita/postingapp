package com.example.postingapp.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class SignupForm {
    @NotBlank(message = "ユーザー名を入力してください。")
    private String name;

    @NotBlank(message = "メールアドレスを入力してください。")
    @Email(message = "メールアドレスは正しい形式で入力してください。")
    private String email;

    @NotBlank(message = "パスワードを入力してください。")
    @Length(min = 8, message = "パスワードは8文字以上で入力してください。")
    private String password;

    @NotBlank(message = "パスワード（確認用）を入力してください。")
    private String passwordConfirmation;
}
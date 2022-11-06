package com.ssafy.pocketfolio.api.dto.response;

import com.ssafy.pocketfolio.db.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Tag(name = "UserDto", description = "유저 Dto (res + req)")
public class UserListRes {
    @Email
    @Schema(description = "이메일", nullable = false, maxLength = 50, example = "test@test.com")
    private String email;

    @Schema(description = "이름", nullable = false, maxLength = 12)
    private String name;

    @Schema(description = "프로필 사진 파일 url", maxLength = 255, example = "/img/J2EeRo2d.jpg")
    private String profilePic;

    @Schema(description = "자기소개", maxLength = 200)
    private String describe;

//    @Schema(description = "마이룸 목록")
//    private List<RoomRes> rooms;

    public UserListRes(User user) {
        email = user.getEmail();
        name = user.getName();
        profilePic = user.getProfilePic();
        describe = user.getDescribe();
    }
}
package com.ll.domain.member.member.controller;

import com.ll.domain.member.member.entity.Member;
import com.ll.domain.member.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ApiV1MemberControllerTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("회원가입")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/join")
                                .content("""
                                        {
                                            "username": "usernew",
                                            "password": "1234",
                                            "nickname": "무명"
                                        }
                                        """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        Member member = memberService.findByUsername("usernew").get();

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.getNickname())))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(member.getId()))
                .andExpect(jsonPath("$.data.createDate").value(Matchers.startsWith(member.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.modifyDate").value(Matchers.startsWith(member.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.nickname").value(member.getNickname()));
    }

    @Test
    @DisplayName("회원가입 without username, password, nickname")
    void t2() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/join")
                                .content("""
                                        {
                                            "username": "",
                                            "password": "",
                                            "nickname": ""
                                        }
                                        """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("""
                        nickname-NotBlank-must not be blank
                        password-NotBlank-must not be blank
                        username-NotBlank-must not be blank
                        """.stripIndent().trim()));
    }

    @Test
    @DisplayName("회원가입 시 이미 사용중인 username, 409")
    void t3() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/join")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234",
                                            "nickname": "무명"
                                        }
                                        """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("409-1"))
                .andExpect(jsonPath("$.msg").value("해당 username은 이미 사용중입니다."));
    }

    @Test
    @DisplayName("로그인")
    void t4() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        Member member = memberService.findByUsername("user1").get();

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%s님 환영합니다.".formatted(member.getNickname())))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.item").exists())
                .andExpect(jsonPath("$.data.item.id").value(member.getId()))
                .andExpect(jsonPath("$.data.item.createDate").value(Matchers.startsWith(member.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.item.modifyDate").value(Matchers.startsWith(member.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.item.nickname").value(member.getNickname()))
                .andExpect(jsonPath("$.data.apiKey").value(member.getApiKey()))
                .andExpect(jsonPath("$.data.accessToken").exists());

        resultActions.andExpect(
                result -> {
                    Cookie accessTokenCookie = result.getResponse().getCookie("accessToken");
                    assertThat(accessTokenCookie.getValue()).isNotBlank();
                    assertThat(accessTokenCookie.getPath()).isEqualTo("/");
                    assertThat(accessTokenCookie.isHttpOnly()).isTrue();
                    assertThat(accessTokenCookie.getSecure()).isTrue();

                    Cookie apiKeyCookie = result.getResponse().getCookie("apiKey");
                    assertThat(apiKeyCookie.getValue()).isEqualTo(member.getApiKey());
                    assertThat(apiKeyCookie.getPath()).isEqualTo("/");
                    assertThat(apiKeyCookie.isHttpOnly()).isTrue();
                    assertThat(apiKeyCookie.getSecure()).isTrue();
                });
    }

    @Test
    @DisplayName("로그인, without username")
    void t5() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/login")
                                .content("""
                                        {
                                            "username": "",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("username-NotBlank-must not be blank"));
    }

    @Test
    @DisplayName("로그인, without password")
    void t6() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": ""
                                        }
                                        """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("password-NotBlank-must not be blank"));
    }

    @Test
    @DisplayName("로그인, with wrong username")
    void t7() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/login")
                                .content("""
                                        {
                                            "username": "user0",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 사용자입니다."));
    }

    @Test
    @DisplayName("로그인, with wrong password")
    void t8() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1"
                                        }
                                        """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-2"))
                .andExpect(jsonPath("$.msg").value("비밀번호가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("내 정보, with user1")
    void t9() throws Exception {
        Member actor = memberService.findByUsername("user2").get();
        String actorAuthToken = memberService.genAuthToken(actor);

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/members/me")
                                .header("Authorization", "Bearer " + actorAuthToken)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(actor.getId()))
                .andExpect(jsonPath("$.createDate").value(Matchers.startsWith(actor.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.modifyDate").value(Matchers.startsWith(actor.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.nickname").value(actor.getNickname()));
    }

    @Test
    @DisplayName("내 정보, with user2")
    void t10() throws Exception {
        Member actor = memberService.findByUsername("user2").get();
        String actorAuthToken = memberService.genAuthToken(actor);

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/members/me")
                                .header("Authorization", "Bearer " + actorAuthToken)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(actor.getId()))
                .andExpect(jsonPath("$.createDate").value(Matchers.startsWith(actor.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.modifyDate").value(Matchers.startsWith(actor.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.nickname").value(actor.getNickname()));
    }

    @Test
    @DisplayName("내 정보, with wrong access key")
    void t11() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/members/me")
                                .header("Authorization", "Bearer wrong-access-key")
                )
                .andDo(print());

        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"))
                .andExpect(jsonPath("$.msg").value("사용자 인증정보가 올바르지 않습니다."));
    }

    @Test
    @DisplayName("logout")
    void t12() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/members/logout")
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("로그아웃 되었습니다."))
                .andExpect(result -> {
                    Cookie accessTokenCookie = result.getResponse().getCookie("accessToken");
                    assertThat(accessTokenCookie.getValue()).isEmpty();
                    assertThat(accessTokenCookie.getMaxAge()).isEqualTo(0);
                    assertThat(accessTokenCookie.getPath()).isEqualTo("/");
                    assertThat(accessTokenCookie.isHttpOnly()).isTrue();
                    assertThat(accessTokenCookie.getSecure()).isTrue();

                    Cookie apiKeyCookie = result.getResponse().getCookie("apiKey");
                    assertThat(apiKeyCookie.getValue()).isEmpty();
                    assertThat(apiKeyCookie.getMaxAge()).isEqualTo(0);
                    assertThat(apiKeyCookie.getPath()).isEqualTo("/");
                    assertThat(apiKeyCookie.isHttpOnly()).isTrue();
                    assertThat(apiKeyCookie.getSecure()).isTrue();
                });
    }
}

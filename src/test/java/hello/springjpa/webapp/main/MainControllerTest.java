package hello.springjpa.webapp.main;

import hello.springjpa.webapp.account.AccountRepository;
import hello.springjpa.webapp.account.AccountService;
import hello.springjpa.webapp.account.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("user");
        signUpForm.setEmail("user@gmail.com");
        signUpForm.setPassword("asdfasdf");
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void AfterEach() {
        accountRepository.deleteAll();
    }

    @DisplayName("이메일 로그인 성공")
    @Test
    void login_with_email() throws Exception {

        mockMvc.perform(post("/login")
                        .param("username", "user@gmail.com")
                        .param("password","asdfasdf")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("user"))
        ;
    }

    @DisplayName("닉네임 로그인 성공")
    @Test
    void login_with_nickname() throws Exception {

        mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password","asdfasdf")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("user"))
        ;
    }

    @DisplayName("로그인 실패")
    @Test
    void login_fail() throws Exception {

        mockMvc.perform(post("/login")
                        .param("username", "111111111")
                        .param("password","22222222222222")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated())
        ;
    }

    @WithMockUser
    @DisplayName("로그아웃")
    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
 //               .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }

}
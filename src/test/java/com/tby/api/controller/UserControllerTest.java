package com.tby.api.controller;

import com.tby.api.exception.BusinessException;
import com.tby.api.exception.ErrorCode;
import com.tby.api.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("DELETE /api/user/{userId} — 成功刪除 user 及其 orders，回傳 204")
    void deleteUser_success() throws Exception {
        doNothing().when(userService).deleteUser(3L);

        mockMvc.perform(delete("/api/user/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/user/{userId} — user 不存在，回傳 404")
    void deleteUser_notFound() throws Exception {
        doThrow(new BusinessException(ErrorCode.USER_NOT_FOUND))
                .when(userService).deleteUser(999L);

        mockMvc.perform(delete("/api/user/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}

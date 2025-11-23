package com.example.ToDo_App.controller;

import com.example.ToDo_App.service.ToDoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;
import jakarta.servlet.ServletException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@WebMvcTest
public class ToDoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToDoService toDoService;


    @Test
    public void testViewAllToDoItems() throws Exception {
        mockMvc.perform(get("/viewToDoList"))
                .andExpect(status().isOk());
    }

    @Test
    public void testViewAllToDoItems_NotFound() throws Exception {
        mockMvc.perform(get("/nonExistentPath"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testViewAllToDoItems_Success() throws Exception {
        when(toDoService.getToDoItemsByStatus(anyString())).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/viewToDoList"))
                .andExpect(status().isOk());
    }

    @Test
    void testAddToDoItem_View() throws Exception {
        mockMvc.perform(get("/addToDoItem"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateToDoStatus_Success() throws Exception {
        when(toDoService.updateStatus(1L)).thenReturn(true);

        mockMvc.perform(get("/updateToDoStatus/1"))
                .andExpect(status().is3xxRedirection());

        verify(toDoService, times(1)).updateStatus(1L);
    }


    @Test
    void testUpdateToDoStatus_Failure() throws Exception {
        when(toDoService.updateStatus(1L)).thenReturn(false);

        mockMvc.perform(get("/updateToDoStatus/1"))
                .andExpect(status().is3xxRedirection());

        verify(toDoService, times(1)).updateStatus(1L);
    }



}

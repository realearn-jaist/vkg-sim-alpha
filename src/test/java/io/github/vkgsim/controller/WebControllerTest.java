package io.github.vkgsim.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WebController.class)
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));
    }

    /**
     * Test the mainPage method
     */
    @Test
    public void testMainPage() throws Exception {
        mockMvc.perform(get("/mainPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));
    }

    /**
     * Test the mappingPage method
     */
    @Test
    public void testMappingPage() throws Exception {
        mockMvc.perform(get("/mappingPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("mappingPage"));
    }

    /**
     * Test the queryPage method
     */
    @Test
    public void testQueryPage() throws Exception {
        mockMvc.perform(get("/queryPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("queryPage"));
    }

    /**
     * Test the errorPage method
     */
    @Test
    public void testErrorPage() throws Exception {
        mockMvc.perform(get("/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("errorPage"));
    }
}

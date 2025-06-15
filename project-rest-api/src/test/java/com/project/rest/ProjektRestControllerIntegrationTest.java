package com.project.rest;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.model.Projekt;
import com.project.service.ProjektService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "admin")
public class ProjektRestControllerIntegrationTest {

    private final String apiPath = "/api/projekty";

    @MockitoBean
    private ProjektService mockProjektService;

    @Autowired
    private MockMvc mockMvc;

    private JacksonTester<Projekt> jacksonTester;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        System.out.printf("-- START -> %s%n", testInfo.getTestMethod().get().getName());
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JacksonTester.initFields(this, mapper);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.printf("<- END -- %s%n", testInfo.getTestMethod().get().getName());
    }

    @Test
    public void getProject_whenValidId_shouldReturnGivenProject() throws Exception {
        Projekt p = new Projekt();
        p.setProjektId(2);
        p.setNazwa("Nazwa2");
        p.setOpis("Opis2");
        p.setDataOddania(LocalDate.of(2024, 6, 7));

        when(mockProjektService.getProjekt(2)).thenReturn(Optional.of(p));

        mockMvc.perform(get(apiPath + "/{id}", 2)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projektId").value(2))
            .andExpect(jsonPath("$.nazwa").value("Nazwa2"));

        verify(mockProjektService, times(1)).getProjekt(2);
        verifyNoMoreInteractions(mockProjektService);
    }

    @Test
    public void getProject_whenInvalidId_shouldReturnNotFound() throws Exception {
        when(mockProjektService.getProjekt(99)).thenReturn(Optional.empty());

        mockMvc.perform(get(apiPath + "/{id}", 99)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(mockProjektService, times(1)).getProjekt(99);
        verifyNoMoreInteractions(mockProjektService);
    }

    @Test
    public void getProjects_whenTwoAvailable_shouldReturnPage() throws Exception {
        Projekt p1 = new Projekt(); p1.setProjektId(1); p1.setNazwa("N1");
        Projekt p2 = new Projekt(); p2.setProjektId(2); p2.setNazwa("N2");
        Page<Projekt> page = new PageImpl<>(List.of(p1, p2));

        when(mockProjektService.getProjekty(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get(apiPath)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].projektId").value(1))
            .andExpect(jsonPath("$.content[1].projektId").value(2));

        verify(mockProjektService, times(1)).getProjekty(any(Pageable.class));
        verifyNoMoreInteractions(mockProjektService);
    }

    @Test
    public void createProject_whenValidData_shouldReturnCreated() throws Exception {
        Projekt toCreate = new Projekt();
        toCreate.setNazwa("Nazwa3");
        toCreate.setOpis("Opis3");
        // dataOddania może być null lub ustawione, nie ma znaczenia dla testu

        Projekt created = new Projekt();
        created.setProjektId(3);
        when(mockProjektService.setProjekt(any())).thenReturn(created);

        String body = jacksonTester.write(toCreate).getJson();

        mockMvc.perform(post(apiPath)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString(apiPath + "/3")));
    }

    @Test
    public void createProject_whenEmptyName_shouldReturnBadRequest() throws Exception {
        Projekt bad = new Projekt();
        bad.setNazwa("");
        bad.setOpis("X");

        mockMvc.perform(post(apiPath)
                .content(jacksonTester.write(bad).getJson())
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());

        // serwis nie powinien zostać w ogóle wywołany
        verify(mockProjektService, never()).setProjekt(any());
    }


    @Test
    public void updateProject_whenValid_shouldReturnOk() throws Exception {
        Projekt existing = new Projekt();
        existing.setProjektId(5);
        existing.setNazwa("Nazwa5");
        existing.setOpis("Opis5");

        Projekt update = new Projekt();
        update.setNazwa("N5-upd");
        update.setOpis("O5-upd");

        when(mockProjektService.getProjekt(5)).thenReturn(Optional.of(existing));
        when(mockProjektService.setProjekt(any())).thenReturn(existing);

        mockMvc.perform(put(apiPath + "/{id}", 5)
                .content(jacksonTester.write(update).getJson())
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        verify(mockProjektService).getProjekt(5);
        verify(mockProjektService).setProjekt(any());
        verifyNoMoreInteractions(mockProjektService);
    }

    @Test
    public void deleteProject_whenValid_shouldReturnOk() throws Exception {
        Projekt existing = new Projekt();
        existing.setProjektId(7);
        when(mockProjektService.getProjekt(7)).thenReturn(Optional.of(existing));

        mockMvc.perform(delete(apiPath + "/{id}", 7))
            .andDo(print())
            .andExpect(status().isOk());

        verify(mockProjektService).getProjekt(7);
        verify(mockProjektService).deleteProjekt(7);
        verifyNoMoreInteractions(mockProjektService);
    }

    @Test
    public void deleteProject_whenInvalid_shouldReturnNotFound() throws Exception {
        when(mockProjektService.getProjekt(8)).thenReturn(Optional.empty());

        mockMvc.perform(delete(apiPath + "/{id}", 8))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(mockProjektService).getProjekt(8);
        verifyNoMoreInteractions(mockProjektService);
    }

    @Test
    public void getProjectsAndVerifyPagingParams() throws Exception {
        int page = 5, size = 15;
        String sort = "nazwa,desc";

        mockMvc.perform(get(apiPath)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("sort", sort))
            .andExpect(status().isOk());

        ArgumentCaptor<Pageable> cap = ArgumentCaptor.forClass(Pageable.class);
        verify(mockProjektService).getProjekty(cap.capture());
        PageRequest pr = (PageRequest) cap.getValue();
        assertEquals(page, pr.getPageNumber());
        assertEquals(size, pr.getPageSize());
        assertEquals("nazwa", pr.getSort().getOrderFor("nazwa").getProperty());
        assertEquals(Sort.Direction.DESC, pr.getSort().getOrderFor("nazwa").getDirection());
    }

}

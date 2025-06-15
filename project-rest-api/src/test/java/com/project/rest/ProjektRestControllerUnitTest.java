package com.project.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.*;

import com.project.model.Projekt;
import com.project.service.ProjektService;
import com.project.controller.ProjektRestController;

@ExtendWith(MockitoExtension.class)
public class ProjektRestControllerUnitTest {

    @Mock
    private ProjektService mockProjektService;

    @InjectMocks
    private ProjektRestController controller;

    @Test
    void getProject_validId_returnsOk() {
        Projekt p = new Projekt();
        p.setProjektId(1);
        p.setNazwa("N1");
        p.setOpis("O1");
        when(mockProjektService.getProjekt(1)).thenReturn(Optional.of(p));

        ResponseEntity<Projekt> resp = controller.getProjekt(1);

        assertAll(
            () -> assertEquals(HttpStatus.OK, resp.getStatusCode()),
            () -> assertEquals(p, resp.getBody())
        );
    }

    @Test
    void getProject_invalidId_returnsNotFound() {
        when(mockProjektService.getProjekt(2)).thenReturn(Optional.empty());

        ResponseEntity<Projekt> resp = controller.getProjekt(2);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void getProjekty_returnsPage() {
        List<Projekt> list = List.of(
            projekty(1,"A"), projekty(2,"B"), projekty(3,"C")
        );
        PageRequest pr = PageRequest.of(0, 5);
        Page<Projekt> page = new PageImpl<>(list, pr, list.size());
        when(mockProjektService.getProjekty(pr)).thenReturn(page);

        Page<Projekt> result = controller.getProjekty(pr);

        assertNotNull(result);
        assertThat(result.getContent(), hasSize(3));
        assertTrue(result.getContent().containsAll(list));
    }

    @Test
    void createProjekt_valid_createsAndReturnsCreated() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(req));

        Projekt p = new Projekt();
        p.setNazwa("X");
        p.setOpis("Y");
        Projekt saved = new Projekt();
        saved.setProjektId(9);
        when(mockProjektService.setProjekt(any())).thenReturn(saved);

        ResponseEntity<Void> resp = controller.createProjekt(p);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertThat(resp.getHeaders().getLocation().getPath(), endsWith("/9"));
    }

    @Test
    void updateProjekt_valid_returnsOk() {
        Projekt existing = new Projekt(); existing.setProjektId(4);
        when(mockProjektService.getProjekt(4)).thenReturn(Optional.of(existing));

        Projekt upd = new Projekt();
        ResponseEntity<Object> resp = controller.updateProjekt(upd, 4);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void deleteProjekt_valid_returnsOk() {
        Projekt existing = new Projekt(); existing.setProjektId(5);
        when(mockProjektService.getProjekt(5)).thenReturn(Optional.of(existing));

        ResponseEntity<Object> resp = controller.deleteProjekt(5);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void deleteProjekt_invalid_returnsNotFound() {
        when(mockProjektService.getProjekt(6)).thenReturn(Optional.empty());

        ResponseEntity<Object> resp = controller.deleteProjekt(6);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    // helper
    private Projekt projekty(int id, String nazwa) {
        Projekt p = new Projekt();
        p.setProjektId(id);
        p.setNazwa(nazwa);
        return p;
    }
}

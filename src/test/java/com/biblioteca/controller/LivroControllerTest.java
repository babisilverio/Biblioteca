package com.biblioteca.controller;

import com.biblioteca.exception.GlobalExceptionHandler;
import com.biblioteca.exception.LivroNotFoundException;
import com.biblioteca.model.Livro;
import com.biblioteca.service.LivroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class LivroControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LivroService livroService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Livro livro;

    @BeforeEach
    void setUp() {
        LivroController livroController = new LivroController(livroService);
        mockMvc = MockMvcBuilders.standaloneSetup(livroController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        livro = new Livro(1L, "Clean Code", "Robert Martin", "9780132350884", 2008);
    }

    @Test
    void deveListarTodosOsLivros() throws Exception {
        when(livroService.findAll()).thenReturn(List.of(livro));

        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Clean Code"))
                .andExpect(jsonPath("$[0].autor").value("Robert Martin"));
    }

    @Test
    void deveBuscarLivroPorId() throws Exception {
        when(livroService.findById(1L)).thenReturn(livro);

        mockMvc.perform(get("/api/livros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Clean Code"));
    }

    @Test
    void deveLancarExcecaoAoBuscarLivroInexistente() throws Exception {
        when(livroService.findById(2L)).thenThrow(new LivroNotFoundException("Livro não encontrado"));

        mockMvc.perform(get("/api/livros/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveCriarNovoLivro() throws Exception {
        when(livroService.save(any(Livro.class))).thenReturn(livro);

        mockMvc.perform(post("/api/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Clean Code"));
    }

    @Test
    void deveRetornarBadRequestAoCriarLivroInvalido() throws Exception {
        livro.setTitulo("");

        mockMvc.perform(post("/api/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAtualizarLivro() throws Exception {
        when(livroService.update(eq(1L), any(Livro.class))).thenReturn(livro);

        mockMvc.perform(put("/api/livros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Clean Code"));
    }

    @Test
    void deveLancarExcecaoAoAtualizarLivroInexistente() throws Exception {
        when(livroService.update(eq(1L), any(Livro.class)))
                .thenThrow(new LivroNotFoundException("Livro não encontrado"));

        mockMvc.perform(put("/api/livros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarLivro() throws Exception {
        mockMvc.perform(delete("/api/livros/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveLancarExcecaoAoDeletarLivroInexistente() throws Exception {
        doThrow(new LivroNotFoundException("Livro não encontrado"))
                .when(livroService).deleteById(1L);

        mockMvc.perform(delete("/api/livros/1"))
                .andExpect(status().isNotFound());
    }
}

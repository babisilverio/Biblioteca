package com.biblioteca.service;

import com.biblioteca.exception.LivroNotFoundException;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private LivroService livroService;

    private Livro livro;

    @BeforeEach
    void setUp() {
        livro = new Livro(1L, "Clean Code", "Robert Martin", "9780132350884", 2008);
    }

    @Test
    void deveListarTodosOsLivros() {
        when(livroRepository.findAll()).thenReturn(Arrays.asList(livro));

        var livros = livroService.findAll();

        assertFalse(livros.isEmpty());
        assertEquals(1, livros.size());
        verify(livroRepository).findAll();
    }

    @Test
    void deveBuscarLivroPorId() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));

        var livroEncontrado = livroService.findById(1L);

        assertNotNull(livroEncontrado);
        assertEquals("Clean Code", livroEncontrado.getTitulo());
    }

    @Test
    void deveLancarExcecaoQuandoLivroNaoEncontrado() {
        when(livroRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LivroNotFoundException.class, () -> livroService.findById(1L));
    }

    @Test
    void deveSalvarLivro() {
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        var livroSalvo = livroService.save(livro);

        assertNotNull(livroSalvo);
        assertEquals(livro.getTitulo(), livroSalvo.getTitulo());
    }

    @Test
    void deveLancarExcecaoQuandoAnoLancamentoForFuturo() {
        livro.setAnoLancamento(2026);

        assertThrows(IllegalArgumentException.class, () -> livroService.save(livro));
    }

    @Test
    void deveLancarExcecaoQuandoISBNInvalido() {
        livro.setIsbn("123"); // ISBN inválido

        assertThrows(IllegalArgumentException.class, () -> livroService.save(livro));
    }

    @Test
    void deveAtualizarLivro() {
        when(livroRepository.existsById(1L)).thenReturn(true);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        var livroAtualizado = livroService.update(1L, livro);

        assertNotNull(livroAtualizado);
        assertEquals(livro.getTitulo(), livroAtualizado.getTitulo());
    }

    @Test
    void deveLancarExcecaoAoAtualizarLivroInexistente() {
        when(livroRepository.existsById(1L)).thenReturn(false);

        assertThrows(LivroNotFoundException.class, () -> livroService.update(1L, livro));
    }

    @Test
    void deveDeletarLivro() {
        when(livroRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> livroService.deleteById(1L));
        verify(livroRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarLivroInexistente() {
        when(livroRepository.existsById(1L)).thenReturn(false);

        assertThrows(LivroNotFoundException.class, () -> livroService.deleteById(1L));
    }
}

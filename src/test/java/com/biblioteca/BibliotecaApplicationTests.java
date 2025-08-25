package com.biblioteca;

import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BibliotecaApplicationTests {

    @Autowired
    private LivroRepository livroRepository;

    @Test
    void contextLoads() {
        Livro livro = new Livro();
        livro.setTitulo("O Hobbit");
        livro.setAutor("J.R.R. Tolkien");
        livro.setIsbn("9788595084742");
        livro.setAnoLancamento(1937);

        // Salva o livro
        Livro livroSalvo = livroRepository.save(livro);
        assertThat(livroSalvo.getId()).isNotNull();
        assertThat(livroSalvo.getTitulo()).isEqualTo("O Hobbit");

        // Busca o livro
        Optional<Livro> livroEncontrado = livroRepository.findById(livroSalvo.getId());
        assertThat(livroEncontrado).isPresent();
        assertThat(livroEncontrado.get().getAutor()).isEqualTo("J.R.R. Tolkien");

        // Lista todos os livros
        assertThat(livroRepository.findAll()).hasSize(1);

        // Deleta o livro
        livroRepository.deleteById(livroSalvo.getId());
        assertThat(livroRepository.findAll()).isEmpty();
    }
}

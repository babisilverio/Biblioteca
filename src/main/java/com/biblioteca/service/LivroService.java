package com.biblioteca.service;

import com.biblioteca.exception.LivroNotFoundException;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LivroService {

    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    public List<Livro> findAll() {
        return livroRepository.findAll();
    }

    public Livro findById(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new LivroNotFoundException("Livro não encontrado com ID: " + id));
    }

    public Livro save(Livro livro) {
        validarLivro(livro);
        return livroRepository.save(livro);
    }

    public void deleteById(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new LivroNotFoundException("Livro não encontrado com ID: " + id);
        }
        livroRepository.deleteById(id);
    }

    public Livro update(Long id, Livro livro) {
        if (!livroRepository.existsById(id)) {
            throw new LivroNotFoundException("Livro não encontrado com ID: " + id);
        }
        validarLivro(livro);
        livro.setId(id);
        return livroRepository.save(livro);
    }

    private void validarLivro(Livro livro) {
        if (livro.getAnoLancamento() > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("O ano de lançamento não pode ser no futuro");
        }
        if (!isValidISBN(livro.getIsbn())) {
            throw new IllegalArgumentException("ISBN inválido");
        }
    }

    private boolean isValidISBN(String isbn) {
        return isbn != null && (isbn.length() == 10 || isbn.length() == 13) && isbn.matches("\\d+");
    }
}

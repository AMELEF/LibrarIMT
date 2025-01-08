package com.imt.framework.web.tuto;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.imt.framework.web.tuto.entities.Livre;
import com.imt.framework.web.tuto.repositories.LivreRepository;
import com.imt.framework.web.tuto.resources.LivreResource;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class LivreResourceTest {

    @InjectMocks
    private LivreResource livreResource;

    @Mock
    private LivreRepository livreRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetBooks_WithMaxPrice() {
        // Arrange
        double maxPrice = 50.0;
        Livre livre = new Livre(1, "The Legend of Zelda : Souvenirs d'enfance", "Mathieu MERIOT", 40.0);
        when(livreRepository.getBooksWithMaxPrice(maxPrice)).thenReturn(Collections.singletonList(livre));

        // Act
        Response response = livreResource.getBooks(maxPrice);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(livreRepository, times(1)).getBooksWithMaxPrice(maxPrice);
    }

    @Test
    public void testGetBooks_WithoutMaxPrice() {
        // Arrange
        Livre livre = new Livre(1, "Titre", "Auteur", 40.0);
        when(livreRepository.findAll()).thenReturn(Collections.singletonList(livre));

        // Act
        Response response = livreResource.getBooks(null);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(livreRepository, times(1)).findAll();
    }

    @Test
    public void testCreateBook() {
        // Arrange
        Livre livre = new Livre(1, "Titre", "Auteur", 40.0);

        // Act
        livreResource.createBook(livre);

        // Assert
        verify(livreRepository, times(1)).save(livre);
    }

    @Test
    public void testUpdateBook_Success() throws Exception {
        // Arrange
        int id = 1;
        Livre livre = new Livre(id, "Titre", "Auteur", 50.0);
        Livre existingLivre = new Livre(id, "Old Titre", "Old Auteur", 30.0);
        when(livreRepository.findById(id)).thenReturn(Optional.of(existingLivre));

        // Act
        livreResource.updateBook(id, livre);

        // Assert
        verify(livreRepository, times(1)).findById(id);
        verify(livreRepository, times(1)).save(existingLivre);
        assertEquals("Titre", existingLivre.getTitre());
        assertEquals("Auteur", existingLivre.getAuteur());
        assertEquals(50.0, existingLivre.getPrice());
    }

    @Test
    public void testUpdateBook_NotFound() {
        // Arrange
        int id = 1;
        Livre livre = new Livre(id, "Titre", "Auteur", 50.0);
        when(livreRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> livreResource.updateBook(id, livre));
        assertEquals("Livre inconnu", exception.getMessage());
        verify(livreRepository, times(1)).findById(id);
        verify(livreRepository, times(0)).save(any());
    }

    @Test
    public void testDeleteBook() {
        // Arrange
        int id = 1;

        // Act
        livreResource.deleteBook(id);

        // Assert
        verify(livreRepository, times(1)).deleteById(id);
    }
}


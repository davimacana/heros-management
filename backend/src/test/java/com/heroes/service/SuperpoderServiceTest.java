package com.heroes.service;

import com.heroes.model.dto.SuperpoderDTO;
import com.heroes.model.entity.Superpoder;
import com.heroes.model.mapper.HeroMapper;
import com.heroes.repository.SuperpoderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuperpoderServiceTest {

    @Mock
    private SuperpoderRepository superpoderRepository;

    @Mock
    private HeroMapper heroMapper;

    @InjectMocks
    private SuperpoderService superpoderService;

    private Superpoder superpoder1;
    private Superpoder superpoder2;
    private Superpoder superpoder3;
    private SuperpoderDTO superpoderDTO1;
    private SuperpoderDTO superpoderDTO2;
    private SuperpoderDTO superpoderDTO3;

    @BeforeEach
    void setUp() {
        superpoder1 = new Superpoder("Super Força", "Capacidade de levantar objetos extremamente pesados");
        superpoder1.setId(1L);

        superpoder2 = new Superpoder("Voo", "Capacidade de voar pelos céus");
        superpoder2.setId(2L);

        superpoder3 = new Superpoder("Visão de Calor", "Capacidade de emitir raios de calor pelos olhos");
        superpoder3.setId(3L);

        superpoderDTO1 = new SuperpoderDTO(1L, "Super Força", "Capacidade de levantar objetos extremamente pesados");
        superpoderDTO2 = new SuperpoderDTO(2L, "Voo", "Capacidade de voar pelos céus");
        superpoderDTO3 = new SuperpoderDTO(3L, "Visão de Calor", "Capacidade de emitir raios de calor pelos olhos");
    }

    @Test
    void testFindAll() {
        List<Superpoder> superpoderes = Arrays.asList(superpoder1, superpoder2, superpoder3);
        when(superpoderRepository.findAll()).thenReturn(superpoderes);
        when(heroMapper.toSuperpoderDTO(superpoder1)).thenReturn(superpoderDTO1);
        when(heroMapper.toSuperpoderDTO(superpoder2)).thenReturn(superpoderDTO2);
        when(heroMapper.toSuperpoderDTO(superpoder3)).thenReturn(superpoderDTO3);

        List<SuperpoderDTO> result = superpoderService.findAll();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(superpoderDTO1, result.get(0));
        assertEquals(superpoderDTO2, result.get(1));
        assertEquals(superpoderDTO3, result.get(2));

        verify(superpoderRepository).findAll();
        verify(heroMapper).toSuperpoderDTO(superpoder1);
        verify(heroMapper).toSuperpoderDTO(superpoder2);
        verify(heroMapper).toSuperpoderDTO(superpoder3);
    }

    @Test
    void testFindAllEmpty() {
        when(superpoderRepository.findAll()).thenReturn(Collections.emptyList());

        List<SuperpoderDTO> result = superpoderService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(superpoderRepository).findAll();
        verify(heroMapper, never()).toSuperpoderDTO(any(Superpoder.class));
    }

    @Test
    void testFindAllWithSingleSuperpoder() {
        List<Superpoder> superpoderes = Arrays.asList(superpoder1);
        when(superpoderRepository.findAll()).thenReturn(superpoderes);
        when(heroMapper.toSuperpoderDTO(superpoder1)).thenReturn(superpoderDTO1);

        List<SuperpoderDTO> result = superpoderService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(superpoderDTO1, result.get(0));

        verify(superpoderRepository).findAll();
        verify(heroMapper).toSuperpoderDTO(superpoder1);
    }

    @Test
    void testFindAllWithNullSuperpoderes() {
        when(superpoderRepository.findAll()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> superpoderService.findAll());
        verify(superpoderRepository).findAll();
    }

    @Test
    void testFindAllWithMapperReturningNull() {
        List<Superpoder> superpoderes = Arrays.asList(superpoder1, superpoder2);
        when(superpoderRepository.findAll()).thenReturn(superpoderes);
        when(heroMapper.toSuperpoderDTO(superpoder1)).thenReturn(superpoderDTO1);
        when(heroMapper.toSuperpoderDTO(superpoder2)).thenReturn(null);

        List<SuperpoderDTO> result = superpoderService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(superpoderDTO1, result.get(0));
        assertNull(result.get(1));

        verify(superpoderRepository).findAll();
        verify(heroMapper).toSuperpoderDTO(superpoder1);
        verify(heroMapper).toSuperpoderDTO(superpoder2);
    }

    @Test
    void testFindAllWithLargeDataset() {
        List<Superpoder> superpoderes = new ArrayList<>();
        List<SuperpoderDTO> expectedDTOs = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            Superpoder superpoder = new Superpoder("Superpoder " + i, "Descrição " + i);
            superpoder.setId((long) i);
            superpoderes.add(superpoder);

            SuperpoderDTO dto = new SuperpoderDTO((long) i, "Superpoder " + i, "Descrição " + i);
            expectedDTOs.add(dto);

            when(heroMapper.toSuperpoderDTO(superpoder)).thenReturn(dto);
        }

        when(superpoderRepository.findAll()).thenReturn(superpoderes);

        List<SuperpoderDTO> result = superpoderService.findAll();

        assertNotNull(result);
        assertEquals(10, result.size());
        assertEquals(expectedDTOs, result);

        verify(superpoderRepository).findAll();
        verify(heroMapper, times(10)).toSuperpoderDTO(any(Superpoder.class));
    }
}

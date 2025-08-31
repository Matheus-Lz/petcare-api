package com.petcare.petcare_api.coredomain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PetServiceModelTest {

    private PetService.PetServiceBuilder createValidServiceBuilder() {
        return PetService.builder()
                .name("Banho e Tosa")
                .description("Serviço completo de banho e tosa.")
                .price(50.0)
                .time(60);
    }

    @Test
    void shouldThrowWhenNameIsEmpty() {
        PetService service = createValidServiceBuilder().name("").build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, service::validate);
        assertEquals("O nome do serviço não pode ser vazio", ex.getMessage());
    }

    @Test
    void shouldThrowWhenDescriptionIsEmpty() {
        PetService service = createValidServiceBuilder().description("").build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, service::validate);
        assertEquals("A descrição do serviço não pode ser vazia", ex.getMessage());
    }

    @Test
    void shouldThrowWhenPriceIsZeroOrLess() {
        PetService serviceWithZeroPrice = createValidServiceBuilder().price(0.0).build();
        PetService serviceWithNegativePrice = createValidServiceBuilder().price(-10.0).build();

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, serviceWithZeroPrice::validate);
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, serviceWithNegativePrice::validate);

        assertEquals("O preço do serviço deve ser maior que zero", ex1.getMessage());
        assertEquals("O preço do serviço deve ser maior que zero", ex2.getMessage());
    }

    @Test
    void shouldThrowWhenTimeIsZeroOrLess() {
        PetService serviceWithZeroTime = createValidServiceBuilder().time(0).build();
        PetService serviceWithNegativeTime = createValidServiceBuilder().time(-30).build();

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, serviceWithZeroTime::validate);
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, serviceWithNegativeTime::validate);

        assertEquals("O tempo do serviço deve ser maior que zero", ex1.getMessage());
        assertEquals("O tempo do serviço deve ser maior que zero", ex2.getMessage());
    }

    @Test
    void shouldValidateSuccessfully() {
        PetService validService = createValidServiceBuilder().build();

        assertDoesNotThrow(validService::validate);
    }
}
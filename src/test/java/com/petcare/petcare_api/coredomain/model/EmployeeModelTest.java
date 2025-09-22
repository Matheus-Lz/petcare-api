package com.petcare.petcare_api.coredomain.model;

import com.petcare.petcare_api.coredomain.model.user.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeModelTest {

    @Test
    void shouldBuildEmployeeAndAssignServices() {
        User u = User.builder().email("e@a.com").password("123456").name("E").cpfCnpj("23602088000117").build();
        PetService s1 = PetService.builder().name("Banho").description("D1").price(10.0).time(30).build();
        PetService s2 = PetService.builder().name("Tosa").description("D2").price(20.0).time(45).build();
        Employee e = Employee.builder().user(u).petServiceList(List.of(s1, s2)).build();
        assertEquals(u, e.getUser());
        assertEquals(2, e.getPetServiceList().size());
    }

    @Test
    void shouldAllowEmptyServiceList() {
        User u = User.builder().email("e@a.com").password("123456").name("E").cpfCnpj("23602088000117").build();
        Employee e = Employee.builder().user(u).petServiceList(List.of()).build();
        assertTrue(e.getPetServiceList().isEmpty());
    }
}

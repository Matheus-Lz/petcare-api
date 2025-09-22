package com.petcare.petcare_api.coredomain.model;

import com.petcare.petcare_api.coredomain.model.scheduling.Scheduling;
import com.petcare.petcare_api.coredomain.model.scheduling.enums.SchedulingStatus;
import com.petcare.petcare_api.coredomain.model.user.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SchedulingModelTest {

    @Test
    void shouldBuildSchedulingWithAllAssociations() {
        User user = User.builder().email("u@a.com").password("123456").name("U").cpfCnpj("23602088000117").build();
        PetService service = PetService.builder().name("Banho").description("D").price(10.0).time(30).build();
        Employee emp = Employee.builder().user(user).petServiceList(List.of(service)).build();
        user.setId("u1");
        service.setId("s1");
        emp.setId("e1");
        Scheduling s = Scheduling.builder()
                .user(user)
                .employee(emp)
                .petService(service)
                .status(SchedulingStatus.WAITING_FOR_ARRIVAL)
                .schedulingHour(LocalDateTime.now())
                .build();
        assertEquals("u1", s.getUser().getId());
        assertEquals("e1", s.getEmployee().getId());
        assertEquals("s1", s.getPetService().getId());
        assertEquals(SchedulingStatus.WAITING_FOR_ARRIVAL, s.getStatus());
        assertNotNull(s.getSchedulingHour());
    }

    @Test
    void shouldAllowNullEmployee() {
        User user = User.builder().email("u@a.com").password("123456").name("U").cpfCnpj("23602088000117").build();
        PetService service = PetService.builder().name("Banho").description("D").price(10.0).time(30).build();
        Scheduling s = Scheduling.builder()
                .user(user)
                .petService(service)
                .status(SchedulingStatus.PENDING)
                .schedulingHour(LocalDateTime.now())
                .build();
        assertNull(s.getEmployee());
        assertEquals(SchedulingStatus.PENDING, s.getStatus());
    }
}

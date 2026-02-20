package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.service.TraineeService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class TraineeFacadeTest {

    @Test
    void createTraineeProfileShouldDelegateToService() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        LocalDate dob = LocalDate.of(2000, 1, 1);
        GeneratedCredentials expected =
                new GeneratedCredentials(UUID.randomUUID(), "A.B", "secret");
        when(service.createTraineeProfile("A", "B", dob, "Addr")).thenReturn(expected);

        GeneratedCredentials actual = facade.createTraineeProfile("A", "B", dob, "Addr");

        assertSame(expected, actual);
        verify(service).createTraineeProfile("A", "B", dob, "Addr");
        verifyNoMoreInteractions(service);
    }
    @Test
    void selectTraineeProfileShouldDelegateToService() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        UUID id = UUID.randomUUID();
        facade.selectTraineeProfile(id);

        verify(service).selectTraineeProfile(id);
        verifyNoMoreInteractions(service);
    }

    @Test
    void updateTraineeAddressShouldDelegateToService() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        UUID id = UUID.randomUUID();
        facade.updateTraineeAddress(id, "New");

        verify(service).updateTraineeAddress(id, "New");
        verifyNoMoreInteractions(service);
    }

    @Test
    void deleteTraineeProfileShouldDelegateToService() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        UUID id = UUID.randomUUID();
        facade.deleteTraineeProfile(id);

        verify(service).deleteTraineeProfile(id);
        verifyNoMoreInteractions(service);
    }

}

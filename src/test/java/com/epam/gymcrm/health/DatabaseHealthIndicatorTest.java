package com.epam.gymcrm.health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DatabaseHealthIndicatorTest {

    @Test
    void healthShouldReturnUpWhenDatabaseIsReachable() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(1)).thenReturn(true);

        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(dataSource);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("reachable", health.getDetails().get("database"));

        verify(connection).close();
    }

    @Test
    void healthShouldReturnDownWhenDatabaseIsNotReachable() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(1)).thenReturn(false);

        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(dataSource);

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("not reachable", health.getDetails().get("database"));

        verify(connection).close();
    }

    @Test
    void healthShouldReturnDownWhenExceptionOccurs() throws Exception {
        DataSource dataSource = mock(DataSource.class);

        when(dataSource.getConnection()).thenThrow(new RuntimeException("DB error"));

        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(dataSource);

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}
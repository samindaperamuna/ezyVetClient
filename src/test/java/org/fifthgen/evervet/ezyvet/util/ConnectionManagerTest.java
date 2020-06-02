package org.fifthgen.evervet.ezyvet.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConnectionManagerTest {

    private ConnectionManager connectionManager;

    @Before
    public void setUp() {
        this.connectionManager = ConnectionManager.getInstance();
    }

    @Test
    public void checkConnectionTest() {
        Assert.assertTrue(connectionManager.checkConnection());
    }
}

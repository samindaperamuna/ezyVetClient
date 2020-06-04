package org.fifthgen.evervet.ezyvet.util;

import org.fifthgen.evervet.ezyvet.TestContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConnectionManagerTest {

    private ConnectionManager connectionManager;

    @Before
    public void setUp() {
        this.connectionManager = ConnectionManager.getInstance();

        TestContext testContext = new TestContext();
        testContext.init();
    }

    @Test
    public void checkConnectionTest() {
        Assert.assertTrue(connectionManager.checkConnection());
    }
}

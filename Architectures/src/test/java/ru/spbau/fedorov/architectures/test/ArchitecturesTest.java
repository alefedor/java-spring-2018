package ru.spbau.fedorov.architectures.test;

import org.junit.*;
import org.junit.runners.MethodSorters;
import ru.spbau.fedorov.architectures.client.ClientRunner;
import ru.spbau.fedorov.architectures.protocol.ArchitectureTypes;
import ru.spbau.fedorov.architectures.protocol.ChangingParameter;
import ru.spbau.fedorov.architectures.server.ServerStarter;

import java.io.IOException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ArchitecturesTest {
    private static Thread server;

    @BeforeClass
    public static void setUp() throws InterruptedException {
        server = new Thread(() -> {
            try {
                ServerStarter.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
         });
        server.start();
        Thread.sleep(100);
    }

    @AfterClass
    public static void tearDown() {
        while (server.isAlive()) {
            server.interrupt();
        }
    }

    @Test
    public void testSeparateThread() throws IOException, InterruptedException {
        ClientRunner.start("localhost", 1000, 10, 50, 20,
                ChangingParameter.ELEMENT_NUMBER, 1000, 2, ArchitectureTypes.SEPARATE_THREAD);
    }

    @Test
    public void testThreadPool() throws IOException, InterruptedException {
        ClientRunner.start("localhost", 1000, 10, 50, 20,
                ChangingParameter.ELEMENT_NUMBER, 1000, 2, ArchitectureTypes.THREAD_POOL);
    }

    @Test
    public void testNonBlocking() throws IOException, InterruptedException {
        ClientRunner.start("localhost", 1000, 10, 50, 20,
                ChangingParameter.ELEMENT_NUMBER, 1000, 2, ArchitectureTypes.NON_BLOCKING);
    }
}

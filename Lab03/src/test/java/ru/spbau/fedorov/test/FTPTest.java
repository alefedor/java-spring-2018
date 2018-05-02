package ru.spbau.fedorov.test;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.spbau.fedorov.algo.client.FTPClient;
import ru.spbau.fedorov.algo.data.FileEntry;
import ru.spbau.fedorov.algo.server.FTPServer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FTPTest {
    private Thread server;
    private static final String TEST_DIR = System.getProperty("user.dir")
            + File.separator + "src"
            + File.separator + "test"
            + File.separator + "resources"
            + File.separator;

    private static final String host = "localhost";
    private static final int LARGE_SIZE = 5279;

    @Before
    public void setUp() throws IOException, InterruptedException {
        server = new Thread(() -> {
            try {
                FTPServer.main(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.start();

        while (!FTPServer.isRunning()) {
            Thread.sleep(100);
        }
    }

    @After
    public void tearDown() {
        while (FTPServer.isRunning()) {
            server.interrupt();
        }
    }

    @Test
    public void testList() throws IOException {
        String path = TEST_DIR + "A";
        List<FileEntry> shouldBe = new ArrayList<>();
        shouldBe.add(new FileEntry("B", true));
        shouldBe.add(new FileEntry("C", true));
        shouldBe.add(new FileEntry("A", false));
        shouldBe.add(new FileEntry("large", false));

        for (int i = 0; i < 5; i++) {
            FTPClient client = new FTPClient(host);
            List<FileEntry> files = client.list(path);

            assertTrue(files != null);
            assertEquals(files.size(), shouldBe.size());

            for (FileEntry file : shouldBe) {
                assertTrue(files.contains(file));
            }
        }
    }

    @Test
    public void testGet() throws IOException {
        String path = TEST_DIR + "A" + File.separator + "A";
        System.out.println(path);
        for (int i = 0; i < 5; i++) {
            FTPClient client = new FTPClient(host);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            boolean was = client.get(path, output);

            assertTrue(was);

            assertArrayEquals("A file".getBytes(), output.toByteArray());

        }
    }

    @Test
    public void testGetNoFile() throws IOException {
        String path = TEST_DIR + "A" + File.separator + "ABACABA";
        System.out.println(path);
        FTPClient client = new FTPClient(host);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        boolean was = client.get(path, output);

        assertFalse(was);
        assertTrue(output.toByteArray().length == 0);
    }

    @Test
    public void testGetDirectory() throws IOException {
        String path = TEST_DIR + "A" + File.separator + "B";
        System.out.println(path);
        FTPClient client = new FTPClient(host);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        boolean was = client.get(path, output);

        assertFalse(was);
        assertTrue(output.toByteArray().length == 0);
    }

    @Test
    public void testListNoDirectory() throws IOException {
        String path = TEST_DIR + "A" + File.separator + "BB";
        System.out.println(path);
        FTPClient client = new FTPClient(host);
        List<FileEntry> files = client.list(path);

        assertTrue(files == null);
    }

    @Test
    public void testListEmptyDirectory() throws IOException {
        String path = TEST_DIR + "A" + File.separator + "C" + File.separator + "empty";
        System.out.println(path);
        FTPClient client = new FTPClient(host);
        List<FileEntry> files = client.list(path);

        assertTrue(files == null);
    }

    @Test
    public void testGetLargeFile() throws IOException {
        String path = TEST_DIR + "A" + File.separator + "large";
        FTPClient client = new FTPClient(host);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        boolean was = client.get(path, output);

        assertTrue(was);

        byte content[] = new byte[LARGE_SIZE];
        new DataInputStream(new FileInputStream(path)).readFully(content);
        assertArrayEquals(content, output.toByteArray());

    }

    @Test
    public void testSequentialGetAndList() throws IOException {
        String filepath = TEST_DIR + "A" + File.separator + "C" + File.separator + "C";
        String dirpath = TEST_DIR + "A" + File.separator + "C" + File.separator;

        FTPClient client = new FTPClient(host);
        List<FileEntry> shouldBe = new ArrayList<>();
        shouldBe.add(new FileEntry("C", false));
        shouldBe.add(new FileEntry("empty", true));

        for (int i = 0; i < 2; i++) {
            List<FileEntry> files = client.list(dirpath);

            assertTrue(files != null);
            assertEquals(files.size(), shouldBe.size());

            for (FileEntry file : shouldBe) {
                assertTrue(files.contains(file));
            }
        }

        for (int i = 0; i < 2; i++) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            boolean was = client.get(filepath, output);

            assertTrue(was);

            assertArrayEquals("C file".getBytes(), output.toByteArray());
        }
    }
}

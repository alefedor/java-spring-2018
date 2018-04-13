package ru.spbau.fedorov.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import ru.spbau.fedorov.algo.ForkJoinMd5;
import ru.spbau.fedorov.algo.Md5Exception;
import ru.spbau.fedorov.algo.SingleThreadMd5;

import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class Md5Test {
    @Test
    public void testCalcSingleThread() throws Md5Exception, NoSuchAlgorithmException, IOException {
        Path path = Paths.get("src/test/resources/greatdir");
        new SingleThreadMd5().getMD5(path);
    }

    @Test
    public void testCalcForkJoin() throws Md5Exception, NoSuchAlgorithmException, IOException {
        Path path = Paths.get("src/test/resources/greatdir");
        new ForkJoinMd5().getMD5(path);
    }

    @Test
    public void testIsEqual() throws Md5Exception, NoSuchAlgorithmException, IOException {
        Path path = Paths.get("src/test/resources/greatdir");
        byte[] singleThreadHash = new SingleThreadMd5().getMD5(path);
        byte[] forkJoinHash = new ForkJoinMd5().getMD5(path);

        assertArrayEquals(singleThreadHash, forkJoinHash);
    }

    @Test
    public void testIsNotEqual() throws Md5Exception, NoSuchAlgorithmException, IOException {
        Path path1 = Paths.get("src/test/resources/greatdir");
        Path path2 = Paths.get("src/test/resources/greatdir/decentdir");
        byte[] firstHash = new ForkJoinMd5().getMD5(path1);
        byte[] secondHash = new ForkJoinMd5().getMD5(path2);
        if (!differ(firstHash, secondHash)) {
            fail();
        }
    }

    private boolean differ(byte[] a, byte[] b) {
        if (a.length != b.length)
            return true;
        for (int i = 0; i < a.length; i++)
            if (a[i] != b[i])
                return true;
        return false;
    }
}

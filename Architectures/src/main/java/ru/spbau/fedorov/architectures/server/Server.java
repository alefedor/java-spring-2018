package ru.spbau.fedorov.architectures.server;


import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.fedorov.architectures.protocol.Signals;
import ru.spbau.fedorov.architectures.util.Statistics;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Server {
    @Getter public static final int PORT = 4321;
    protected DataOutputStream out;
    protected AtomicLong querySum = new AtomicLong(0);
    protected AtomicLong clientSum = new AtomicLong(0);

    Server(@NotNull Socket s) throws IOException {
        this.out = new DataOutputStream(s.getOutputStream());
    }

    @Nullable
    public abstract Statistics start(int clients, int queries) throws IOException, InterruptedException ;

    protected void ready() throws IOException {
        out.writeInt(Signals.READY);
    }

    protected void fail() throws IOException {
        out.writeInt(Signals.FAIL);
    }

    //protected abstract void stop();

    protected void sort(@NotNull int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 1; j < arr.length; j++) {
                if (arr[j - 1] > arr[j]) {
                    arr[j - 1] ^= arr[j];
                    arr[j] ^= arr[j - 1];
                    arr[j - 1] ^= arr[j];
                }
            }
        }
    }

    protected void startQuery() {
        querySum.addAndGet(-System.currentTimeMillis());
    }

    protected void endQuery() {
        querySum.addAndGet(System.currentTimeMillis());
    }

    protected void startClient() {
        clientSum.addAndGet(-System.currentTimeMillis());
    }

    protected void endClient() {
        clientSum.addAndGet(System.currentTimeMillis());
    }

}

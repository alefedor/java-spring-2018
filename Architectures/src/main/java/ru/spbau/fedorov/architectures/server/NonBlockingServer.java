package ru.spbau.fedorov.architectures.server;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.architectures.protocol.ArrayMessage;
import ru.spbau.fedorov.architectures.util.Statistics;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NonBlockingServer extends Server {
    private final Selector readSelector = Selector.open();
    private final Selector writeSelector = Selector.open();

    private volatile ConcurrentLinkedQueue<NonBlockingClient> shouldRegisterRead = new ConcurrentLinkedQueue<>();
    private volatile ConcurrentLinkedQueue<NonBlockingClient> shouldRegisterWrite = new ConcurrentLinkedQueue<>();

    private ExecutorService executor = Executors.newFixedThreadPool(4);
    private CountDownLatch clientsHandled;
    private AtomicBoolean fail = new AtomicBoolean(false);

    public NonBlockingServer(@NotNull Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public Statistics start(int clients, int queries) throws IOException, InterruptedException {
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress(PORT));

        Thread readThread = new Thread(() -> {
           while (!Thread.interrupted()) {
               while (!shouldRegisterRead.isEmpty()) {
                   NonBlockingClient client = shouldRegisterRead.poll();
                   try {
                       SocketChannel channel = client.getSocketChannel();
                       channel.configureBlocking(false);
                       channel.register(readSelector, SelectionKey.OP_READ, client);
                   } catch (Exception e) {
                       e.printStackTrace();
                       fail.set(true);
                   }
               }


               try {
                   int num = readSelector.select();
                   if (num == 0) {
                       continue;
                   }

                   Iterator<SelectionKey> iterator = readSelector.selectedKeys().iterator();
                   while (iterator.hasNext()) {
                       SelectionKey key = iterator.next();
                       NonBlockingClient client = (NonBlockingClient) key.attachment();

                       client.read();
                       iterator.remove();
                   }
               } catch (ClosedByInterruptException e) {
                   // when interrupt while trying to read
               } catch (Exception e) {
                   e.printStackTrace();
                   fail.set(true);
               }
           }
        });

        Thread writeThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                while (!shouldRegisterWrite.isEmpty()) {
                    NonBlockingClient client = shouldRegisterWrite.poll();
                    try {
                        SocketChannel channel = client.getSocketChannel();
                        channel.configureBlocking(false);
                        channel.register(writeSelector, SelectionKey.OP_WRITE, client);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail.set(true);
                    }
                }

                try {
                    int num = writeSelector.select();
                    if (num == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> iterator = writeSelector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        NonBlockingClient client = (NonBlockingClient) key.attachment();

                        client.write(key);
                        iterator.remove();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    fail.set(true);
                }
            }
        });

        readThread.start();
        writeThread.start();

        ready();

        clientsHandled = new CountDownLatch(clients);
        List<NonBlockingClient> clientList = new ArrayList<>();

        for (int i = 0; i < clients; i++) {
            SocketChannel channel = socketChannel.accept();
            NonBlockingClient client = new NonBlockingClient(channel, queries);
            clientList.add(client);
            shouldRegisterRead.add(client);
            readSelector.wakeup();
        }

        clientsHandled.await();

        executor.shutdown();

        while (readThread.isAlive()) {
            readThread.interrupt();
        }

        while (writeThread.isAlive()) {
            writeThread.interrupt();
        }

        readSelector.close();
        writeSelector.close();

        for (NonBlockingClient client : clientList) {
            client.getSocketChannel().close();
        }

        socketChannel.close();

        if (fail.get()) {
            fail();
            return null;
        }

        return Statistics.of(querySum.get() / (double)queries / (double) clients, clientSum.get() / (double)clients);
    }

    private class NonBlockingClient {
        @Getter private final SocketChannel socketChannel;
        private final static int BUFFER_SIZE = 1000000;
        private final ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        private final ByteBuffer writeBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        private int state = 0;
        private AtomicInteger answered = new AtomicInteger(0);
        private final int queries;

        public NonBlockingClient(@NotNull SocketChannel channel, int queries) {
            socketChannel = channel;
            this.queries = queries;
            startClient();
        }

        public void read() throws IOException {
            int read = socketChannel.read(readBuffer);
            while (read > 0) {
                read = socketChannel.read(readBuffer);
            }

            readBuffer.flip();

            while (readBuffer.remaining() >= 4) {
                int size = readBuffer.getInt();
                if (readBuffer.remaining() >= size) {
                    startQuery();

                    int lastLimit = readBuffer.limit();
                    int newPosition = readBuffer.position() + size;
                    readBuffer.limit(readBuffer.position() + size);

                    List<Integer> list = ArrayMessage.Array.parseFrom(readBuffer).getDataList();
                    int arr[] = new int[list.size()];
                    for (int i = 0; i < arr.length; i++) {
                        arr[i] = list.get(i);
                    }

                    executor.submit(() -> {
                        try {
                            sort(arr);
                            addToWrite(arr);
                            endQuery();
                            answered.incrementAndGet();
                            shouldRegisterWrite.add(this);
                            writeSelector.wakeup();
                        } catch (Exception e) {
                            e.printStackTrace();
                            fail.set(true);
                        }
                    });

                    readBuffer.position(newPosition);
                    readBuffer.limit(lastLimit);
                } else {
                    readBuffer.position(readBuffer.position() - 4);
                    break;
                }
            }

            readBuffer.compact();
        }

        private void addToWrite(int[] arr) {
            ArrayMessage.Array.Builder builder = ArrayMessage.Array.newBuilder();
            for (int x : arr) {
                builder.addData(x);
            }
            byte[] bytes = builder.build().toByteArray();

            synchronized (writeBuffer) {
                writeBuffer.putInt(bytes.length);
                writeBuffer.put(bytes);
            }
        }

        public void write(@NotNull SelectionKey key) throws IOException {
            synchronized (writeBuffer) {
                writeBuffer.flip();

                int written = socketChannel.write(writeBuffer);
                while (written > 0) {
                    written = socketChannel.write(writeBuffer);
                }

                if (!writeBuffer.hasRemaining()) {
                    if (queries == answered.get()) {
                        clientsHandled.countDown();
                        endClient();
                    }
                    key.cancel();
                    writeBuffer.compact();
                    return;
                }

                writeBuffer.compact();
            }
        }
    }
}

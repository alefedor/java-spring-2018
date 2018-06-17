package ru.spbau.fedorov.architectures.util;

import org.apache.commons.io.input.BoundedInputStream;
import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.architectures.protocol.ArrayMessage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ArrayHandler {

    @NotNull
    public static int[] readArray(@NotNull InputStream in, int size) throws IOException {
        BoundedInputStream bin = new BoundedInputStream(in, size);
        List<Integer> data = ArrayMessage.Array.parseFrom(bin).getDataList();

        int[] result = new int[data.size()];
        for (int i = 0; i < data.size(); i++)
            result[i] = data.get(i);

        return result;
    }

    public static void writeArray(@NotNull DataOutputStream out, @NotNull int[] arr) throws IOException {
        ArrayMessage.Array.Builder builder = ArrayMessage.Array.newBuilder();
        for (int x : arr) {
            builder.addData(x);
        }
        byte[] bytes = builder.build().toByteArray();
        out.writeInt(bytes.length);
        out.write(bytes);
    }
}

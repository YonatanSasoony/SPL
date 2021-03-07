package bgu.spl.net.api;

import java.awt.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<String> {

    private byte[] buffer = new byte[1024];
    private int len = 0;

    @Override
    public byte[] encode(String message) {

        return (message + "\u0000").getBytes();
    }

    @Override
    public String decodeNextByte(byte nextByte) {
        if(nextByte == '\u0000')
            return pop();
        pushByte(nextByte);
        return null;
    }

    private String pop(){
        String result = new String (buffer, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    private void pushByte(byte nextByte){
        if(len >= buffer.length)
            buffer = Arrays.copyOf(buffer, buffer.length*2);
        buffer[len] = nextByte;
        len ++ ;
    }

}

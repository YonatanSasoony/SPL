package bgu.spl.net.srv;

import bgu.spl.net.api.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<MessageFrame> {

    private final StompMessagingProtocol protocol;
    private final MessageEncoderDecoder<String> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<String> reader, StompMessagingProtocol protocol ) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;

    }

    public StompMessagingProtocol getProtocol() {
        return protocol;
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                String nextMessage =  encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    MessageFrame frameToRead = new MessageFrame(nextMessage);
                    protocol.process(frameToRead);
                }

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    public void startProtocol(int connectionID,ConnectionsImpl connections){
        (protocol).start(connectionID,connections);

    }


    @Override
    public void send(MessageFrame msg) {
        try {
            out.write(encdec.encode(msg.toString()));
            out.flush();
        }catch(IOException e){ e.printStackTrace(); }
    }
}

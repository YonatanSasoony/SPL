package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessageFrame;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.StompMessagingProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {


    private final int port;
    private final Supplier<StompMessagingProtocol> protocolFactory;
    private final Supplier<MessageEncoderDecoder<String>> encdecFactory;
    private ServerSocket sock;
    private AtomicInteger connectionId;
    private Map<Integer,ConnectionHandler> handlerMap;
    private ConnectionsImpl connections;



    public BaseServer(
            int port,
            Supplier<StompMessagingProtocol> protocolFactory,
            Supplier<MessageEncoderDecoder<String>> encdecFactory) {

        this.port = port;
        this.protocolFactory =  protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
        this.handlerMap = new ConcurrentHashMap<>();
        connections = new ConnectionsImpl(this);
        connectionId = new AtomicInteger(1);

    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
			System.out.println("Server started");

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();
                int id = connectionId.getAndIncrement();

                BlockingConnectionHandler handler = new BlockingConnectionHandler(
                        clientSock,
                        encdecFactory.get(),
                        protocolFactory.get()
                );


                handlerMap.put(id,handler);
                handler.startProtocol(id,connections);
                execute(handler);
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler  handler);

    public Map<Integer, ConnectionHandler> getHandlerMap(){return handlerMap;}


}

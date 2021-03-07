package bgu.spl.net.srv;

import java.util.function.Supplier;

public class ThreadPerClientServer extends BaseServer {


    public ThreadPerClientServer(int port, Supplier protocolFactory, Supplier encdecFactory) {
        super(port, protocolFactory, encdecFactory);
    }

    @Override
    protected void execute(BlockingConnectionHandler handler) {
        new Thread(handler).start();
    }
}

package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.StompMessagingProtocolImpl;
import bgu.spl.net.srv.Reactor;
import bgu.spl.net.srv.ThreadPerClientServer;

public class StompServer {

    public static void main(String[] args) {


        String serverType = args[1];

        int port = Integer.decode(args[0]).intValue(); //todo


        if(serverType.equals("tpc" )){
               ThreadPerClientServer server = new ThreadPerClientServer (
                       port,
                       () -> new StompMessagingProtocolImpl(),
                       () -> new MessageEncoderDecoderImpl()  ) ;
               server.serve();
        }
        else if (serverType.equals("reactor")) {
            Reactor reactor = new Reactor(3,port,
                    () -> new StompMessagingProtocolImpl(),
                    () -> new MessageEncoderDecoderImpl());
            reactor.serve();

        }

        }

    }




package bgu.spl.net.api;

import java.util.LinkedList;
import java.util.List;

public class MessageFrame {

    private String command;
    private List<Pair<String,String>> headersList;
    private String frameBody;

    public MessageFrame(String toConvert){

        int commandIndex = toConvert.indexOf('\n');
        String command = toConvert.substring(0,commandIndex);
        String withoutCommand = toConvert.substring(commandIndex+1);
        String frameBody;
        int bodyIndex = withoutCommand.indexOf("\n\n");
        if (bodyIndex + 2 == withoutCommand.length() )
            frameBody ="";
        else
            frameBody = withoutCommand.substring(bodyIndex+2);

        String headers = withoutCommand.substring(0,bodyIndex);
        List<Pair<String,String>> headersList = new LinkedList<>();

        while (headers != null)
            headers = getHeader(headers,headersList);

        this.headersList = headersList;
        this.command = command;
        this.frameBody = frameBody;
    }
    String getHeader(String headers,List<Pair<String,String>> headersList){

        int index = headers.indexOf('\n');
        if (index == -1) {
            int pos = headers.indexOf(':');
            headersList.add(new Pair<>( headers.substring(0,pos),headers.substring(pos+1)  ));
            return null;
        }
        else {
            String temp = headers.substring(0, index);
            int pos = temp.indexOf(':');
            String left = temp.substring(0, pos);
            String right = temp.substring(pos + 1);
            headersList.add(new Pair<>(left,right ));
            return headers.substring(index + 1);
        }
    }


    public MessageFrame(String command, List<Pair<String, String>> headersList, String frameBody) {
        this.command = command;
        this.headersList = headersList;
        this.frameBody = frameBody;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<Pair<String, String>> getHeadersList() {
        return headersList;
    }

    public void setHeadersList(List<Pair<String, String>> headersList) {
        this.headersList = headersList;
    }

    public String getFrameBody() {
        return frameBody;
    }

    public void setFrameBody(String frameBody) {
        this.frameBody = frameBody;
    }

    public String getSendType(){
        if (frameBody.contains("added")) return "add";
        if (frameBody.contains("borrow")) return "borrow";
        if (frameBody.contains("Returning")) return "“return";
        if (frameBody.contains("status")) return "status";
        if (frameBody.contains("has")) return "has";
        if (frameBody.contains("Taking")) return "taking";

        return null;
    }

    public void setSubIdHeader (String subId){

        boolean flag = false;

        for(Pair<String,String> p : headersList)
            if(p.getFirst().equals("subscription")){
                p.setSecond(subId);
                flag = true;
            }
        if (flag == false)
            headersList.add(new Pair<>("subscription", subId));
    }


    public String toString (){

        String result = command + "\n";
        for (Pair p : headersList)
            result = result + p.getFirst() + ":" + p.getSecond() + "\n";

        result = result + "\n" + frameBody;

        return result;
    }
}

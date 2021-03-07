

#include "../include/ClientRunner.h"

/***************server handling **************/

string ClientRunner::getLastWord(string text){
    int index = -1;
    for (int i = text.length() - 1; i >= 0; i--)
        if (text[i] == ' ') {
            index = i;
            break;
        }
    if (index != -1)
        return text.substr(index + 1);
    return "";
}

string ClientRunner::getBookOwner(string body, string keyWord) {

    string bookOwner = "";
    if (keyWord == "has")
        bookOwner = getSubStr(' ', body);
    if (keyWord == "taking" || keyWord == "returning")
        bookOwner  = getLastWord(body);

    return bookOwner;
}

string ClientRunner::getRequestedBook(string body, string keyWord) {
    string requestedBook="";
    if (keyWord == "taking" || keyWord == "returning") {
        getSubStr(' ',body);
        cutLastWord(body);
        cutLastWord(body);
        requestedBook = body;
    }
    if (keyWord == "has"){
        getSubStr(' ',body);
        getSubStr(' ',body);
        requestedBook = body;
    }
    if(keyWord == "borrow"){
        getSubStr(' ',body);
        getSubStr(' ',body);
        getSubStr(' ',body);
        getSubStr(' ',body);
        requestedBook = body;
    }
    return requestedBook;
}

void ClientRunner:: cutLastWord(string &msg){
    int index = -1;
    for (int i=msg.length() - 1; i>=0; i--)
        if(msg.at(i) == ' '){
            index = i;
            break;
        }
    msg = msg.substr(0,index);
}

string ClientRunner::getTopic(string msg) {

    int startIndex = msg.find("destination:");
    if (startIndex != -1){
        string temp = msg.substr(startIndex+12);
        return getSubStr('\n',temp);
    }
    return "";
}


bool ClientRunner::isWish(string requestedBook) {

    for (auto book : wishList)
        if (book == requestedBook)
            return true;
    return false;
}


string ClientRunner::errorProcess(string msg) {
    return getErrorDescription(msg);
}

string ClientRunner::receiptProcess(string msg) {

    int index = msg.find("id");
    string _id = "";
    if (index != -1)
        _id = msg.substr(index + 3);

    short id;
    sscanf(_id.c_str(), "%hi", &id);
    return receiptMap[id];
}
string ClientRunner::getErrorDescription(string msg){
    int index = msg.find("message:") ;
    string temp = msg.substr(index+8);
    return getSubStr('\n', temp);

}
string ClientRunner::getFrameBody(string msg) {

    int index = msg.find("\n\n");
    if((index != -1) && (index == msg.length() - 2))
        return "";
    else
        return msg.substr(index + 2 );
}

string ClientRunner::findKeyWord(string frameBody) {

    if (signed (frameBody.find("has added")) != -1) return "has added";
    if (signed(frameBody.find("borrow")) != -1) return "borrow";
    if (signed(frameBody.find("Taking")) != -1) return "taking";
    if (signed(frameBody.find("has")) != -1) return "has";
    if (signed(frameBody.find("Returning") )!= -1) return "returning";
    if (signed(frameBody.find("status")) != -1) return "status";
    return "";
}

void ClientRunner::borrowAct(string frameBody, string topic) {
    string keyWord = findKeyWord(frameBody);
    string requestedBook = getRequestedBook(frameBody,keyWord);
    vector<string> bookList = myBooksMap[topic];
    for (auto book : bookList)
        if (book == requestedBook) {
            string body = username + " has " + requestedBook;
            string newMsg = createSendMsg(topic, body);
           send(newMsg);
        }
}

void ClientRunner::hasAct(string frameBody, string topic) {
    string keyWord = findKeyWord(frameBody);
    string requestedBook = getRequestedBook(frameBody,keyWord);
    string bookOwner = getBookOwner(frameBody,keyWord);
    if (isWish(requestedBook)) {
        myBooksMap[topic].push_back(requestedBook);
        removeItemFromVector(wishList,requestedBook);
        borrowedBooksMap.insert(pair<string,string>(requestedBook,bookOwner));
        string body = "Taking " + requestedBook + " from " + bookOwner;
        string newMsg = createSendMsg(topic, body);
        send(newMsg);
    }
}

void ClientRunner::takingAct(string frameBody, string topic) {

    string keyWord = findKeyWord(frameBody);
    string bookOwner = getBookOwner(frameBody,keyWord);
    string requestedBook = getRequestedBook(frameBody,keyWord);

    if (bookOwner == username)
        removeItemFromVector(myBooksMap[topic],requestedBook);
}

void ClientRunner::removeItemFromVector(vector<string>& list,string toDelete){

    int index = -1;
    for (int i = 0; i < list.size(); i++)
        if (list[i] == toDelete)
            index = i;
    if (index != -1)
        list.erase(list.begin() + index);

}

void ClientRunner::returningAct(string frameBody, string topic) {
    string keyWord = findKeyWord(frameBody);
    string bookOwner = getBookOwner(frameBody,keyWord);
    string requestedBook = getRequestedBook(frameBody,keyWord);

    if (bookOwner == username)
        myBooksMap[topic].push_back(requestedBook);
}

void ClientRunner::statusAct(string topic) {

    vector<string> bookList = myBooksMap[topic];
    string output = username + ": ";
    for (auto book : bookList)
        output = output + book + ",";
    output = output.substr(0, output.length() - 1);
    string sendMsg = createSendMsg(topic,output);
    send (sendMsg);
}

string ClientRunner::messageKeyWordProcess(string msg) {

    string topic = getTopic(msg);
    string frameBody = getFrameBody(msg);
    string keyWord = findKeyWord(frameBody);
    if (keyWord == "status") statusAct(topic);
    else if (keyWord == "borrow") borrowAct(frameBody,  topic);
    else if (keyWord == "taking") takingAct(frameBody, topic);
    else if (keyWord == "has") hasAct(frameBody,  topic);
    else if (keyWord == "returning") returningAct(frameBody, topic);

    return topic + ": " + frameBody;
}


void ClientRunner::run() {

    while (isConnected) {
        unique_lock<mutex> lck(mtxLogged);
        cv.wait(lck); // waits until logged on
        while(isLogged){
        string input;
        if (handler->getLine(input)) {
            string command = getSubStr('\n', input);
            string output = "";

            if (command == "CONNECTED") output = "Login successful";
            else if (command == "ERROR") output = errorProcess(input);
            else if (command == "RECEIPT") output = receiptProcess(input);
            else if (command == "MESSAGE") output = messageKeyWordProcess(input);

            cout<< output<<endl;

            if (output == "Disconnected from server" || command == "ERROR") {
                handler->close();
                delete handler;
                handler = nullptr;
                isLogged = false;
                clearData();

            }
        }
        else{ // if connection failed out of the blue
            handler->close();
            delete handler;
            handler = nullptr;
            isLogged = false;
            clearData();
            break;
        }
    }}
}
void ClientRunner::clearData(){
    username="";
    receiptId=0;
    subId =0;
    receiptMap.clear();
    myBooksMap.clear();
    borrowedBooksMap.clear();
    wishList.clear();
    subIdMap.clear();
}

ClientRunner::ClientRunner()
        : cv(), mtxLogged(), isConnected(true),mtxSend(), isLogged(false),handler(nullptr), username(), receiptId(0), subId(0), receiptMap(), myBooksMap(), borrowedBooksMap(), wishList(), subIdMap() {}

string ClientRunner::getAndIncrementReceiptId() {
    receiptId++;
    return to_string(receiptId);
}

string ClientRunner::getAndIncrementSubId() {
    subId++;
    return to_string(subId);
}

string ClientRunner::getSubStr(char ch, string &text) {
    int position = text.find(ch);
    string output = text.substr(0, position);
    text.erase(0, position + 1);
    return output;
}

/***************keyboard handling **************/

void ClientRunner::start() {

    std::thread t1 (&ClientRunner::run,this);
    string ip, port, pass;

    while (isConnected) {

        string input;
        getline(cin, input);
        int position = input.find(' ');
        string actionType = input.substr(0, position);
        string actionInfo = input.substr(position + 1);


        if (actionType == "login" && isLogged == false) {
            loginCommand(actionInfo, ip, port, pass);

            short _port;
            sscanf(port.c_str(), "%hi", &_port);
            handler = new ConnectionHandler(ip, _port);
            if (handler->connect()) {
                isLogged = true;
                unique_lock<mutex> lck(mtxLogged);
                cv.notify_all(); //notifies thread
                string msg = createConnectMsg(ip, pass);
                send(msg);
            }
        }

        if (isLogged) {
            if (actionType == "join")joinCommand(actionInfo);
            else if (actionType == "exit")exitCommand(actionInfo);
            else if (actionType == "add")addCommand(actionInfo);
            else if (actionType == "borrow")borrowCommand(actionInfo);
            else if (actionType == "return")returnCommand(actionInfo);
            else if (actionType == "status")statusCommand(actionInfo);
            else if (actionType == "logout") logoutCommand();
        }
        if (actionType == "bye") {
            isLogged = false;
            isConnected = false;
            cv.notify_all(); //notifies thread
        }
    }
    t1.join();

}


/***************Frames Creators **************/


void ClientRunner::loginCommand(string &text, string &ip, string &port, string &pass) {
    ip = getSubStr(':', text);
    port = getSubStr(' ', text);
    username = getSubStr(' ', text);
    pass = getSubStr(' ', text);

}

void ClientRunner::joinCommand(string actionInfo) {
    string subscribeMsg = createSubscribeMsg(actionInfo);
    subIdMap[actionInfo] = subId;
    receiptMap.insert(pair<int, string>(receiptId, "Joined club " + actionInfo));
    send(subscribeMsg);
}

void ClientRunner::exitCommand(string actionInfo) {
    string unsubscribeMsg = createUnsubscribeMsg(actionInfo);
    receiptMap.insert(pair<int, string>(receiptId, "Exited club " + actionInfo));
    send(unsubscribeMsg);
}

void ClientRunner::addCommand(string actionInfo) {

    string topic = getSubStr(' ', actionInfo);
    string bookName = actionInfo;
    string body = username + " has added the book " + bookName;
    myBooksMap[topic].push_back(bookName);
    string sendMsg = createSendMsg(topic, body);
    send(sendMsg);
}

void ClientRunner::borrowCommand(string actionInfo) {
    string topic = getSubStr(' ', actionInfo);
    string bookName = actionInfo;
    string body = username + " wish to borrow " + bookName;
    wishList.push_back(bookName);
    string sendMsg = createSendMsg(topic, body);
    send(sendMsg);
}

void ClientRunner::returnCommand(string actionInfo) {
    string topic = getSubStr(' ', actionInfo);
    string bookName = actionInfo;
    string prevOwner = borrowedBooksMap[bookName];
    if( prevOwner.length() != 0 ) {
        string body = "Returning " + bookName + " to " + prevOwner;
        removeItemFromVector(myBooksMap[topic], bookName);
        string sendMsg = createSendMsg(topic, body);
        send(sendMsg);
    }
}

void ClientRunner::statusCommand(string actionInfo) {
    string topic = actionInfo;
    string body = "book status";
    string sendMsg = createSendMsg(topic, body);
    send(sendMsg);
}

void ClientRunner::logoutCommand() {

    for (auto pair : subIdMap) {
        string unsubscribeMsg = createUnsubscribeMsg(pair.first);
        send(unsubscribeMsg);
    }
    string disconnectMsg = createDisconnectMsg();
    receiptMap[receiptId] = "Disconnected from server";

    send(disconnectMsg);
}


/************Messages Creators ***********/


string ClientRunner::createConnectMsg(string ip, string pass) {
    return "CONNECT\naccept-version:1.2\nhost:" + ip + "\nlogin:" + username + "\npasscode:" + pass +
           "\nreceipt:" + getAndIncrementReceiptId() + "\n\n\0";
}

string ClientRunner::createSubscribeMsg(string topic) {
    return "SUBSCRIBE\ndestination:" + topic + "\nid:" + getAndIncrementSubId() +
           "\nreceipt:" + getAndIncrementReceiptId() + "\n\n\0";
}

string ClientRunner::createUnsubscribeMsg(string topic) {
    return "UNSUBSCRIBE\nid:" + to_string(subIdMap[topic]) + "\nreceipt:" +
           getAndIncrementReceiptId() + "\n\n\0";

}

string ClientRunner::createSendMsg(string topic, string body) {
    return "SEND\ndestination:" + topic + "\n\n" + body + "\0";
}

string ClientRunner::createDisconnectMsg() {
    return "DISCONNECT\nreceipt:" + getAndIncrementReceiptId() + "\n\n\0";
}

void ClientRunner:: send (string msg){
    lock_guard<mutex> lock(mtxSend);// constructor locks the mutex while destructor (out of scope) unlocks it
    handler->sendLine(msg);
}
















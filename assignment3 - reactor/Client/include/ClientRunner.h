#ifndef BOOST_ECHO_CLIENTRUNNER_CLIENTRUNNER_H

#define BOOST_ECHO_CLIENTRUNNER_CLIENTRUNNER_H


#include "ConnectionHandler.h"
#include <boost/asio/ip/tcp.hpp>
#include <string>
#include <thread>
#include <mutex>
#include <condition_variable>
using namespace std;

class ClientRunner {

public:
    void start();
    ClientRunner();

private:
    condition_variable cv;
    mutex mtxLogged;
    bool isConnected;
    mutex mtxSend;
    bool isLogged;
    ConnectionHandler* handler;
    std::string username;
    int receiptId;
    int subId;
    map<int, string> receiptMap;
    map<string, vector<string>> myBooksMap;
    map<string,string> borrowedBooksMap; //book-username
    vector<string> wishList;
    map<string, int> subIdMap;

    string getSubStr (char, string &);

    string getAndIncrementReceiptId();
    string getAndIncrementSubId();
    void loginCommand(string &,string &,string &,string &);



    string createConnectMsg(string ip, string pass);

    string createSubscribeMsg(string);

    string createUnsubscribeMsg(string);

    string createSendMsg(string , string);

    string createDisconnectMsg();

    void joinCommand(string);

    void exitCommand(string );

    void addCommand(string);

    void borrowCommand(string );

    void returnCommand(string);

    void statusCommand(string );

    void logoutCommand();



    string errorProcess(string );

    string receiptProcess(string );

    string messageKeyWordProcess(string );

    string getFrameBody(string );

    string findKeyWord(string );

    void takingAct(string , string);

    void borrowAct(string , string);

    void hasAct(string, string);

    void returningAct(string , string);

    void statusAct(string);

    string getTopic(string );

    string getBookOwner(string ,string);

    string getRequestedBook(string ,string);

    bool isWish(string );

    string getLastWord(string );

     void run();

    void send(string );

    string getErrorDescription(string );

    void cutLastWord(string &);

    void clearData();

    void removeItemFromVector(vector<string> &list, string toDelete);
};

#endif
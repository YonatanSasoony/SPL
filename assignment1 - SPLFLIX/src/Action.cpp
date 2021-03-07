#include "../include/Session.h"
#include "../include/Action.h"
#include "../include/User.h"
#include "../include/Watchable.h"
#include <fstream>
#include <json.hpp>

#include <unordered_map>
#include <stdio.h>
#include <map>

using namespace std;


//Base Action

BaseAction::BaseAction() : errorMsg(), status(PENDING) {} // constructor




ActionStatus BaseAction::getStatus() const { return status;}





void BaseAction::complete() { status = COMPLETED;}

void BaseAction::error(const string &_errorMsg) {
    status = ERROR;
    errorMsg = _errorMsg;
    printf("Error: %s\n",errorMsg.c_str());
}

string BaseAction::getErrorMsg() const { return errorMsg;}


string BaseAction::getFullStatus (){
    if (status == COMPLETED) return "COMPLETED" ;
    else if (status == PENDING) return "PENDING";
    return "ERROR: " + errorMsg ;
    }

BaseAction::~BaseAction() {}

//Create User
void CreateUser::act(Session &sess) {

    string info = sess.getActionInfo();
    int pos = info.find(' ');
    string name = info.substr(0, pos);
    string algo = info.substr(pos+1);

    if ((algo != "rer") && (algo != "len") && (algo != "gen"))  // algo not valid
        error("3-letter code is invalid");
     else {
        User *newUser = sess.ifExist(name);
        if (newUser != nullptr) // username taken
            error("Username is already taken");
        else {
            if (algo == "len")
                sess.addUser(name, new LengthRecommenderUser(name));
            else if (algo == "rer")
                sess.addUser(name, new RerunRecommenderUser(name));
            else
                sess.addUser(name, new GenreRecommenderUser(name));
            complete();
        }
    }
}


//ChangeActiveUser
void ChangeActiveUser::act(Session &sess) {
    string name = sess.getActionInfo();
    User *newActiveUser = sess.ifExist(name);

    if (newActiveUser == nullptr) // user doesnt exist
        error("User doesn't exist");
    else {
        sess.setActiveUser(newActiveUser);
        complete();
    }
}

void DeleteUser::act(Session &sess) {

    string name = sess.getActionInfo();
    if (!sess.delUser(name)) // user doesnt exist
        error("User doesn't exist");
    else
        complete();
}

void DuplicateUser::act(Session &sess) {
    string info = sess.getActionInfo();
    int pos = info.find(' ');
    string originalUserName = info.substr(0, pos);
    string newUserName = info.substr(pos + 1);

    if (sess.ifExist(originalUserName) == nullptr)
        error("User doesn't exist");
    else if (sess.ifExist(newUserName) != nullptr)
        error("New user name is already taken");
    else {
        User *originalUser = sess.ifExist(originalUserName);
        User *toAdd = originalUser->clone(newUserName);
        sess.getUserMap().insert(make_pair(newUserName,toAdd));
        complete();
    }
}

void PrintContentList::act(Session &sess) {
    vector<Watchable *> contents = sess.getContent();
    for (auto item : contents) {
        vector<string> tags = item->getTags();
        string allTags = "";
        for (auto tag:tags)
            allTags = allTags + ", " + tag;
        allTags = allTags.substr(2);

        if (item->getType() == "movie")
            printf("%ld. %s %d minutes [%s]\n", item->getId(), item->toString().c_str(), item->getLength(), allTags.c_str());
        else
            printf("%ld. %s  %d minutes [%s]\n", item->getId(), item->toString().c_str(),  item->getLength(), allTags.c_str());
    }
    complete();
}

void PrintWatchHistory::act(Session &sess) {

    printf("Watch history for %s\n", sess.getActiveUser()->getName().c_str());
    vector<Watchable *> historyVector = sess.getActiveUser()->getHistory();

    int index = 1 ;
    for (auto item: historyVector) {
        printf("%d. %s\n", index, item->toString().c_str());
        index ++ ;
    }
    complete();
}

void Watch::act(Session &sess) {
    int id = stoi(sess.getActionInfo()) ;// check index
    pair<char, int> watchPair = watch(sess, id-1);
    complete();
    while (watchPair.first == 'y') {
        BaseAction *newWatch = new Watch();
        static_cast<Watch*>(newWatch)->complete();
        sess.getActionsLog().push_back(newWatch);
        watchPair = watch(sess, watchPair.second - 1);
    }
}

pair<char, int> Watch::watch(Session &sess, int id) {
    vector<Watchable *> contents = sess.getContent();
    string contentName = contents[id]->toString();
    printf("Watching %s\n", contentName.c_str());
    (sess.getActiveUser()->getHistory()).push_back(contents[id]);
    if (sess.getActiveUser()->getAlgo() == "len")
        static_cast<LengthRecommenderUser *>( sess.getActiveUser())->updateAvg(contents[id]->getLength());   // updates avg
    if (sess.getActiveUser()->getAlgo() == "gen") {
        for (auto tag: contents[id]->getTags()) {
            (static_cast<GenreRecommenderUser *>(sess.getActiveUser())->getTagMap()).insert(make_pair(tag, 0));
            ((static_cast<GenreRecommenderUser *>(sess.getActiveUser())->getTagMap()).find(tag)->second)++; // updates tagMap
        }
    }

    Watchable *nextWatch = contents[id]->getNextWatchable(sess);
    if (nextWatch != nullptr)
        printf("We recommend watching %s, continue? [y/n]\n", nextWatch->toString().c_str());

    char ans;
    cin >> ans;
    return make_pair(ans, nextWatch->getId());
}

void PrintActionsLog::act(Session &sess) {

    vector<BaseAction *> actionsLog = sess.getActionsLog();
    for (int i = actionsLog.size() - 1; i>= 0; i--)
        printf("%s %s\n", actionsLog[i]->toString().c_str(), actionsLog[i]->getFullStatus().c_str() ); // tostring
    complete();
}



void Exit::act(Session &sess) {
    complete();
}

string CreateUser::toString() const { return "CreateUser"; }

string ChangeActiveUser::toString() const { return  "ChangeActiveUser";}

string DeleteUser::toString() const { return "DeleteUser";}

string DuplicateUser::toString() const { return "Duplicate"; }

string PrintContentList::toString() const { return "PrintContentList"; }

string PrintWatchHistory::toString() const { return "PrintWatchHistory"; }

string Watch::toString() const { return "Watch"; }

string PrintActionsLog::toString() const { return "PrintActionsLog"; }

string Exit::toString() const { return "Exit"; }













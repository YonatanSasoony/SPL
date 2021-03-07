
#include <fstream>
#include <json.hpp>
#include "../include/Session.h"
#include "../include/Watchable.h"
#include "../include/User.h"
#include "../include/Action.h"
#include <iostream>
using namespace std;

Session::Session(const string &configFilePath):  content(),actionsLog(),userMap(),activeUser(),actionInfo()   { // constructor

    User* defaultUser = new LengthRecommenderUser("default"); 
    activeUser = defaultUser;
    userMap.insert(make_pair("default",defaultUser));

    ifstream ifs(configFilePath);
    nlohmann::json j = nlohmann::json::parse (ifs);
    nlohmann::json movies = j["movies"];
    long id (1) ;

    for (auto item : movies.items()){
        nlohmann::json toAdd = item.value();
        Watchable *newMovie = new Movie(id,toAdd["name"],toAdd["length"],toAdd["tags"]);
        content.push_back(newMovie);
        id++;

    }
    nlohmann::json tvSeries = j["tv_series"];
    for (auto item : tvSeries.items()){
        nlohmann::json toAdd = item.value();
        vector<int> seasons = toAdd["seasons"];
        int season = 1;
        for (unsigned long i=0; i< seasons.size(); i++){ //seasons
            int episode = 1;
            for (int k=0; k< seasons[i]; k++){ //episodes of each season
                Watchable *newEpisode = new Episode (id, toAdd["name"], toAdd["episode_length"], season, episode, toAdd["tags"] );
                content.push_back(newEpisode);
                if ( (i==seasons.size()-1 ) && (k==seasons[i]-1) )//last season and last episode
                    static_cast<Episode*>(newEpisode)->setAsLastEpisode();
                id++;
                episode++;
            }
            season++;
        }
    }
}

Session::~Session() { // destructor

    for (auto &watchable : content)
        delete watchable;
    for (auto &action : actionsLog)
        delete action;
    for (auto &pair: userMap)  // delete users - including active
        delete pair.second;
    activeUser = nullptr;

}

Session::Session(Session &other) : content(),actionsLog(),userMap(),activeUser(),actionInfo()  { // copy constructor
    deepCopy(other);

}

Session::Session(Session &&other) : content(),actionsLog(),userMap(),activeUser(),actionInfo() { //move copy constructor

    this->content = other.content;
    this->actionsLog = other.actionsLog;
    this->userMap = other.userMap;
    this->activeUser = other.activeUser;

    for (auto &item : other.content)
        item = nullptr ;

    for (auto &item : other.actionsLog)
        item = nullptr;

    for (auto &pair : other.userMap)
        pair.second = nullptr ;

    other.activeUser = nullptr;

}
Session& Session::operator=(Session &other){    // copy assignment


    if (this != &other)
    {
        for (auto &watchable : content)
            delete watchable;
        for (auto &action : actionsLog)
            delete action;
        for (auto &pair: userMap)  // delete users - including active
            delete pair.second;
        content.clear();
        actionsLog.clear();
        userMap.clear();
        activeUser = nullptr;

        deepCopy(other);
    }

    return *this;
}
Session& Session::operator=(Session &&other){    // move copy assignment

    if (this != &other)
    {
        for (auto &watchable : content)
            delete watchable;
        for (auto &action : actionsLog)
            delete action;
        for (auto &pair: userMap)  // delete users - including active
            delete pair.second;
        content.clear();
        actionsLog.clear();
        userMap.clear();
        activeUser = nullptr;

        this->content = other.content;
        this->actionsLog = other.actionsLog;
        this->userMap = other.userMap;
        this->activeUser = other.activeUser;

        for (auto &item : other.content)
            item = nullptr ;

        for (auto &item : other.actionsLog)
            item = nullptr;

        for (auto &pair : other.userMap)
            pair.second = nullptr ;
        other.activeUser = nullptr;

    }

    return *this;
}



vector<Watchable*>& Session::getContent(){return content;}
vector<BaseAction*>& Session::getActionsLog(){ return actionsLog;}
unordered_map<string,User*>& Session::getUserMap(){ return userMap;}
string& Session::getActionInfo(){ return actionInfo;}

void Session::addUser(string &name, User* user){userMap.insert(make_pair( name,user));}

bool Session::delUser(string &name){
        User *toDel = nullptr ;
        for (auto currUser : userMap )
            if (currUser.first == name)
                toDel = currUser.second;

       if(userMap.erase(name)){
           delete toDel;
           toDel = nullptr;
           return true;
       }
       return false;

}
void Session:: setActiveUser (User *_user) { activeUser = _user;}

User* Session::ifExist (string &name){
    for( auto currUser : userMap)
        if(currUser.first == name)
            return currUser.second;
    return nullptr ;
}

User* Session::getActiveUser(){
    return activeUser;
}

void Session::addToLog(BaseAction *act){ actionsLog.push_back(act);}

 void Session::start(){
    cout << "SPLFLIX is now on!" << endl;

     while (true){
        string input ;
        string actionType ;
        getline(cin,input);
        int pos = input.find(' ');
        actionType = input.substr(0,pos);
        actionInfo = input.substr(pos+1);

         bool flag = true ;
         BaseAction *action;
         if (actionType == "createuser") action = new CreateUser();
         else if (actionType == "changeuser") action = new ChangeActiveUser();
         else if (actionType == "deleteuser") action = new DeleteUser();
         else if (actionType == "dupuser")    action = new DuplicateUser();
         else if (actionType == "content")    action = new PrintContentList();
         else if (actionType == "watchhist")  action = new PrintWatchHistory();
         else if (actionType == "watch")      action = new Watch();
         else if (actionType == "log")        action = new PrintActionsLog();
         else if (actionType == "exit")       action = new Exit();
         else flag = false ;

         if(flag) {
             action->act(*this);
             actionsLog.push_back(action);
         }

         if (actionType == "exit") break;

     }

    }


    void Session::deepCopy (Session &other){
        for (auto watchable : other.content) {
            if (watchable->getType() == "movie")
                this->content.push_back(new Movie(static_cast<Movie&>(*watchable)));
            else
                this->content.push_back(new Episode(static_cast<Episode&>(*watchable)));
        }

        for (auto action : other.actionsLog) {

            if (action->toString()=="CreateUser")
                this->actionsLog.push_back(new CreateUser(static_cast<CreateUser&>(*action)));
            else if (action->toString()=="ChangeActiveUser")
                this->actionsLog.push_back(new ChangeActiveUser(static_cast<ChangeActiveUser&>(*action)));
            else if (action->toString()=="DeleteUser")
                this->actionsLog.push_back(new DeleteUser(static_cast<DeleteUser&>(*action)));
            else if (action->toString()=="DuplicateUser")
                this->actionsLog.push_back(new DuplicateUser(static_cast<DuplicateUser&>(*action)));
            else if (action->toString()=="PrintContentList")
                this->actionsLog.push_back(new PrintContentList(static_cast<PrintContentList&>(*action)));
            else if (action->toString()=="PrintWatchHistory")
                this->actionsLog.push_back(new PrintWatchHistory(static_cast<PrintWatchHistory&>(*action)));
            else if (action->toString()=="Watch")
                this->actionsLog.push_back(new Watch(static_cast<Watch&>(*action)));
            else if (action->toString()=="PrintActionsLog")
                this->actionsLog.push_back(new PrintActionsLog(static_cast<PrintActionsLog&>(*action)));
            else
                this->actionsLog.push_back(new Exit(static_cast<Exit&>(*action)));

        }
        for (auto pair: other.userMap) {

            if (pair.second->getAlgo() =="len")
                this->userMap.insert(make_pair(pair.first,new LengthRecommenderUser(static_cast<LengthRecommenderUser&>(*pair.second))));
            else if (pair.second->getAlgo() =="rer")
                this->userMap.insert(make_pair(pair.first,new RerunRecommenderUser(static_cast<RerunRecommenderUser&>(*pair.second))));
            else
                this->userMap.insert(make_pair(pair.first,new GenreRecommenderUser(static_cast<GenreRecommenderUser&>(*pair.second))));
        }

        for (auto pair : this->userMap){ //update each user's history to point at the new content
            for (auto &item : pair.second->getHistory()){
                int num = item->getId();
                item = this->content[num - 1];
            }
        }

        activeUser = (userMap.find(other.activeUser->getName()))->second;

    }






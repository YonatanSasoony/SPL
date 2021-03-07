#ifndef SESSION_H_
#define SESSION_H_

#include <vector>
#include <unordered_map>
#include <string>
#include "Action.h"
#include "User.h"


class User;
class Watchable;
class BaseAction;

class Session{
public:
    Session(const std::string &configFilePath);
    virtual ~Session();
    Session(Session&);
    Session(Session&&);
    Session&operator=(Session&);
    Session&operator=(Session&&);
    void start();
    std::vector<Watchable*>& getContent();
    std::vector<BaseAction*>& getActionsLog();
    std::unordered_map <std::string,User*>& getUserMap();
    User* getActiveUser();
    std::string& getActionInfo();
    void addUser(std::string&, User*);
    bool delUser(std::string&);
    void setActiveUser (User*);
    User* ifExist (std::string&);
    void addToLog(BaseAction*);
private:
    std::vector<Watchable*> content;
    std::vector<BaseAction*> actionsLog;
    std::unordered_map<std::string,User*> userMap;
    User* activeUser;
    std::string actionInfo;
    void deepCopy (Session &);
};
#endif

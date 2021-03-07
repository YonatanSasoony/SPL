#ifndef USER_H_
#define USER_H_

#include <vector>
#include <string>
#include <unordered_set>
#include <unordered_map>
#include <map>

class Session;
class User;
class Watchable;
class BaseAction;

class User{
public:
    User(const std::string&);//constructor
    User(const User &); // copy constructor
    User(User &&);// move copy constructor
    virtual User& operator=(User&);//copy assignment
    virtual User& operator=(User&&);//move copy assignment
    virtual ~User();//destructor



    virtual Watchable* getRecommendation(Session&) = 0;
    std::string getName() const;
    const std::vector<Watchable *> get_history() const;
    std::vector<Watchable*> & getHistory() ;
    virtual std::string getAlgo() = 0 ;
    void setName(std::string);
    virtual User* clone(std::string&)=0;

protected:
    std::vector<Watchable*> history;
    Watchable* getNextEpisode (Session &);
    bool isWatched (Watchable*);
    void copy (User &);
private:
    std::string name;

};


class LengthRecommenderUser : public User {
public:
    LengthRecommenderUser(const std::string&); //constructor
    LengthRecommenderUser(const LengthRecommenderUser&); // copy constructor
    LengthRecommenderUser(LengthRecommenderUser&&); // move copy constructor
    virtual LengthRecommenderUser& operator= (LengthRecommenderUser &); //copy assignment
    virtual LengthRecommenderUser& operator= (LengthRecommenderUser &&); //move copy assignment
    virtual ~LengthRecommenderUser(); //destructor

    virtual Watchable* getRecommendation(Session&);
    virtual std::string getAlgo ();
    int getAvg();
    void updateAvg (int length);
    virtual User* clone(std::string&);
private:
    int avg;
};

class RerunRecommenderUser : public User {
public:
    RerunRecommenderUser(const std::string&); //constructor
    RerunRecommenderUser(const RerunRecommenderUser&); // copy constructor
    RerunRecommenderUser(RerunRecommenderUser&&); // move copy constructor
    virtual RerunRecommenderUser& operator= (RerunRecommenderUser &); //copy assignment
    virtual RerunRecommenderUser& operator= (RerunRecommenderUser &&); //move copy assignment
    virtual ~RerunRecommenderUser(); //destructor
    virtual Watchable* getRecommendation(Session&);
    virtual std::string getAlgo ();
    virtual User* clone(std::string&);
private:
    int recIndex;
};

class GenreRecommenderUser : public User {
public:
    GenreRecommenderUser(const std::string&); //constructor
    GenreRecommenderUser(const GenreRecommenderUser&); // copy constructor
    GenreRecommenderUser(GenreRecommenderUser&&); // move copy constructor
    virtual GenreRecommenderUser& operator= (GenreRecommenderUser &); //copy assignment
    virtual GenreRecommenderUser& operator= (GenreRecommenderUser &&); //move copy assignment
    virtual ~GenreRecommenderUser(); //destructor
    virtual Watchable* getRecommendation(Session&);
    virtual std::string getAlgo ();
    std::map< std::string ,int>& getTagMap();
    virtual User* clone(std::string&);
    std::map<std::string, int> copyMap();
private:
    std::map< std::string ,int> tagMap;


};

#endif

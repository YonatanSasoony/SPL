#ifndef WATCHABLE_H_
#define WATCHABLE_H_

#include <string>
#include <vector>


class Session;
class User;
class BaseAction;
using namespace std;
class Watchable{
public:
    Watchable(long id, int length, const std::vector<std::string>& tags);
    virtual ~Watchable();
    virtual std::string toString() const = 0;
    virtual Watchable* getNextWatchable(Session&) const = 0;
    virtual std::string getName()=0;
    int getSeason();
    int getEpisode();
    virtual std::string getType()=0;
    long getId();
    int getLength();
    std::vector<std::string> getTags();
    long getNextEpisodeId ();
    void setAsLastEpisode();
    bool contains(string);

private:
    const long id;
    int length;
    std::vector<std::string> tags;
};

class Movie : public Watchable{
public:
    Movie(long id, const std::string& name, int length, const std::vector<std::string>& tags);
    Movie(Movie&);
    virtual std::string toString() const;
    virtual Watchable* getNextWatchable(Session&) const;
    virtual std::string getName();
    virtual std::string getType();

private:
    std::string name;
};


class Episode: public Watchable{
public:
    Episode(long id, const std::string& seriesName,int length, int season, int episode ,const std::vector<std::string>& tags);
    Episode(Episode&);
    virtual std::string toString() const;
    virtual Watchable* getNextWatchable(Session&) const;
    virtual std::string getName();
    int getSeason();
    int getEpisode();
    virtual std::string getType();
    long getNextEpisodeId ();
    void setAsLastEpisode();

private:
    std::string seriesName;
    int season;
    int episode;
    long nextEpisodeId;
};

#endif

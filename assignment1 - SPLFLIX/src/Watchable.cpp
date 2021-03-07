#include "../include/Watchable.h"
#include "../include/Session.h"
#include "../include/User.h"
#include "../include/Action.h"

using namespace std;


Watchable::Watchable(long _id, int _length, const vector<string> &_tags):id(_id),length(_length),tags(_tags){} // constructor
Watchable::~Watchable() {} // destructor

long Watchable::getId(){ return id;}
int Watchable:: getLength( ){return length;}

bool Watchable::contains(string favTag) {
    for (auto tag:tags)
        if (tag ==favTag)
            return true;
    return false;
}

vector<string> Watchable::getTags() { return tags;}


Watchable* Movie::getNextWatchable(Session &sess) const {
    return sess.getActiveUser()->getRecommendation(sess);
}

Movie::Movie(long _id, const string &_name, int _length, const vector<string> &_tags) : Watchable(_id,_length,_tags),
name(_name){}

Movie::Movie(Movie &other): Watchable(other),name(other.name) {}

string Movie::toString()const{return name ;}

string Movie::getName() {return name;}
string Movie::getType() {return "movie";}






Episode::Episode(long _id, const string &_seriesName, int _length, int _season, int _episode,
                 const vector<string> &_tags): Watchable(_id, _length, _tags), seriesName(_seriesName),
                 season(_season), episode(_episode) , nextEpisodeId(_id + 1) {}



Episode::Episode(Episode &other): Watchable(other), seriesName(other.seriesName),season(other.season),
 episode(other.episode),nextEpisodeId(other.nextEpisodeId) {}


string Episode::toString() const {
    return seriesName + " S" + to_string(season) + "E" + to_string(episode) ;
}

Watchable* Episode::getNextWatchable(Session &sess) const {
    return   sess.getActiveUser()->getRecommendation(sess);
}


int Episode::getSeason(){ return season;};
int Episode::getEpisode(){return episode;};
string Episode::getName() {return seriesName;}
string Episode::getType() {return "episode";}

long Episode::getNextEpisodeId (){return nextEpisodeId;}
void Episode::setAsLastEpisode(){nextEpisodeId = -1;}


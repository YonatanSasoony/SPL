
#include "../include/User.h"
#include "../include/Session.h"
#include "../include/Watchable.h"
#include "../include/Action.h"

using namespace std;

// USER:
User::User(const string &_name) : history(),name(_name)  {} //  constructor
User::User(const User &other) :history(other.history), name(other.getName()) {} // copy constructor

User::User(User &&other)  :history(other.history), name(other.getName()){ //move copy constructor
    this->copy(other);

    for (auto &item : other.history)
        item = nullptr;

}

User &User::operator=(User &other) { //copy assignment
    if (this != &other) {
        for (auto &item : history)
            item = nullptr;
        history.clear();
        this->copy(other);
    }
    return *this;
}

User &User::operator=(User &&other) { // move copy assignment
    if (this != &other) {
        for (auto &item : history)
            item = nullptr;
        history.clear();
        this->copy(other);

        for (auto &item : other.history)
            item = nullptr;
    }
    return *this;
}

User::~User() {     // Destructor
    for (auto &item : history)
        item = nullptr;
}


void User::copy(User &other) {
    name = other.getName();

    for (auto item : other.getHistory()) {
        history.push_back(item);
    }
}


string User::getName() const { return name; }

const vector<Watchable *> User::get_history() const { return history; }

vector<Watchable *> &User::getHistory() { return history; }

void User::setName(string _name) { name = _name; }

Watchable *User::getNextEpisode(Session &sess) {

    vector<Watchable *> history = sess.getActiveUser()->getHistory();
    Watchable *lastHistory = history.back(); //back() returns a reference to the last Watchable in history
    if (lastHistory->getType() == "episode") {
        long next = static_cast<Episode *>(lastHistory)->getNextEpisodeId();
        if (next != -1) // there is another episode to watch
            return sess.getContent()[next - 1];
    }
    return nullptr; // last episode of series or a movie
}

bool User::isWatched(Watchable *w) {
    for (auto item : history)
        if (w == item)
            return true;
    return false;
}


// LengthRecommenderUser

LengthRecommenderUser::LengthRecommenderUser(const string &name) : User(name), avg(0) {} //  constructor

LengthRecommenderUser::LengthRecommenderUser(const LengthRecommenderUser &other) : User(other),
avg(other.avg) {} // copy constructor

LengthRecommenderUser::LengthRecommenderUser(LengthRecommenderUser &&other) :User(other),
avg(other.avg){} // move copy constructor

LengthRecommenderUser& LengthRecommenderUser::operator=(LengthRecommenderUser &other) { //copy assignment

    if (this != &other) {
        for (auto &item : history)
            item = nullptr;
        history.clear();
        this->copy(other);
        this->avg = other.avg;
    }
    return *this;
}

LengthRecommenderUser& LengthRecommenderUser::operator=(LengthRecommenderUser &&other) {//move copy assignment
    if (this != &other) {
        for (auto &item : history)
            item = nullptr;
        history.clear();

        this->copy(other);
        this->avg = other.avg;

        for (auto &item : other.history)
            item = nullptr;
    }
    return *this;
}

LengthRecommenderUser::~LengthRecommenderUser() {     // Destructor
    for (auto &item : history)
        item = nullptr;
}


Watchable *LengthRecommenderUser::getRecommendation(Session &sess) {
    Watchable *next = getNextEpisode(sess);
    if (next != nullptr)    // there is another episode to watch
        return next;

    int diff = INT16_MAX; //***************
    Watchable *toWatch = nullptr;
    for (auto item : sess.getContent()) {
        if (!isWatched(item)) {
            if (diff > abs((item->getLength()) - avg)) {
                toWatch = item;
                diff = abs((item->getLength()) - avg);
            }
        }
    }
    return toWatch;
}

string LengthRecommenderUser::getAlgo() { return "len"; }

int LengthRecommenderUser::getAvg() { return avg; }

void LengthRecommenderUser::updateAvg(int length) { avg = (avg * (history.size() - 1) + length) / history.size(); }


User *LengthRecommenderUser::clone(string &newName) {

    User *newUser = new LengthRecommenderUser(newName);
    static_cast<LengthRecommenderUser *>(newUser)->avg = avg;
    for (auto item : history)
        newUser->getHistory().push_back(item);

    return newUser;
}

// RerunRecommenderUser

RerunRecommenderUser::RerunRecommenderUser(const string &name) : User(name), recIndex(0) {} // constructor

RerunRecommenderUser::RerunRecommenderUser(const RerunRecommenderUser &other) : User(other),
recIndex(other.recIndex) {} // copy constructor

RerunRecommenderUser::RerunRecommenderUser(RerunRecommenderUser &&other) :User(other),
recIndex(other.recIndex){} // move copy constructor

RerunRecommenderUser& RerunRecommenderUser::operator=(RerunRecommenderUser &other) { //copy assignment

    if (this != &other) {
        if (this != &other) {
            for (auto &item : history)
                item = nullptr;
            history.clear();
            this->copy(other);
            this->recIndex = other.recIndex;
        }
    }
        return *this;
}

RerunRecommenderUser& RerunRecommenderUser::operator=(RerunRecommenderUser &&other) { //move copy assignment
    if (this != &other) {
        for (auto &item : history)
            item = nullptr;
        history.clear();

        this->copy(other);
        this->recIndex = other.recIndex;

        for (auto &item : other.history)
            item = nullptr;
    }
    return *this;
}


RerunRecommenderUser::~RerunRecommenderUser() {     // Destructor
        for (auto &item : history)
            item = nullptr;

}





Watchable *RerunRecommenderUser::getRecommendation(Session &sess) {
    Watchable *next = getNextEpisode(sess);
    if (next != nullptr)    // there is another episode to watch
        return next;
    Watchable *toWatch = history[recIndex];
    recIndex = (recIndex + 1) % history.size();
    return toWatch;
}

string RerunRecommenderUser::getAlgo() { return "rer"; }

User *RerunRecommenderUser::clone(string &newName) {

    User *newUser = new RerunRecommenderUser(newName);
    static_cast<RerunRecommenderUser *>(newUser)->recIndex = recIndex;
    return newUser;
}

// GenreRecommenderUser

GenreRecommenderUser::GenreRecommenderUser(const std::string &name):User(name),tagMap(){} // constructor

GenreRecommenderUser::GenreRecommenderUser(const GenreRecommenderUser &other) : User(other),
tagMap(other.tagMap) {} // copy constructor

GenreRecommenderUser::GenreRecommenderUser(GenreRecommenderUser &&other) :User(other),
tagMap(other.tagMap){} // move copy constructor

GenreRecommenderUser &GenreRecommenderUser::operator=(GenreRecommenderUser &other) { //copy assignment

    if (this != &other) {
        for (auto &item : history)
            item = nullptr;
        history.clear();
        this->copy(other);
        this->tagMap = other.tagMap;
    }
    return *this;
}

GenreRecommenderUser &GenreRecommenderUser::operator=(GenreRecommenderUser &&other) {//move copy assignment
    if (this != &other) {
        for (auto &item : history)
            item = nullptr;
        history.clear();

        this->copy(other);
        this->tagMap = other.tagMap;

        for (auto &item : other.history)
            item = nullptr;
    }
    return *this;
}

GenreRecommenderUser::~GenreRecommenderUser() {     // Destructor
        for (auto &item : history)
            item = nullptr;
}


User *GenreRecommenderUser::clone(string &newName) {
    User *newUser = new GenreRecommenderUser(newName);
    static_cast<GenreRecommenderUser *>(newUser)->tagMap = this->copyMap();
    return newUser;
}

map<string, int> &GenreRecommenderUser::getTagMap() { return tagMap; }

map<string, int> GenreRecommenderUser::copyMap() {
    map<string, int> temp;
    for (auto item: tagMap)
        temp.insert(item);
    return temp;
}

Watchable *GenreRecommenderUser::getRecommendation(Session &sess) {
    Watchable *next = getNextEpisode(sess);
    if (next != nullptr)    // there is another episode to watch
        return next;
    map<string, int> temp = copyMap(); // generates a map with number of appearance of each tag



    while (temp.size() > 0) {
        int tagNum = 0;
        string favTag;
        for (auto item:temp) { // finds favourite tag available
            if (tagNum < item.second) {
                tagNum = item.second;
                favTag = item.first;
            }
        }
        for (auto item: sess.getContent())
            if ((isWatched(item) == false) && (item->contains(favTag) == true))
                return item;
        temp.erase(favTag);
    }
    return nullptr;
}

string GenreRecommenderUser::getAlgo() { return "gen"; }






















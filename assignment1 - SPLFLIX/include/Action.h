#ifndef ACTION_H_
#define ACTION_H_
#include <string>
#include <iostream>

class Session;
class User;
class Watchable;

enum ActionStatus{
	PENDING, COMPLETED, ERROR
};


class BaseAction{
public:
	BaseAction();
    virtual ~BaseAction();
	ActionStatus getStatus() const;
	virtual void act(Session&)=0;
	virtual std::string toString() const=0;
    std::string getFullStatus ();
protected:
	void complete();
	void error(const std::string&);
	std::string getErrorMsg() const;

private:
	std::string errorMsg;
	ActionStatus status;
};

class CreateUser  : public BaseAction {
public:
	virtual void act(Session&);
	virtual std::string toString() const;
};

class ChangeActiveUser : public BaseAction {
public:
	virtual void act(Session&);
	virtual std::string toString() const;
};

class DeleteUser : public BaseAction {
public:
	virtual void act(Session&);
	virtual std::string toString() const;
};


class DuplicateUser : public BaseAction {
public:
	virtual void act(Session&);
	virtual std::string toString() const;
};

class PrintContentList : public BaseAction {
public:
	virtual void act (Session&);
	virtual std::string toString() const;
};

class PrintWatchHistory : public BaseAction {
public:
	virtual void act (Session&);
	virtual std::string toString() const;
};


class Watch : public BaseAction {
public:
	virtual void act(Session&);
	virtual std::string toString() const;
private:
    std::pair<char,int> watch(Session&, int);
};


class PrintActionsLog : public BaseAction {
public:
    virtual void act(Session &sess);

    virtual std::string toString() const;


};

class Exit : public BaseAction {
public:
	virtual void act(Session& sess);
	virtual std::string toString() const;
};


#endif

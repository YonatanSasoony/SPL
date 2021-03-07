CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude -pthread
LDFLAGS:=-lboost_system -lpthread

all: StompBookClubClient
	@echo 'Building target: StompBookClubClient'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/StompBookClubClient bin/ConnectionHandler.o bin/ClientRunner.o bin/Main.o $(LDFLAGS)
	@echo 'Finished building target: StompBookClubClient'
	@echo ' '

StompBookClubClient: bin/ConnectionHandler.o bin/ClientRunner.o bin/Main.o $(LDFLAGS)


# Depends on the source and header files
bin/ClientRunner.o: src/ClientRunner.cpp
	g++ $(CFLAGS) -o bin/ClientRunner.o src/ClientRunner.cpp

bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/Main.o: src/Main.cpp
	g++ $(CFLAGS) -o bin/Main.o src/Main.cpp
.PHONY: clean

#Clean the build directory
clean:
	rm -f bin/*

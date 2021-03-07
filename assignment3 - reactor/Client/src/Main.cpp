#include <iostream>
#include <mutex>
#include <thread>
#include "../include/ClientRunner.h"

using boost::asio::ip::tcp;
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

class Task{
private:
    int id;
    std::mutex & mutex;
public:
    Task (int _id, std::mutex& _mutex) : id(_id), mutex(_mutex) {}

    void run(){
        for (int i= 0; i < 100; i++){
            std::lock_guard<std::mutex> lock(mutex); // constructor locks the mutex while destructor (out of scope) unlocks it
            std::cout << i << ") Task " << id << " is working" << std::endl;
        }
    }
};



int main() {
    ClientRunner  clientRunner;
    clientRunner.start();

    return 0;

}
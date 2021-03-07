import sqlite3
import os
import atexit
import sys

if os.path.isfile('moncafe.db'):
    os.remove('moncafe.db')

connection = sqlite3.connect('moncafe.db')
cursor = connection.cursor()


def close_db():
    connection.commit()
    cursor.close()
    connection.close()


atexit.register(close_db)  # register close_db() to be executed at the end of the program

cursor.execute("""
                CREATE TABLE Coffee_stands (
                    id                             INT     PRIMARY KEY,
                    location                       TEXT    NOT NULL,
                    number_of_employees            INT        
                    )""")
cursor.execute("""
            CREATE TABLE Employees (
                id                          INT     PRIMARY KEY,
                name                        TEXT    NOT NULL,
                salary                      REAL    NOT NULL,
                coffee_stand                INT     NOT NULL,
                FOREIGN KEY(coffee_stand)   REFERENCES Coffee_stands(id)
                        )""")

cursor.execute("""
                CREATE TABLE Suppliers (
                    id                      INT     PRIMARY KEY,
                    name                    TEXT    NOT NULL,
                    contact_information    TEXT
                    )""")

cursor.execute("""
                CREATE TABLE Products (
                    id              INT     PRIMARY KEY,
                    description     TEXT    NOT NULL,
                    price           REAL    NOT NULL,
                    quantity        INT     NOT NULL        
                    )""")

cursor.execute("""
                CREATE TABLE Activities (
                    product_id              INT    NOT NULL,
                    quantity                INT    NOT NULL,
                    activator_id            INT    NOT NULL,
                    date                    DATE   NOT NULL,
                    FOREIGN KEY(product_id)  REFERENCES Products(id)                
                    )""")

cursor.execute("""
                CREATE TABLE Reports (
                    employee_id                 INT     NOT NULL,
                    total                       REAL NOT NULL,
                    FOREIGN KEY(employee_id)    REFERENCES Employees(id)
                    )""")


def add_employee(_id, name, salary, coffee_stand):
    cursor.execute("INSERT INTO Employees VALUES (?, ?, ?, ?)", (_id, name, salary, coffee_stand))
    cursor.execute("INSERT INTO Reports   VALUES (?, ?)", (_id, 0))


def add_supplier(_id, name, contact_info):
    cursor.execute("INSERT INTO Suppliers VALUES (?, ?, ?)", (_id, name, contact_info))


def add_product(_id, description, price, quantity):
    cursor.execute("INSERT INTO Products VALUES (?, ?, ?, ?)", (_id, description, price, quantity))


def add_coffee_stand(_id, location, number_of_employees):
    cursor.execute("INSERT INTO Coffee_stands VALUES (?, ?, ?)", (_id, location, number_of_employees))


file = open(sys.argv[1], "r")
for line in file:
    letter = line[0]
    info = line[3:len(line) - 1]
    fields = info.split(", ")
    if letter == 'E':
        add_employee(int(fields[0]), fields[1], float(fields[2]), int(fields[3]))
    elif letter == 'S':
        add_supplier(int(fields[0]), fields[1], fields[2])
    elif letter == 'P':
        add_product(int(fields[0]), fields[1], float(fields[2]), 0)
    elif letter == 'C':
        add_coffee_stand(int(fields[0]), fields[1], int(fields[2]))

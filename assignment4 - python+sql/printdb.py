import sqlite3


def print_activities(name, sort_key):
    cursor.execute("SELECT * FROM Activities ORDER BY date ")
    table = cursor.fetchall()
    for row in table:
        print(row)


def print_coffee_stands(name, sort_key):
    cursor.execute("SELECT * FROM Coffee_stands ORDER BY id ")
    table = cursor.fetchall()
    for row in table:
        print(row)


def print_employees(name, sort_key):
    cursor.execute("SELECT * FROM Employees ORDER BY id ")
    table = cursor.fetchall()
    for row in table:
        print(row)


def print_products(name, sort_key):
    cursor.execute("SELECT * FROM Products ORDER BY id ")
    table = cursor.fetchall()
    for row in table:
        print(row)


def print_suppliers(name, sort_key):
    cursor.execute("SELECT * FROM Suppliers ORDER BY id ")
    table = cursor.fetchall()
    for row in table:
        print(row)


def print_employee_report():
    print("Employees report")
    table = cursor.execute("""SELECT Employees.name, Employees.salary, Coffee_stands.location, Reports.total 
                               FROM Employees JOIN Coffee_stands ON Employees.coffee_stand = Coffee_stands.id
                               JOIN Reports ON Employees.id = Reports.employee_id
                               ORDER BY Employees.name""")
    employee_report = table.fetchall()
    for row in employee_report:
        print_row(row)
    print()


def print_row(row):
    print("{0} {1} {2} {3}".format(row[0], row[1], row[2], row[3]))


def print_activities_report():
    print("Activities")
    table = cursor.execute("""
    SELECT Activities.date, Products.description, Activities.quantity, Employees.name, Suppliers.name
                              FROM Activities JOIN Products ON Activities.product_id = Products.id
                              LEFT Join  Employees ON Activities.activator_id = Employees.id
                              LEFT Join  Suppliers ON Activities.activator_id = Suppliers.id                  
                              ORDER BY Activities.date""")

    activities_report = table.fetchall()
    if len(activities_report) != 0:
        for row in activities_report:
            print(row)


def print_tables():
    print("Activities")
    print_activities("Activities", "date")
    print("Coffee stands")
    print_coffee_stands("Coffee_stands", "id")
    print("Employees")
    print_employees("Employees", "id")
    print("Products")
    print_products("Products", "id")
    print("Suppliers")
    print_suppliers("Suppliers", "id")
    print()


def printdb():
    print_tables()
    print_employee_report()
    print_activities_report()


connection = sqlite3.connect('moncafe.db')
cursor = connection.cursor()
if __name__ == '__main__':
    printdb()

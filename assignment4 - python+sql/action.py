import atexit
import sqlite3
import sys
import printdb


def set_quantity(product_id, quantity):
    cursor.execute(""" UPDATE Products
                       SET    quantity = {0}
                       WHERE id = {1} """.format(quantity, product_id)
                   )


def update_records(activator_id, income):
    field = cursor.execute("SELECT total FROM Reports Where employee_id = ?", [activator_id])
    curr_total = float(field.fetchone()[0])

    cursor.execute("UPDATE Reports SET total = ? WHERE employee_id = ?  ", (curr_total + income, activator_id))


def act():
    info = line[0:len(line) - 1]
    fields = info.split(", ")
    product_id = int(fields[0])
    act_quantity = fields[1]
    if act_quantity[0] == "-":
        act_quantity = int((fields[1])[1:])
        act_quantity = act_quantity * (-1)
    else:
        act_quantity = int(fields[1])
    activator_id = int(fields[2])
    date = fields[3]

    field = cursor.execute("SELECT quantity, price FROM Products WHERE id = ?", [product_id])
    product_info = field.fetchone()
    curr_quantity = int(product_info[0])
    curr_quantity = curr_quantity + act_quantity
    curr_price = float(product_info[1])

    if act_quantity > 0:  # supply
        cursor.execute("INSERT INTO Activities VALUES (?, ?, ?, ?)", (product_id, act_quantity, activator_id, date))
        set_quantity(product_id, curr_quantity)
    elif (act_quantity < 0) and (curr_quantity >= 0):  # Sell
        cursor.execute("INSERT INTO Activities VALUES (?, ?, ?, ?)", (product_id, act_quantity, activator_id, date))
        set_quantity(product_id, curr_quantity)
        update_records(activator_id, act_quantity * curr_price * (-1))


def close_db():
    connection.commit()
    cursor.close()
    connection.close()


connection = sqlite3.connect('moncafe.db')
cursor = connection.cursor()
atexit.register(close_db)  # register close_db() to be executed at the end of the program

file_path = sys.argv[1]
file = open(file_path, 'r')
text = file.readlines()
for line in text:
    act()
connection.commit()
printdb.printdb()





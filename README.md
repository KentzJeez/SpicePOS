![LOGIN](https://github.com/user-attachments/assets/d12eeec5-9306-4aed-9f29-74edf8c3a65e)
![dashboard](https://github.com/user-attachments/assets/164a0a5b-b95f-47ea-9784-3e65740b1afb)
![update](https://github.com/user-attachments/assets/dc0ef43a-002b-4d06-a386-9c85973d4ae7)
![purchase](https://github.com/user-attachments/assets/e25fb0a1-3232-4802-a80e-d4334b535107)
![sell](https://github.com/user-attachments/assets/642e0144-6927-41f8-a946-41950f4a290f)

![add](https://github.com/user-attachments/assets/d5cf351c-2817-4eb5-bcee-376f9d05d470)

![inventory](https://github.com/user-attachments/assets/e5d04f71-1238-442a-8f6f-b8fe6cfda8a0)



Spice Business Management System
This Java-based Spice Business Management System is designed to help manage spice inventory and purchasing processes efficiently. The system includes features like adding and purchasing spices, updating spice prices, and managing quantities in stock. It uses a MySQL database to store and manage data, and the user interface is built using NetBeans.

Features
Spice Selection: Users can select spices from a dropdown list, which is populated dynamically from the database.
Buying Price Display: Upon selecting a spice, the current buying price of the spice is displayed.
Quantity Input: Users can input the quantity of spices they wish to purchase.
Total Price Calculation: The system automatically calculates the total cost based on the quantity entered and the buying price of the selected spice.
Database Integration: Spice data, including name, quantity, and buying price, are stored and managed in a MySQL database.
Spice Purchase: After selecting a spice and entering the quantity, users can update the database with the new quantity of the purchased spice.

Technology Stack
Java (JDK 8+): Core programming language used for building the application.
NetBeans IDE: Used to develop the GUI components.
MySQL: Database for storing and retrieving spice information.(XAMPP)
JDBC: Java API used for connecting and executing queries in the MySQL database.
Installation and Setup
Clone the repository to your local machine:

bash
Copy code
git clone https://github.com/yourusername/spice-business-management.git
Open the project in NetBeans IDE.

Configure the MySQL Database:

Create a database named spice_business (or use any preferred name).
Import the spice_business.sql file to set up the database schema and initial data.
Update the database connection details in the Java code:

Modify the DB_URL, DB_USER, and DB_PASSWORD in the PurchaseSpice class according to your local MySQL setup.
Run the application through NetBeans or compile it using the command line:

This project is open-source and available under the MIT License.

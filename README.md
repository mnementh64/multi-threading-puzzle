# Solution to an academic Multi-threading problem

## The Post Office


In a post office, there is one clerk, and there are 9 post cars to deliver parcels. Inside the office there are 3 seats for customers.

The clerk sleeps all the time. He only wakes up to serve the customers once all 3 seats are full.

Otherwise the clerk wakes up when all post cars come back to the office. He then prepares all the parcels, loads them one by one to the cars and dispatches the cars one by one for delivery. Cars come back to the office once the delivery is done. Each car travels a random distance to deliver and come back to office.

The post office has at least 10 regular customers. Customers come to the office at any time in a random fashion.

There are infinite number of parcels in the office. 

*Write a program to simulate the post office.*

###Constraints
- Do not use any library except java.lang.
- Each post car and customer will have its own thread.
- Console output with timestamp showing, agent, and it's state. 
- Code needs to be uploaded to the submission site in zip.
- Should contain the jar which is executable from command line.  

###Extra Credit
Assume clerk interrupts serving clients to load the cars once all cars come.

###Super Credit
Assume parcels are brought by customers. Each customer may bring 1 or 2 parcels in a random fashion. 
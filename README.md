# Group-5-Lab3-Enterprise-Warehouse-Management-System (Ghudsan, Osama, Waqar)
Group 5-Lab3 Enterprise Warehouse Management System


## CURL commands for CRUD operations
curl -X GET http://localhost:8080/api/products
curl -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d "{\"name\": \"Mechanical Keyboard\", \"price\": 89.99}"
curl -X GET http://localhost:8080/api/products/1
curl -X PUT http://localhost:8080/api/products/1 -H "Content-Type: application/json" -d "{\"name\": \"Ergonomic Keyboard\", \"price\": 104.99}"
curl -X DELETE http://localhost:8080/api/products/1 

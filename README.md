# Grupp-5-Lab3-Enterprise-Warehouse-Management-System (Ghudsan, Osama, Waqar)
Grupp 5-Lab3 Enterprise Warehouse Management System


## CURL-kommandon för CRUD-operationer
* curl -X GET http://localhost:8080/api/products
* curl -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d "{\"name\": \"Mechanical Keyboard\", \"price\": 89.99}"
* curl -X GET http://localhost:8080/api/products/1
* curl -X PUT http://localhost:8080/api/products/1 -H "Content-Type: application/json" -d "{\"name\": \"Ergonomic Keyboard\", \"price\": 104.99}"
* curl -X DELETE http://localhost:8080/api/products/1

Testlänkar för Java Streams (Del 2)Kopiera och klistra in dessa länkar direkt i webbläsaren när applikationen körs:

1. Sök & Filtrera: Kategori
   Hämtar bara produkter som tillhör en viss kategori (t.ex. Elektronik).
* Länk: http://localhost:8080/api/products/search/category?category=Elektronik
* Tips: Ändra Elektronik i slutet av länken

2. Sök & Filtrera: Varning för lågt lagersaldo
   Hittar alla produkter som har ett lagersaldo under ett visst tröskelvärde (t.ex. under 5 st).
* Länk: http://localhost:8080/api/products/search/low-stock?threshold=5
* Tips: Ni kan ändra siffran 5 till vad ni vill för att ändra gränsen för varningen.

3. Analys: Totalt lagervärde
   Räknar ut det totala värdet på hela lagret (pris × lagersaldo) för alla produkter tillsammans.
* Länk: http://localhost:8080/api/products/analytics/total-value

4. Analys: Medelpris per kategori
   Grupperar produkterna efter sin kategori och räknar ut det matematiska medelpriset för varje grupp.
* Länk: http://localhost:8080/api/products/analytics/average-prices

5. Sortering: Topp N dyraste produkter
   Sorterar produkterna efter pris (dyrast först) och begränsar listan till det antal (limit) ni valt.
* Länk: http://localhost:8080/api/products/analytics/top-expensive?limit=2

6. Sortering: Topp N mest populära produkter
   Sorterar produkterna efter lagersaldo (högst saldo först) och begränsar listan till det antal (limit) ni valt.
* Länk: http://localhost:8080/api/products/analytics/top-popular?limit=2



## Reflektion om hur Spring Boot underlättar utvecklingen
Spring Boot gör det mycket enklare att bygga program eftersom det har färdiga inställningar, så utvecklare slipper konfigurera allt manuellt och sparar mycket tid. När man jämför Java och Spring Boot med Microsofts C# och .NET, ser man att båda systemen är mycket populära för stora företag, väldigt säkra och byggda med liknande programmeringsspråk. Den största skillnaden är att .NET kommer helt från Microsoft och är väldigt smidigt och färdigt direkt, medan Java är mer uppdelat och bygger på att man pusslar ihop olika gratisverktyg från hela världen.

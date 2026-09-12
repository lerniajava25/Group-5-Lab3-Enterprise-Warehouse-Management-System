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



---

## JUnit-testning och Mockito

För att kontrollera att systemet fungerar korrekt har vi skrivit enhetstester med **JUnit 5**. Ett enhetstest testar en liten del av programmet, till exempel en metod i `ProductService`, utan att hela applikationen behöver startas.

I våra tester kontrollerar vi bland annat att:

* produkter får ett unikt ID när de skapas
* produkter kan hämtas, uppdateras och raderas
* sökning efter kategori och produkter med lågt lagersaldo fungerar
* det totala lagervärdet och medelpriset per kategori räknas ut korrekt
* produkter sorteras rätt efter pris eller lagersaldo
* ett okänt ID eller ett ogiltigt värde hanteras på rätt sätt

JUnit använder olika metoder för att kontrollera resultatet. Exempelvis betyder `assertEquals` att det förväntade värdet ska vara samma som det faktiska värdet, medan `assertTrue` kontrollerar att ett villkor är sant. Med `@BeforeEach` skapas en ny och tom `ProductService` före varje test. På så sätt påverkar inte ett test nästa test.

### Mockito

**Mockito** används för att skapa en mock, alltså ett simulerat objekt som används i stället för ett riktigt objekt. I `ProductServiceTest` används `@Mock` på en simulerad `Product`. Med `when(...).thenReturn(...)` bestämmer vi vad mock-objektet ska returnera:

```java
when(updatedProduct.getName()).thenReturn("Mouse");
when(updatedProduct.getPrice()).thenReturn(29.99);
```

Det gör att vi kan testa uppdateringen utan att behöva skapa en separat produkt på riktigt. Med `verify` kontrollerar vi dessutom att rätt metod  anropades:

```java
verify(updatedProduct).getName();
verify(updatedProduct).getPrice();
```

### Happy path och edge cases

Vi använder både **happy path-tester** och **edge case-tester**:

* **Happy path** betyder att systemet får giltiga värden och ska fungera normalt. Exempel är att skapa en produkt, hitta en produkt med ett befintligt ID eller räkna ut lagervärdet för produkter som finns i lagret.
* **Edge cases** testar gränser och ovanliga situationer. Exempel i våra tester är ett tomt lager, ett ID som inte finns, en produkt som ligger exakt på gränsen för lågt lagersaldo, `n = 0` vid sortering samt ett negativt `n`.


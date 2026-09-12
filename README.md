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

# Teknisk Rapport: Lagerhanteringssystem (Java Streams & Analys)

Denna rapport dokumenterar hur minnesbaserad databearbetning har implementerats med Java Streams API samt en branschanalys kring detta teknikval.

---

## 1. Reflektion: Hur Spring Boot underlättar utvecklingen av Stream-logik

Spring Boot har underlättat utvecklingen av vår avancerade lageranalys genom att erbjuda en sömlös integration mellan webb-endpoints och vårt interna servicelager:

*   **Sömlös parametrisering till Streams:** Genom `@RequestParam` (t.ex. vid `/search/low-stock?threshold=5` eller `?limit=2`) mappar Spring Boot automatiskt värden från webbläsarens adressfält direkt in som argument till våra Stream-metoder. Vi slipper skriva manuell kod för att hämta, typkonvertera och validera text från HTTP-anrop.
*   **Direkt JSON-serialisering av komplexa Streams-resultat:** När vår Java Stream transformerar data till komplexa strukturer, som när `.collect(Collectors.groupingBy(...))` skapar en `Map<String, Double>` för medelpriser, konverterar Spring Boots inbyggda Jackson-modul detta direkt till ett snyggt JSON-objekt till webbläsaren utan att vi behöver skriva någon extra kod.
*   **Trådsäkerhet i flertrådad miljö:** Spring Boot körs på en flertrådad Tomcat-server där varje HTTP-anrop körs i en egen tråd. Eftersom vi har implementerat en trådsäker `CopyOnWriteArrayList` kan våra Java Streams säkert läsa, filtrera och sortera datan samtidigt, även om flera användare anropar vårt API exakt samtidigt.

---

## 2. Branschanalys & Språkjämförelse: Java Streams vs. Node.js (Array-metoder)

Vi jämför här hur **Java/Spring Boot** hanterar databehandling i minnet jämfört med **Node.js (JavaScript)**, vilket är en annan mycket populär miljö för API-utveckling.

### Vad som är SAMMA:
*   **Funktionell programmeringsstil:** Både Java Streams (med `.filter()`, `.sorted()`, `.map()`) och JavaScript i Node.js (med `.filter()`, `.sort()`, `.map()`) använder en deklarativ stil. Man beskriver *vad* man vill uppnå med datan snarare än *hur* loopen ska stegas igenom (imperativ kod).
*   **Icke-destruktiva operationer:** Varken Java Streams eller JavaScripts inbyggda array-metoder ändrar på ursprungslistan (`productList`) under filtreringen eller sorteringen, utan de skapar nya representationer av datan.

### Vad som SKILJER:

| Egenskap | Java / Spring Boot (Streams API) | Node.js (JavaScript Array Methods) |
| :--- | :--- | :--- |
| **Exekveringsmodell** | **Lazy Evaluation (Lat utvärdering).** En Java Stream gör ingenting förrän en *terminal operation* (som `.collect()` eller `.sum()`) anropas. Detta gör den extremt minneseffektiv vid stora datamängder. | **Eager Evaluation (Irig utvärdering).** Varje metod i Node.js (t.ex. en `.filter().map()`) skapar en helt ny tillfällig array i minnet direkt efter varje steg, vilket drar mer RAM-minne. |
| **Trådsäkerhet & Multithreading** | **Flertrådad databearbetning.** Eftersom Spring Boot hanterar trådar kan vi med ett enkelt metodanrop byta till `.parallelStream()` för att fördela tunga analysberäkningar (som lagervärde och medelpriser) över datorns alla CPU-kärnor automatiskt. | **Enkeltrådad (Single-threaded).** All databearbetning och sortering sker på en enda tråd. Om en array är gigantisk och sorteringen tar tid, fryser hela Node.js-servern för alla andra användare under tiden. |
| **Typsäkerhet vid transformation** | **Statiskt typat.** Kompilatorn säkerställer att vi inte råkar räkna ut medelpris på ett textfält eller multiplicera fel datatyper i vår Stream. Fel upptäcks innan koden körs. | **Dynamiskt typat.** Det är lättare att råka introducera buggar (t.ex. att ett pris behandlas som textsträngen `"3500"` istället för siffran `3500`), vilket kan leda till att beräkningar blir `NaN` under körning. |

### Slutsats
För ett lagerhanteringssystem där datakvalitet, trådsäkerhet och tunga aggregeringar är centralt är **Java och Spring Boot Streams API** det starkare valet då det erbjuder överlägsen minneshantering (lazy evaluation) och inbyggd trådsäkerhet. **Node.js** är smidigt för snabb utveckling, men kräver mycket mer försiktighet vid tunga matematiska beräkningar för att inte blockera servern.

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


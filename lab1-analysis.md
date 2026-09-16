# Lab 1 Analysis — WalkMates


**Deadline:** Friday 18 September 2026, 23:59  
**Pair:** Marjan Motafeghi (mamo2414), Yasaman Vallaee (yava2400)


---


## Part A — Fundamentals (M1)


### Activity 1.1 — Quality-attribute analysis (ISO/IEC 25010)


We selected the following three features:


1. **FR-1.3 Wallet**
2. **FR-4.4 Booking creation**
3. **FR-5.1 Match explanation**


#### FR-1.3 Wallet


We chose **Functional suitability** and **Security**.


Functional suitability is important because the wallet must correctly follow the rules for top-ups, balance limits, and money calculations. Security is important because the wallet contains financial data, and the balance should be protected from incorrect or unauthorized changes.


#### FR-4.4 Booking creation


We chose **Functional suitability** and **Reliability**.


Functional suitability is important because the system must correctly check all five booking conditions and decide whether a booking should be accepted or rejected. Reliability is important because the booking system should work correctly and consistently when checking these conditions.


#### FR-5.1 Match explanation


We chose **Interaction capability** and **Functional suitability**.


Interaction capability is important because the AI explanation should be short and easy for the user to understand. Functional suitability is important because the explanation should perform its intended function and explain why a listing is suitable for the seeker.


#### Testable quality requirement


For the testable quality requirement, we chose **FR-1.3 Wallet**:


> The system shall reject any wallet top-up below 10.00 SEK.


This requirement is testable because we can check whether the system accepts or rejects a top-up according to the specified minimum limit.


---


### Activity 1.2 — Bug analysis (error → fault → failure)


We first read the failure report. It states that a **NEW seeker** who already had one active booking was able to create a second booking.


According to **FR-4.4 Rule 2**, the number of active bookings must be below the maximum allowed for the seeker's trust tier. According to **FR-1.2**, a NEW seeker can have a maximum of one active booking. Therefore, the second booking should be rejected.


We then inspected the code responsible for creating bookings in `BookingService.createBooking()`. The relevant condition is:


```java
if (seekerActive > seeker.getMaxConcurrentBookings())
```


For a NEW seeker with one active booking, `seekerActive` is 1 and the maximum is also 1. The condition evaluates `1 > 1`, which is false, so the second booking is not rejected.


- **Human error:** The developer likely misunderstood the booking-limit condition or selected the wrong comparison operator.
- **Fault:** The booking-limit check uses `>` even when the seeker has already reached the maximum.
- **Failure:** A NEW seeker can create a second active booking even though only one active booking is allowed.


A **unit test** of `BookingService` using **Boundary Value Analysis (BVA)** should have caught this fault because it occurs at the maximum booking limit. For a NEW seeker, the relevant boundary is one active booking: with zero active bookings a new booking can be accepted, while with one active booking another booking must be rejected.


---


### Activity 1.3 — Your first test


We ran `BeginnerFirstTest` successfully and learned how a test is structured using **Arrange, Act, and Assert**. We also saw how tests connect to requirements, for example by checking the initial trust tier, wallet balance, and wallet top-up rules.


We then added and successfully ran our own test for adding **250 SEK** to a new seeker's wallet.


---


## Part B — Specification-based Testing (M2)


### Activity 2.1 — Equivalence Partitioning (EP)


We derived equivalence classes from **FR-1.1 Registration** and **FR-1.3 Wallet** and selected one representative value for each class.


| Input | Equivalence class | Representative | Expected outcome |
| --- | --- | --- | --- |
| Email | Valid format and length | `sam@example.com` | Accepted |
| Email | Missing `@` | `samexample.com` | Rejected |
| Email | Empty local part | `@example.com` | Rejected |
| Email | Domain without `.` | `sam@example` | Rejected |
| Email | More than 254 characters | >254-character email | Rejected |
| Display name | Valid | `Sam Lee` | Accepted |
| Display name | Too short | `A` | Rejected |
| Display name | Too long | 41 characters | Rejected |
| Display name | Invalid characters | `Sam123` | Rejected |
| Phone | Valid Swedish format | `0701234567` | Accepted |
| Phone | Valid international format | `+4671234567` | Accepted |
| Phone | Invalid format | `1234567890` | Rejected |
| Wallet top-up | Valid amount | `250.00` | Accepted |
| Wallet top-up | Below minimum | `5.00` | Rejected |
| Wallet top-up | Above maximum | `6000.00` | Rejected |


The valid wallet representative of **250.00 SEK** is also used in Activity 1.3.


---


### Activity 2.2 — Boundary Value Analysis (BVA)


For **FR-1.3**, we selected values immediately below, at, and immediately above the specified wallet boundaries.


| Boundary | Test value / setup | Position | Expected outcome |
| --- | --- | --- | --- |
| Minimum top-up 10.00 | `9.99` | Just below | Rejected |
| Minimum top-up 10.00 | `10.00` | At boundary | Accepted |
| Minimum top-up 10.00 | `10.01` | Just above | Accepted |
| Maximum single top-up 5000.00 | `4999.99` | Just below | Accepted |
| Maximum single top-up 5000.00 | `5000.00` | At boundary | Accepted |
| Maximum single top-up 5000.00 | `5000.01` | Just above | Rejected |
| Maximum balance 20000.00 | Balance 19500.00 + 499.99 → 19999.99 | Just below | Accepted |
| Maximum balance 20000.00 | Balance 19500.00 + 500.00 → 20000.00 | At boundary | Accepted |
| Maximum balance 20000.00 | Balance 19500.00 + 500.01 → 20000.01 | Just above | Rejected |


For the maximum-balance cases, the starting balance of **19,500.00 SEK** is built using valid individual top-ups. This isolates the 20,000 SEK resulting-balance boundary without violating the **5,000 SEK maximum single top-up** rule.


---


### Activity 2.3 — Decision table (trust tier → limits)


Based on **FR-1.2**, each trust tier determines both the maximum number of concurrent active bookings and the platform fee.


| Trust tier | Max concurrent active bookings | Platform fee |
| --- | ---: | ---: |
| `NEW` | 1 | 15% |
| `VERIFIED` | 3 | 12% |
| `TRUSTED` | 5 | 8% |
| `PRO_SITTER` | 10 | 5% |


The decision-table tests check that each trust tier maps to the expected maximum concurrent-booking limit and platform fee.

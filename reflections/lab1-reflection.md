# Lab Reflection — WalkMates

**Lab:** 1
**Pair:** Marjan Motafeghi (mamo2414), Yasaman Vallaee (yava2400)
**Repo:** [yasva/walkmates-yava2400-mamo2414](https://github.com/yasva/walkmates-yava2400-mamo2414)
**Repo commit/tag:** [Commits · yasva/walkmates-yava2400-mamo2414](https://github.com/yasva/walkmates-yava2400-mamo2414/commits/main/)

---

### 1. What we did

#### Part A — Fundamentals (M1)

**Activity 1.1 — Quality-attribute analysis (ISO/IEC 25010)**
We selected three features: **FR-1.3 Wallet, FR-4.4 Booking creation, and FR-5.1 Match explanation**. For FR-1.3 Wallet, we chose **Functional suitability and Security** because the wallet must correctly follow the rules for top-ups, balance limits, and money calculations, and the balance should be protected from incorrect or unauthorized changes. For FR-4.4 Booking creation, we chose **Functional suitability and Reliability** because the system must correctly check the booking conditions and work correctly and consistently. For FR-5.1 Match explanation, we chose **Interaction capability and Functional suitability** because the explanation should be easy for the user to understand and should explain why a listing is suitable for the seeker.

For the testable quality requirement, we chose **FR-1.3 Wallet**. We specified that the system should reject any top-up amount below the minimum allowed amount.

**Activity 1.2 — Bug analysis (error → fault → failure)**
We analyzed the failure where a **NEW seeker who already had one active booking was able to create a second one**. We identified the likely human error, the fault in the code, and the failure. The likely human error was a misunderstanding of the booking limit or using the wrong comparison operator. The fault was in the **FR-4.4 Rule 2** check, where `>` was used for the booking limit. The failure was that a NEW seeker could have two active bookings even though only one is allowed.

We decided that a **unit test** using **Boundary Value Analysis (BVA)** should have caught this fault because the problem happens at the maximum booking limit.

**Activity 1.3 — Your first test**
We completed the **FIRST_TEST_TUTORIAL** and successfully ran `BeginnerFirstTest`. We learned how a test is structured using **Arrange, Act, and Assert** and how tests connect to requirements. We then added and successfully ran our own test for adding **250 SEK** to a new seeker's wallet.

#### Part B — Specification-based Testing (M2)

For the specification-based testing part, we designed and implemented tests for the `Seeker` class in `SeekerSpecBasedTest`.

**Activity 2.1 — Equivalence Partitioning (EP)**
We used **Equivalence Partitioning** to design representative valid and invalid test cases for registration fields in **FR-1.1**, including email, display name, and phone number. We also applied EP to wallet top-ups in **FR-1.3**, selecting representatives for valid top-ups and values below or above the allowed range.

**Activity 2.2 — Boundary Value Analysis (BVA)**
We used **Boundary Value Analysis** for the wallet limits in **FR-1.3**. We tested values just below, exactly at, and just above the **10.00 SEK minimum top-up** and **5,000.00 SEK maximum single top-up**. For the **20,000.00 SEK maximum resulting balance**, we first built a valid balance of 19,500 SEK and then tested additions of 499.99, 500.00, and 500.01.

**Activity 2.3 — Decision table**
We created a decision table based on **FR-1.2** that maps the four trust tiers (`NEW`, `VERIFIED`, `TRUSTED`, and `PRO_SITTER`) to their maximum concurrent active bookings and platform fees. We wrote separate tests for each row of the table and checked `getMaxConcurrentBookings()` and the platform fee for each trust tier.

---

### 2. What we found

The booking failure in Activity 1.2 showed us how a small error in a comparison operator can cause a failure at a boundary. The code used `>` when checking the number of active bookings, which allowed a NEW seeker who had already reached the limit of one active booking to create another booking.

From the wallet tests, we learned why it is important to test values around the boundaries. Instead of testing only normal valid and invalid values, BVA made us check values immediately below, at, and above the specified limits.

During the phone-number EP testing, we also found an apparent inconsistency between the literal international format in **FR-1.1** (`+467XXXXXXXX`) and the validation pattern provided in `Seeker` (`\\+467\d{7}`). This became visible when an initially suggested representative value was rejected by the implementation.

---

### 3. AI use (be honest — it doesn't lower your grade)

We used AI to better understand the ISO/IEC 25010 quality characteristics, the lab instructions, and the EP, BVA, and decision-table techniques. AI suggested possible quality characteristics, representative test values, and examples of JUnit tests. We compared these suggestions with `docs/REQUIREMENTS.md`, our test-design tables, and the provided project code before deciding what to keep.

For the 20,000 SEK maximum wallet balance, AI helped us discuss different boundary values. We decided to first build a valid balance of 19,500 SEK and then test 499.99, 500.00, and 500.01. We chose these values because we wanted to test the 20,000 SEK balance limit without breaking the 5,000 SEK single top-up rule.

For the trust-tier decision table, AI suggested using a parameterized test. We chose separate tests for `NEW`, `VERIFIED`, `TRUSTED`, and `PRO_SITTER` because this was simpler for us and made the relationship between each decision-table row and its test clear.

AI initially suggested `+46701234567` as a representative valid international phone number based on the literal `+467XXXXXXXX` notation in FR-1.1. The test failed with `IllegalArgumentException`. Instead of changing the production code or accepting the AI suggestion, we inspected the `Seeker` implementation and compared it with FR-1.1 and our original test-design table. The implementation uses `\\+467\d{7}`, and our original representative `+4671234567` matched that validation pattern. We therefore kept our original representative value. From this, we learned that we should not use test values suggested by AI without checking them against the requirements and the code.

---

### 4. Judgment

We had to decide how to divide inputs into meaningful equivalence classes and which values should represent each class. We also had to check that the values we chose for one test did not break another requirement.

For Activity 1.1, we decided which quality characteristics were most relevant to each feature. For Activity 1.2, we decided that BVA and a unit test were suitable because the observed fault occurs at the booking-limit boundary. For Activity 2.3, we chose separate tests for all four trust tiers so that each decision-table row was clearly represented.

We also had to decide how to handle the international phone-number test when the literal notation in FR-1.1 and the validation pattern in `Seeker` appeared inconsistent. We inspected both rather than changing production code simply to make the test pass, and kept the representative value from our original test-design table that matched the provided implementation.

---

### 5. What we'd test next

If we had another hour, we would add more boundary tests for the registration fields in **FR-1.1**, especially the exact email and display-name length limits. We would also test more combinations of booking conditions in **FR-4.4**, since the failure analyzed in Activity 1.2 showed that booking-limit boundaries are an important risk area.

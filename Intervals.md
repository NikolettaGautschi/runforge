/**
 * TRAINING_SET_EXTENSION.md — Interval Comprehensive Documentation
 *
 * Complete architecture and API documentation for the extended Interval module.
 * Added as part of the RunForge project enhancement (2026-06-07).
 */

# Interval Extension — Complete Architecture

## Overview

Interval represents individual training sessions/workout segments that belong to a training plan.
Each set specifies an exercise with a type (distance or duration), quantity, and unit of measurement.
Sets are ordered sequentially within a plan and support full CRUD operations.

## Backend Architecture

### 1. Model: `Interval.java`
**Location**: `backend/src/main/java/com/runforge/model/Interval.java`

**Key Features**:
- Discriminated union design with two enum types:
  - `Type`: DISTANCE | DURATION
  - `Unit`: KM | M | MIN | SEC
- Validates unit/type compatibility via `Unit.isCompatibleWith(Type)`
- Tracks sequence order within training plan
- Lazy-loaded relationship to RunningPlan (foreign key: training_id)

**Fields**:
```java
Long id                    // Auto-generated PK
Type type                  // DISTANCE or DURATION (required)
Unit unit                  // KM/M or MIN/SEC (required, validated against type)
Integer order              // Sequence within plan (1-based)
Double quantity            // Positive quantity value
LocalDate createdDate      // Timestamp (defaults to now)
RunningPlan training      // Many-to-One FK (required)
```

**Database Table**: `training_set`
```sql
CREATE TABLE training_set (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  type VARCHAR(10) NOT NULL,           -- DISTANCE or DURATION
  unit VARCHAR(10) NOT NULL,           -- KM, M, MIN, or SEC
  "order" INT NOT NULL,                 -- Sequence 1, 2, 3...
  quantity DOUBLE NOT NULL,
  created_date DATE DEFAULT CURRENT_DATE,
  training_id BIGINT NOT NULL FK,
  CHECK (quantity > 0),
  UNIQUE (training_id, "order")
);
```

### 2. Repository: `IntervalRepository.java`
**Location**: `backend/src/main/java/com/runforge/repository/IntervalRepository.java`

**Methods**:
```java
// Query training sets by plan (ordered by sequence)
findByTrainingIdOrderByOrder(Long trainingId) -> List<Interval>

// Filter by type (DISTANCE/DURATION)
findByTrainingIdAndType(Long trainingId, Type type) -> List<Interval>

// Count sets in plan
countByTrainingId(Long trainingId) -> long

// Get max order for auto-increment
findMaxOrderByTrainingId(Long trainingId) -> Integer
```

**Extends**: `JpaRepository<Interval, Long>`

### 3. Service: `IntervalService.java`
**Location**: `backend/src/main/java/com/runforge/service/IntervalService.java`

**Key Responsibilities**:
- CRUD operations with validation
- Auto-assign sequence order on creation
- Unit/type compatibility validation
- Reordering support for drag-and-drop UI

**Methods**:
```java
// Retrieval
findByRunningPlan(Long trainingId) -> List<Interval>
findById(Long id) -> Interval
findByRunningPlanAndType(Long trainingId, Type type) -> List<Interval>
countByRunningPlan(Long trainingId) -> long

// Mutation (with validation)
create(Long trainingId, Interval set) -> Interval
  - Validates parent plan exists
  - Validates unit/type compatibility
  - Auto-assigns order if not provided
  
update(Long id, Interval updated) -> Interval
  - Validates compatibility on type/unit changes
  
delete(Long id) -> void

// Utility
reorder(Long trainingId, List<Long> setIds) -> void
  - Reassigns order values to match provided sequence
  - Validates all sets belong to same plan
```

### 4. Controller: `IntervalController.java`
**Location**: `backend/src/main/java/com/runforge/controller/IntervalController.java`

**Endpoints**:

#### GET Endpoints
```
GET /api/plans/{planId}/sets
  → List all sets for a plan (ordered by sequence)
  Response: 200 OK with Interval[]

GET /api/plans/{planId}/sets/type/{type}
  → Filter by type (DISTANCE or DURATION)
  Response: 200 OK with filtered Interval[]

GET /api/sets/{id}
  → Get single set
  Response: 200 OK | 404 Not Found
```

#### POST Endpoints
```
POST /api/plans/{planId}/sets
  Request: Interval dto (type, unit, quantity required)
  → Create new set with auto-ordered sequence
  Response: 201 CREATED with Interval (ID + order assigned)

POST /api/plans/{planId}/sets/reorder
  Request: ReorderRequest { setIds: number[] }
  → Reorder sets to match provided ID sequence
  Response: 200 OK
```

#### PUT Endpoints
```
PUT /api/sets/{id}
  Request: Partial Interval
  → Update existing set (all fields updatable)
  Response: 200 OK with updated Interval
```

#### DELETE Endpoints
```
DELETE /api/sets/{id}
  → Delete training set from plan
  Response: 204 No Content
```

**CORS Configuration**: Enabled for http://localhost:3000

---

## Frontend Architecture

### 1. Types: `types/index.ts`
**Location**: `frontend/src/types/index.ts`

**Key Types** (fully aligned with backend):

```typescript
// Discriminated union types for type safety

type IntervalType = 'DISTANCE' | 'DURATION';
type IntervalUnit = 'KM' | 'M' | 'MIN' | 'SEC';

interface DistanceBasedSet {
  id?: number;
  type: 'DISTANCE';
  unit: 'KM' | 'M';      // Type-safe: only distance units
  order: number;
  quantity: number;
  createdDate?: string;
}

interface DurationBasedSet {
  id?: number;
  type: 'DURATION';
  unit: 'MIN' | 'SEC';   // Type-safe: only duration units
  order: number;
  quantity: number;
  createdDate?: string;
}

type Interval = DistanceBasedSet | DurationBasedSet;
```

**Helper Functions**:
```typescript
isDistanceBased(set: Interval): set is DistanceBasedSet
isDurationBased(set: Interval): set is DurationBasedSet
formatInterval(set: Interval): string  // "5 Kilometer", "30 Minuten"
```

**UI Constants**:
```typescript
TRAINING_SET_TYPE_LABELS: {
  DISTANCE: 'Entfernung',
  DURATION: 'Dauer'
}

TRAINING_SET_UNIT_LABELS: {
  KM: 'Kilometer',
  M: 'Meter',
  MIN: 'Minuten',
  SEC: 'Sekunden'
}
```

### 2. API Client: `services/api.ts`
**Location**: `frontend/src/services/api.ts`

**IntervalApi Methods**:
```typescript
getByPlan(planId: number)
  → Fetch all sets for a plan, ordered by sequence

getById(id: number)
  → Fetch single set

create(planId: number, data: any)
  → Create new set with auto-ordered sequence

update(id: number, data: Partial<any>)
  → Update existing set

delete(id: number)
  → Delete set

reorder(planId: number, setIds: number[])
  → Reorder sets via drag-and-drop
```

**Example Usage**:
```typescript
import { IntervalApi } from '@/services/api';

// Fetch all sets for a plan
const sets = await IntervalApi.getByPlan(1);

// Create distance-based set
const distanceSet = await IntervalApi.create(1, {
  type: 'DISTANCE',
  unit: 'KM',
  quantity: 5,
  order: 1
});

// Create duration-based set
const durationSet = await IntervalApi.create(1, {
  type: 'DURATION',
  unit: 'MIN',
  quantity: 30,
  order: 2
});

// Reorder sets
await IntervalApi.reorder(1, [3, 1, 2]);
```

---

## Usage Examples

### Backend: Creating a Training Set

```java
// Service call
Interval set = IntervalService.create(planId, new Interval() {{
  setType(Interval.Type.DISTANCE);
  setUnit(Interval.Unit.KM);
  setQuantity(5.0);
  // Order auto-assigned: 1, 2, 3...
}});

// Result: Interval with id=1, order=1, createdDate=2026-06-07
```

### Backend: Reordering Sets

```java
// Reorder sets to new sequence [3, 1, 2]
List<Long> newOrder = Arrays.asList(3L, 1L, 2L);
IntervalService.reorder(planId, newOrder);

// Result: sets now have order values [1, 2, 3] in that ID sequence
```

### Frontend: Display Training Sets in Modal

```typescript
const { data: sets } = useQuery({
  queryKey: ['sets', planId],
  queryFn: () => IntervalApi.getByPlan(planId)
});

return (
  <div>
    {sets?.map(set => (
      <div key={set.id}>
        <span>{set.order}</span>
        <span>{formatInterval(set)}</span>
        {/* "5 Kilometer" or "30 Minuten" */}
      </div>
    ))}
  </div>
);
```

### Frontend: Drag-and-Drop Reordering

```typescript
const handleReorder = async (newOrder: Interval[]) => {
  const setIds = newOrder.map(s => s.id!);
  await IntervalApi.reorder(planId, setIds);
  queryClient.invalidateQueries({ queryKey: ['sets', planId] });
};
```

---

## Validation Rules

### Unit/Type Compatibility

Must enforce at both backend and frontend:

| Type     | Valid Units | Invalid Units |
|----------|------------|---------------|
| DISTANCE | KM, M      | MIN, SEC      |
| DURATION | MIN, SEC   | KM, M         |

**Backend Validation**:
```java
if (!unit.isCompatibleWith(type)) {
  throw new RuntimeException("Unit not compatible with type");
}
```

**Frontend Validation** (via TypeScript types):
```typescript
// Type-safe: compiler prevents invalid combinations
const set: DistanceBasedSet = {
  type: 'DISTANCE',
  unit: 'KM'     // ✓ Valid
};

const set: DistanceBasedSet = {
  type: 'DISTANCE',
  unit: 'MIN'    // ✗ Compile error
};
```

### Quantity Validation

- Must be positive (> 0)
- Enforced via `@Positive` annotation on backend
- Should be validated in frontend forms

### Sequence Ordering

- Order values must be unique within a plan
- Auto-calculated as MAX(order) + 1 on creation
- Can be manually reassigned via reorder endpoint

---

## Database Considerations

### Cascade Behavior
- When a RunningPlan is deleted, all associated Interval records are deleted
- Configured via `@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)`

### Lazy Loading
- RunningPlan reference is lazy-loaded to avoid N+1 queries
- Join column: `training_id` (NOT NULL, FK))

### Index Recommendations
```sql
-- For fast lookups by plan
CREATE INDEX idx_training_set_training_id ON training_set(training_id);

-- For unique ordering within plan
CREATE UNIQUE INDEX idx_training_plan_order ON training_set(training_id, "order");
```

---

## Testing Checklist

### Backend Unit Tests
- [ ] Unit/type compatibility validation
- [ ] Auto-order assignment on create
- [ ] Reordering logic with ID mapping
- [ ] Cascade delete when plan deleted
- [ ] Find by type filters

### API Integration Tests
- [ ] POST /api/plans/{id}/sets — create with auto-order
- [ ] GET /api/plans/{id}/sets — retrieve ordered by sequence
- [ ] GET /api/plans/{id}/sets/type/DISTANCE — type filtering
- [ ] PUT /api/sets/{id} — update with validation
- [ ] DELETE /api/sets/{id} — delete single set
- [ ] POST /api/plans/{id}/sets/reorder — reorder by ID list

### Frontend Tests
- [ ] Type guard functions (isDistanceBased, isDurationBased)
- [ ] Format function (formatInterval)
- [ ] API client error handling
- [ ] TanStack Query cache invalidation on mutation

### E2E Tests
- [ ] Create plan → add distance set → add duration set
- [ ] Edit set quantity
- [ ] Reorder sets via drag-and-drop
- [ ] Delete set and verify order resequence
- [ ] Invalid unit/type combination rejection

---

## Future Enhancements

1. **UI Components**
   - Interval editor modal (create/edit)
   - Drag-and-drop reordering component
   - Set list display with order indicators

2. **Advanced Features**
   - Bulk import from CSV/Excel
   - Set templates (save/reuse common sets)
   - Workout execution tracking (actual vs. planned)
   - Statistics on set difficulty distribution

3. **Performance**
   - Cache training sets on frontend
   - Batch reorder with single DB transaction
   - Pagination for large training plans

4. **Integration**
   - Assign training sets to specific members
   - Track member progress through sets
   - Generate difficulty scores per set

---

**Document Version**: 1.0
**Last Updated**: 2026-06-07
**Framework**: Spring Boot 3.2 (Backend), React 18 + TypeScript (Frontend)


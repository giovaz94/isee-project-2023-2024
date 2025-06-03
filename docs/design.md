# Evolutionary Agent-based Aggression Simulation - EvASim

## Goals of the project

> The goal of the project is to simulate an environment using a BDI Agent framework in which we have agents simulating two types of creatures, doves and hawks, that compete for survival based on their behaviors, observing how the evolution of the species unfolds.

## Requirements Analysis

- The simulation is composed of a sequence of days / rounds;
- Food is spawned casually inside the map;
- Food pieces come in pairs and each one of them can be further split in half.
- Survival and reproduction rules:
  - eating one piece of food lets a creature survive to the next day and eating two piece of food allows a blob to both survive and reproduce;
  - a creature of one kind always reproduce itself (if they’re able to do it) in another creature of the same kind
  - each creature continues to search for food until they have reached the capacity to reproduce themselves. If the day is over or they have eaten enough food to reproduce they return home.
- Contention rules:
  - if a single entity tries to pick up a pair of food it will succeed if no other entities are also trying to take the same pair.
  - if another entity is trying to take the same pair of food the following scenarios applies, depending on the other creature:
    - If both are doves they share the food, each taking a piece of food;
    - If one is a dove and one is a hawk, then the hawk shares half of a food piece with the dove and then it immediately steals the other before the dove can take it;
    - If both are hawks then they fight over the food both gaining a piece of it. But, since fighting requires energy they consume all the benefits from eating the food, acquiring 0 food.
  - If two entities are competing over a pair of food and a third tries to join then the latter notices the other two and gives up taking that food.
- Movements:
  - Creatures explore the map using random movements.
  - They have a limited sight of the environment that they’re exploring. If they find a piece of food they proceed to move towards it.


## Design

```mermaid
classDiagram
    direction TB

    class Simulation {
        <<interface>>
        +start() Job
        +stop()
        +pause()
        +resume()
    }

    class Round {
        <<interface>>
    }
    Simulation *--> "*" Round

    class SpawnZone {
        <<interface>>
        +shape: Shape
    }
    SpawnZone *--> "1" Shape

    class World {
        <<interface>>
        +shape: Shape
        +entities: Sequence~Entity~
        +foods: Sequence~Food~
        +blobs: Sequence~Blob~
        +remove(Food food)
        +visibleEntities(e: Entity) Sequence~Entity~
    }
    Round *--> "1" World
    World *--> "*" Entity
    World *--> "*" SpawnZone

    class Shape {
        <<interface>>
        +contains(p2D: Position2D) Boolean
        +intersect(s: Shape) Boolean
    }
    class Circle
    Shape <|-- Circle
    class Rectangle
    Shape <|-- Rectangle
    World *--> "*" Shape

    class CollisionDetector {
        <<interface>>
        +typealias Collision = Pair~Entity, Entity~
        +detectCollisions(w: World) Set~Collision~
        +check(e1: Entity, e2: Entity) Boolean
    }
    World *--> "1" CollisionDetector
    
    class Entity {
        <<interface>>
        +position: Position2D
        +shape: Shape
    }
    Entity *--> "*" Shape
    
    class EntityId {
        +value: String
    }
    Entity *--> EntityId

    class Food {
        <<interface>>
        -acquiredBy: Set~Blob~
        +energy: Int
        +attemptToGet(Blob b) Boolean
    }
    Entity <|-- Food
    Food *--> "0..2" Blob
    
%%    class Piece {
%%        <<interface>>
%%    }
%%    Food *--> "2" Piece
    
    class Blob {
        <<interface>>
        +initialPosition: Position2D
        +velocity: Vector2D
        +health: Health
        +sight: Sight
        +personality: Personality
        +applyForce(v2D: Vector2D)
        +update(Δt: Duration)
        +canReproduce() Boolean
        +isDead() Boolean
        +isAlive() Boolean
    }
    Entity <|-- Blob

    class Personality {
        <<interface>>
    }
    Blob *--> Personality
    class Hawk
    Personality <|-- Hawk
    class Dove 
    Personality <|-- Dove
    
    class Health {
        +health: Int
        +increase(quantity: Double)
        +decrease(quantity: Double)
    }
    Blob *--> Health
    
    class Sight {
        <<interface>>
        +visibilityArea: Shape
    }
    Blob *--> Sight
    
%%    class Position2D {
%%        <<interface>>
%%        +x: Double
%%        +y: Double
%%    }
%%    Entity *--> Position2D
%%    
%%    class Vector2D {
%%        <<interface>>
%%    }
%%    Blob *--> Vector2D
```

### Agents

<!--

#### Goals

- `+!find_food`: reazione dell'agente: in futuro può essere programmato un comportamento più intelligente
del semplice continuare a muoversi verso la direzione prestabilita iniziale per cercare di "spottare" il cibo

#### Belief

+ `food` => viene aggiunto alla belief base quando il food è nel sight dell'agente
+ `reached_food` => viene aggiunto quando l'agente ha raggiunto il food

#### Actions

- `move_towards(X, Y)`: agents move towards the given coordinates (X, Y)
- `collect_food(Food_ID)`: agents collect the given food

-->

```jason
direction(0, 0).
speed(0).
status(exploring).

!round.

+!round <-
  !find_food;
  !collect_food;
  !contention;
  !back_home.

+!find_food : status(exploring) <-
  !change_direction;
  random(N, 1, 20); // N is the number of steps to take following the direction
  !move_on(N);
  !find_food.

+!find_food : status(targeting(F)) & position(PosX, PosY) <-
  waypoint_direction(PosX, PosY, F, DirX, DirY);
  -+direction(DirX, DirY);
  !move.
  !find_food.

+!find_food : status(reached(F)) <- true.

+!move_on(0) <- true.

+!move_on(N) : N > 0 & status(exploring) <-
  !move;
  !move_on(N - 1).

+!move_on(N) : N > 0 & (obstacle(X, Y) | not(status(exploring))) <- true.

// TODO: obstacle avoidance. How to deal with multiple obstacles?
+!change_direction : not(obstacle)) <-
  random(X, -1, 1);
  random(Y, -1, 1);
  -+direction(X, Y).

+!change_direction : obstacle() & direction(X, Y) <-
  // TODO...
  -+direction(-X, -Y); // Reverse direction when an obstacle is detected

+!move : direction(X, Y) & speed(V) <-
  !update_position(X, Y, V); // External action

+!collect_food : food(F) <-
  collect_food(F, IsCollected); // External action
  if (IsCollected) {
    -+status(contending(F));
    !contention;
  } else {
    -+status(exploring);
    !find_food;
  }

+target_food(F) <- // Belief added from the environment
  status(targeting(F)).

+reached_food(F) : status(targeting(F)) <- // Belief added from the environment
  status(reached(F)).

!contention : status(contending(F)) <-
  ...
```

## Salient implementation details

## Deployment instructions

## Usage examples

## Conclusions

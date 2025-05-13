package io.github.evasim.model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

class SightTest : FreeSpec({
    "Blobs with circular sight" - {
        val circularSight = Circle(radius = 5.0)

        "should see each other if they are in their sight" {
            val blob1 = blob(circularSight, origin)
            val blob2 = blob(circularSight, Position2D(3.0, 2.0))
            (blob1 in blob2.sight) shouldBe true
            (blob2 in blob1.sight) shouldBe true
        }

        "should not see each other if they are outside their sight" {
            val blob1 = blob(circularSight, origin)
            val blob2 = blob(circularSight, Position2D(5.0, 4.0))
            (blob1 in blob2.sight) shouldBe false
            (blob2 in blob1.sight) shouldBe false
        }

        "should be able to see foods in their sight" {
            val food = food(position = origin)
            val blob = blob(circularSight, Position2D(0.0, 4.0))
            (food in blob.sight) shouldBe true
            blob.update(elapsedTime = 5.seconds)
            blob.position shouldBe Position2D(5.0, 4.0)
            (food in blob.sight) shouldBe false
        }
    }

    "Blobs with conical Sight" - {
        val conicalSight = Cone(radius = 5.0, fovDegrees = Degrees(90.0))

        "should see each other if they are in their sight" {
            val blob1 = blob(conicalSight, position = origin, velocity = Vector2D(2.0, 0.0)) // facing right
            val blob2 = blob(
                sightShape = conicalSight,
                position = Position2D(3.0, 2.0),
                velocity = Vector2D(-1.0, 0.0),
            ) // facing left
            (blob1 in blob2.sight) shouldBe true
            (blob2 in blob1.sight) shouldBe true
        }

        "should not see each other if they are outside their sight" {
            val blob1 = blob(conicalSight, origin, velocity = Vector2D(2.0, 0.0)) // facing right
            val blob2 = blob(
                sightShape = conicalSight,
                position = Position2D(5.0, 4.0),
                velocity = Vector2D(-1.0, 0.0),
            ) // facing left
            (blob1 in blob2.sight) shouldBe false
            (blob2 in blob1.sight) shouldBe false
        }

        "should be able to see foods in their sight" {
            val food = food(position = origin)
            val blob = blob(conicalSight, Position2D(0.0, 4.0), velocity = Vector2D(1.0, 0.0)) // facing right
            (food in blob.sight) shouldBe false
            blob.updateVelocity(Vector2D(0.0, -1.0))
            blob.sight.contains(food) shouldBe true
        }
    }
}) {
    companion object {
        fun blob(sightShape: Shape, position: Position2D, velocity: Vector2D = Vector2D(1.0, 0.0)) =
            // NOTE: id is not unique, but it's ok for the test
            Blob(Entity.Id("blob"), Dove, position, Circle(1.0), velocity, Direction.DOWN, sightShape)

        fun food(position: Position2D): Food = Food.of(shape = Circle(radius = 1.0), position = position, pieces = 2)
    }
}

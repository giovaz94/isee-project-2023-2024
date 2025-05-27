package io.github.evasim.model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ShapesTest : FreeSpec({

    "Point inside circle should be correctly identified" {
        val circle = Circle(radius = 5.0)
        circle.locallyContains(origin) shouldBe true
        circle.locallyContains(Position2D(3.0, 4.0)) shouldBe true
        circle.locallyContains(Position2D(5.0, 0.0)) shouldBe true
        circle.locallyContains(Position2D(6.0, 0.0)) shouldBe false
        circle.locallyContains(Position2D(4.0, 4.0)) shouldBe false
    }

    "Rectangle should contain points correctly" {
        val rectangle = Rectangle(width = 5.0, height = 5.0)
        rectangle.locallyContains(origin) shouldBe true
        rectangle.locallyContains(Position2D(1.0, 1.0)) shouldBe true
        rectangle.locallyContains(Position2D(2.5, 2.5)) shouldBe true
        rectangle.locallyContains(Position2D(4.0, 0.0)) shouldBe false
        rectangle.locallyContains(Position2D(6.0, 6.0)) shouldBe false
    }

    "Overlapping circles should intersect" {
        val circle1 = Placed(Circle(radius = 5.0), origin)
        val circle2 = Placed(Circle(radius = 5.0), Position2D(3.0, 4.0))
        circle1.circleIntersect(circle2) shouldBe true
    }

    "Adjacent circles should intersect" {
        val circle1 = Placed(Circle(radius = 5.0), Position2D(0.0, 0.0))
        val circle2 = Placed(Circle(radius = 5.0), Position2D(0.0, 0.0))
        circle1.circleIntersect(circle2) shouldBe true
    }

    "Non-overlapping circles should not intersect" {
        val circle1 = Placed(Circle(radius = 5.0), Position2D(0.0, 0.0))
        val circle2 = Placed(Circle(radius = 5.0), Position2D(10.0, 10.0))
        circle1.circleIntersect(circle2) shouldBe false
    }

    "Overlapping rectangle and circle should intersect" {
        val circle = Placed(Circle(radius = 5.0), Position2D(0.0, 0.0))
        val rectangle = Placed(Rectangle(width = 6.0, height = 6.0), Position2D(-3.0, -3.0))
        circle.circleRectIntersect(rectangle) shouldBe true
    }

    "Overlapping rectangles should intersect" {
        val rectangle1 = Placed(Rectangle(width = 5.0, height = 5.0), Position2D(0.0, 5.0))
        val rectangle2 = Placed(Rectangle(width = 6.0, height = 6.0), Position2D(3.0, 7.0))
        rectangle1.rectIntersect(rectangle2) shouldBe true
    }

    "Circle fully inside larger circle should be contained and not collide" {
        val inner = Placed(Circle(radius = 2.0), Position2D(0.0, 0.0))
        val outer = Circle(radius = 5.0)

        inner.isFullyContainedIn(outer) shouldBe true
        (inner collidesWith outer) shouldBe false
    }

    "Circle partially outside larger circle should not be contained and should collide" {
        val inner = Placed(Circle(radius = 2.0), Position2D(4.0, 0.0))
        val outer = Circle(radius = 5.0)

        inner.isFullyContainedIn(outer) shouldBe false
        (inner collidesWith outer) shouldBe true
    }

    "Rectangle fully inside larger rectangle should be contained and not collide" {
        val inner = Placed(Rectangle(width = 4.0, height = 4.0), Position2D(0.0, 0.0))
        val outer = Rectangle(width = 10.0, height = 10.0)

        inner.isFullyContainedIn(outer) shouldBe true
        (inner collidesWith outer) shouldBe false
    }

    "Rectangle partially outside larger rectangle should not be contained and should collide" {
        val inner = Placed(Rectangle(width = 4.0, height = 4.0), Position2D(4.0, 4.0))
        val outer = Rectangle(width = 10.0, height = 10.0)

        inner.isFullyContainedIn(outer) shouldBe false
        (inner collidesWith outer) shouldBe true
    }

    "Rectangle corner just touching edge should not be fully contained and should collide" {
        val inner = Placed(Rectangle(width = 2.0, height = 2.0), Position2D(4.0, 4.0))
        val outer = Rectangle(width = 8.0, height = 8.0)

        inner.isFullyContainedIn(outer) shouldBe false
        (inner collidesWith outer) shouldBe true
    }

    "Circle just touching the inside edge of a rectangle should be considered contained" {
        val inner = Placed(Circle(radius = 2.0), Position2D(0.0, 0.0))
        val outer = Rectangle(width = 4.0, height = 4.0)

        inner.isFullyContainedIn(outer) shouldBe true
        (inner collidesWith outer) shouldBe false
    }

    "Circle whose tip exceeds rectangle should not be contained and should collide" {
        val inner = Placed(Circle(radius = 3.0), Position2D(0.0, 0.0))
        val outer = Rectangle(width = 4.0, height = 4.0)

        inner.isFullyContainedIn(outer) shouldBe false
        (inner collidesWith outer) shouldBe true
    }
})

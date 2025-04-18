package io.github.evasim.model

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ShapesTest : FreeSpec({

    "Point inside circle should be correctly identified" {
        val center = Position2D(0.0, 0.0)
        val circle = Circle(center, radius = 5.0)
        (center in circle) shouldBe true
        (Position2D(3.0, 4.0) in circle) shouldBe true
        (Position2D(5.0, 0.0) in circle) shouldBe true
        (Position2D(6.0, 0.0) in circle) shouldBe false
        (Position2D(4.0, 4.0) in circle) shouldBe false
    }

    "Rectangle should contain points correctly" {
        val rectangle = Rectangle(Position2D(0.0, 0.0), width = 5.0, height = 5.0)
        (Position2D(1.0, 1.0) in rectangle) shouldBe true
        (Position2D(5.0, 5.0) in rectangle) shouldBe true
        (Position2D(6.0, 6.0) in rectangle) shouldBe false
        (Position2D(-1.0, -1.0) in rectangle) shouldBe false
    }

    "Overlapping circles should intersect" {
        val circle1 = Circle(Position2D(0.0, 0.0), radius = 5.0)
        val circle2 = Circle(Position2D(3.0, 4.0), radius = 5.0)
        circle1.intersect(circle2) shouldBe true
    }

    "Adjacent circles should intersect" {
        val circle1 = Circle(Position2D(0.0, 0.0), radius = 5.0)
        val circle2 = Circle(Position2D(10.0, 0.0), radius = 5.0)
        circle1.intersect(circle2) shouldBe true
    }

    "Non-overlapping circles should not intersect" {
        val circle1 = Circle(Position2D(0.0, 0.0), radius = 5.0)
        val circle2 = Circle(Position2D(10.0, 10.0), radius = 5.0)
        circle1.intersect(circle2) shouldBe false
    }

    "Overlapping rectangle and circle should intersect" {
        val circle = Circle(Position2D(0.0, 0.0), radius = 5.0)
        val rectangle = Rectangle(topLeft = Position2D(-3.0, -3.0), width = 6.0, height = 6.0)
        rectangle.intersect(circle) shouldBe true
        circle.intersect(rectangle) shouldBe true
    }

    "Overlapping rectangles should intersect" {
        val rectangle1 = Rectangle(topLeft = Position2D(0.0, 5.0), width = 5.0, height = 5.0)
        val rectangle2 = Rectangle(topLeft = Position2D(3.0, 7.0), width = 6.0, height = 6.0)
        rectangle1.intersect(rectangle2) shouldBe true
    }
})

package io.github.evasim.controller

import com.google.common.eventbus.EventBus
import io.github.evasim.model.Blob
import io.github.evasim.model.Food
import io.github.evasim.model.World
import java.util.concurrent.CopyOnWriteArraySet

/** A notable event in the simulation to be published towards subscribers. */
sealed interface Event

/** A marker interface for event subscribers. */
interface EventSubscriber

/** An event publisher, posting events and allowing subscribers to register and unregister. */
interface EventPublisher {

    /** Publishes the given [event] to all registered subscribers. */
    fun post(event: Event)

    /** Registers the given [subscriber] to receive events. */
    fun register(subscriber: EventSubscriber)

    /** Unregisters the given [subscriber] from receiving events. */
    fun unregister(subscriber: EventSubscriber)
}

/** An [EventPublisher] leveraging Google Guava's [EventBus]. */
open class EventBusPublisher : EventPublisher {

    private val eventBus = EventBus()
    protected val subscribers = CopyOnWriteArraySet<EventSubscriber>()

    override fun post(event: Event) = eventBus.post(event)

    override fun register(subscriber: EventSubscriber) {
        if (subscribers.add(subscriber)) {
            eventBus.register(subscriber)
        }
    }

    override fun unregister(subscriber: EventSubscriber) {
        if (subscribers.remove(subscriber)) {
            eventBus.unregister(subscriber)
        }
    }
}

/** A [blob] update event. */
data class UpdatedBlob(val blob: Blob) : Event

/** A [food] update event. */
data class UpdatedFood(val food: Food) : Event

/** A [world] update event. */
data class UpdatedWorld(val world: World) : Event

package io.github.evasim.controller

import com.google.common.eventbus.EventBus
import io.github.evasim.model.Entity

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

    override fun post(event: Event) = eventBus.post(event)

    override fun register(subscriber: EventSubscriber) = eventBus.register(subscriber)

    override fun unregister(subscriber: EventSubscriber) = eventBus.unregister(subscriber)
}

/** An [entity] update event. */
data class UpdatedEntity<E : Entity>(val entity: E) : Event

/** A factory method to create an [UpdatedEntity] from an [Entity]. */
fun <E : Entity> E.updated(): UpdatedEntity<E> = UpdatedEntity(this)

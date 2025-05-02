package io.github.evasim.model

/** The blob's personality. */
sealed interface Personality

/** Hawk personality, which is aggressive. */
data object Hawk : Personality

/** Dove personality, which is passive. */
data object Dove : Personality

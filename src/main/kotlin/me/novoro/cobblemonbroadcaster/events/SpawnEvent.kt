package me.novoro.cobblemonbroadcaster.events

import me.novoro.cobblemonbroadcaster.config.Configuration
import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.pokemon.aspect.AspectProvider
import me.novoro.cobblemonbroadcaster.util.LangManager
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

class SpawnEvent(private val config: Configuration) {

    init {
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(priority = Priority.LOWEST) { event ->
            //val aspects = AspectProvider.providers.flatMap { it.provide(pokemonEntity.pokemon) }
            //val labels = pokemonEntity.pokemon.species.labels

            val pokemonEntity = event.entity
            val aspectsAndLabels = mutableSetOf<String>()
            aspectsAndLabels.addAll(AspectProvider.providers.flatMap {it.provide(pokemonEntity.pokemon)})
            aspectsAndLabels.addAll(pokemonEntity.pokemon.species.labels)


            // Blacklist Stuff
            val world = event.entity.world as? ServerWorld
            val worldName = world?.registryKey?.value.toString()
            if (me.novoro.cobblemonbroadcaster.util.BlacklistedWorlds.isBlacklisted(worldName)) {
                return@subscribe
            }

            // Debugging: Log all aspects of the Pokémon
            //SimpleLogger.debug("Pokemon ${pokemonEntity.pokemon.species.name} has aspects: $aspects")

            // Dynamically check user-defined aspects
            config.keys.forEach { customCategory ->
                if (customCategory !in setOf("shiny", "legendary", "mythical", "ultrabeast")) {
                    if (customCategory in aspectsAndLabels) {
                        if (handleCategory(pokemonEntity, event.spawnablePosition.spawner.name, customCategory) { true }) return@subscribe
                    }
                }
            }

            // Default categories
            for (category in aspectsAndLabels) {
                if (handleCategory(pokemonEntity, event.spawnablePosition.spawner.name, category) {
                    true
                }) {
                    return@subscribe
                }
            }
        }
    }

    private fun handleCategory(
        pokemonEntity: com.cobblemon.mod.common.entity.pokemon.PokemonEntity,
        spawnerName: String,
        category: String,
        condition: () -> Boolean
    ): Boolean {
        if (!condition()) {
            return false
        }

        val isEnabled = config.getBoolean("$category.enabled", true)
        if (!isEnabled) return false

        val langKey = "$category.SpawnMessage"
        val replacements = mapOf(
            "pokemon" to pokemonEntity.pokemon.species.name,
            "player" to spawnerName
        )

        // Send the message to all players
        pokemonEntity.server?.playerManager?.playerList?.forEach { player ->
            LangManager.send(player as ServerPlayerEntity, langKey, replacements)
        }

        return true
    }
}

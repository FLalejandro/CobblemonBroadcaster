package me.novoro.cobblemonbroadcaster.events

import me.novoro.cobblemonbroadcaster.config.Configuration
import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.pokemon.aspect.AspectProvider
import me.novoro.cobblemonbroadcaster.util.LangManager
import me.novoro.cobblemonbroadcaster.util.LabelHelper
import me.novoro.cobblemonbroadcaster.util.SimpleLogger
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos;

class SpawnEvent(private val config: Configuration) {

    //TODO Add Gender Placeholders ♂, ♀, ⚲
    //TODO Add Multiple Spec-Support (Shiny Legendary, Legendary Galarian, etc.)
    //TODO Make Labels and Aspects work together

    init {
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(priority = Priority.LOWEST) { event ->

            // Aspect and Label
            val pokemonEntity = event.entity
            val aspectsAndLabels = mutableSetOf<String>()

            // Aspects (Gender, Shiny, etc.)
            aspectsAndLabels.addAll(
                AspectProvider.providers.flatMap { it.provide(pokemonEntity.pokemon) }
            )

            // Labels (Legendary, Mythical, Paradox, Kantonian, Baby, etc.)
            aspectsAndLabels.addAll(
                LabelHelper.filterValidLabels(pokemonEntity.pokemon.species.labels)
            )

            // Spawner + Spawner Name (WTF IS A POKESNACK!!!)
            val spawnerType = event.spawnablePosition.spawner
            val spawnerName = spawnerType.name
            val pos = event.spawnablePosition.position
            val isSnack = spawnerName.startsWith("poke_snack")

            // Blacklist Stuff
            val world = event.entity.world as? ServerWorld
            val worldName = world?.registryKey?.value.toString()
            if (me.novoro.cobblemonbroadcaster.util.BlacklistedWorlds.isBlacklisted(worldName)) {
                return@subscribe
            }

            // Priority
            if (handleCategory(pokemonEntity, event.spawnablePosition.spawner.name, "shiny", pos, isSnack) { pokemonEntity.pokemon.shiny }) return@subscribe

            // Debugging: Log all aspects of the Pokémon
            SimpleLogger.debug("Pokemon ${pokemonEntity.pokemon.species.name} has aspects: $aspectsAndLabels")

            // Dynamically check user-defined aspects
            config.keys.forEach { customCategory ->
                if (customCategory !in setOf("shiny", "legendary", "mythical", "ultrabeast")) {
                    if (customCategory in aspectsAndLabels) {
                        if (handleCategory(pokemonEntity, event.spawnablePosition.spawner.name, customCategory, pos, isSnack) { true }) return@subscribe
                    }
                }
            }

            for (category in aspectsAndLabels) {
                if (handleCategory(pokemonEntity, event.spawnablePosition.spawner.name, category, pos, isSnack) {
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
        spawnPos: BlockPos,
        isSnack: Boolean,
        condition: () -> Boolean
    ): Boolean {

        if (!condition()) return false

        val isEnabled = config.getBoolean("$category.enabled", true)
        if (!isEnabled) return false

        val langKey = if (isSnack) "$category.SpawnMessage-Snack"
        else "$category.SpawnMessage"

        val replacements = mapOf(
            "pokemon" to pokemonEntity.pokemon.species.name,
            "player" to spawnerName,
            "x" to spawnPos.x.toString(),
            "y" to spawnPos.y.toString(),
            "z" to spawnPos.z.toString()
        )

        // Send the message to all players
        pokemonEntity.server?.playerManager?.playerList?.forEach { player ->
            LangManager.send(player as ServerPlayerEntity, langKey, replacements)
        }

        return true
    }

}

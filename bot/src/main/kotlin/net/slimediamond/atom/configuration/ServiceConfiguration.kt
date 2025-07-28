package net.slimediamond.atom.configuration

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
class ServiceConfiguration {

    @Setting
    @Comment("A list of services which are disabled")
    var disabled: List<String> = emptyList()

}
package net.slimediamond.atom.configuration

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
class CommandConfiguration {

    @Setting
    var prefix: String = "!a "

    @Setting
    @Comment("The amount of threads given to the worker pool for executing commands")
    var workerPoolSize = 4

}
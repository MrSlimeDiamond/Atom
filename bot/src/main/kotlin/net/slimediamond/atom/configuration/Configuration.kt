package net.slimediamond.atom.configuration

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
class Configuration {

    @Setting("command")
    val commandConfiguration = CommandConfiguration()

    @Setting("irc")
    val ircConfiguration = IrcConfiguration()

    @Setting("storage")
    val storageConfiguration = StorageConfiguration()

}
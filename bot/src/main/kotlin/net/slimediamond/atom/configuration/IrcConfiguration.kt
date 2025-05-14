package net.slimediamond.atom.configuration

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
class IrcConfiguration {

    @Setting("server")
    val serverConfiguration = ServerConfiguration()
    @Setting("user")
    val userConfiguration = UserConfiguration()

    // TODO multiple servers
    @ConfigSerializable
    class ServerConfiguration {

        @Setting
        var name: String? = null
        @Setting
        var hostname: String? = null
        @Setting
        var port: Int? = null
        @Setting
        var ssl: Boolean = false

    }

    @ConfigSerializable
    class UserConfiguration {

        @Setting
        var nickname: String? = "Atom"
        @Setting
        var username: String? = "atom"
        @Setting
        var realname: String? = "SlimeDiamond's bot"

    }

}
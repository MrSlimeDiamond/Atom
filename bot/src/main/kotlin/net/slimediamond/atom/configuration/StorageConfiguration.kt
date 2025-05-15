package net.slimediamond.atom.configuration

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
class StorageConfiguration {

    @Setting("jdbc")
    var jdbcString: String = ""

}
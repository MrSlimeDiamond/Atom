package net.slimediamond.atom.data

import net.slimediamond.atom.Atom
import net.slimediamond.atom.irc.api.Server
import net.slimediamond.data.Key
import net.slimediamond.data.identification.ResourceKey
import net.slimediamond.data.value.Value

class Keys {

    val SERVER: Key<Value<Server>> = Key.from(ResourceKey.of(Atom.instance, "server"), Server::class.java)

}
package net.slimediamond.atom.api.irc

class UserTracker {

    private val realNames = HashMap<String, String>()

    fun getRealName(nickname: String): String? {
        return this.realNames[nickname]
    }

    fun put(nickname: String, realName: String) {
        this.realNames[nickname] = realName;
    }

}
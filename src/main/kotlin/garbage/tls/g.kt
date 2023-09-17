package garbage.tls

class G {

    companion object {

        const val NAME = "garbage-tls"
        val VERSION: String by lazy {
            G::class.java.getResourceAsStream("/garbage/tls/version.json").use {
                it.bufferedReader().use {
                    var result: String? = null
                    for (line in it.lineSequence()) {
                        val key = "\"version\":"
                        val line = line.trim()
                        if (line.startsWith(key)) {
                            result = line.substring(key.length).replace(Regex("[\",\\s]|\\+.*"), String())
                            break
                        }
                    }
                    result!!
                }
            }
        }
        val TAG: String by lazy { "$NAME::$VERSION" }

        var DEBUG = false

    }

}

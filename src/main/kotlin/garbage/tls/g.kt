/*
==--==--==--==--==--==--==--==--==--==--==--==--==--==--==--==--

garbage-tls

Copyright (C) 2023  Anonymous



This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

::--::--::--::--::--::--::--::--::--::--::--::--::--::--::--::--
*/

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

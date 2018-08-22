package de.smartsquare.kickpi.nearby

interface IDGenerator {

    /**
     * @return a id which is unique for the host device.
     */
    fun generate(): String

}
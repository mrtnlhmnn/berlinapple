package de.mrtnlhmnn.berlinapple.data

enum class MovieCategory(val categoryName: String) {
    Wettbewerb("Wettbewerb"),
    Encounters("Encounters"),
    BerlinaleTalents("Berlinale Talents"),
    BerlinaleShorts("Berlinale Shorts"),
    BerlinaleSeries("Berlinale Series"),
    PanoramaDokumente("Panorama Dokumente"),
    Panorama("Panorama"),
    OnTransmission("On Transmission"),
    ForumExpanded("Forum Expanded"),
    ForumExpandedAusstellung("Forum Expanded Ausstellung"),
    Forum50("Forum 50"),
    Forum("Forum"),
    BerlinaleOpenHouse("Berlinale Open House"),
    BerlinaleOpenHouseWithTypo("BerlinaleOpen House"),  // Typo, grrr
    BerlinaleClassicsEvent("Berlinale Classics Event"),
    BerlinaleClassics("Berlinale Classics"),
    Generation14plus("Generation 14plus"),
    GenerationKplus("Generation Kplus"),
    Retrospektive("Retrospektive"),
    RetrospektiveEvent("Retrospektive Event"),
    BerlinaleSpecialGala("Berlinale Special Gala"),
    BerlinaleSpecial("Berlinale Special"),
    BerlinaleSpecialWithTypo("BerlinaleSpecial"),
    PerspektiveDeutschesKino("Perspektive Deutsches Kino"),
    BerlinaleGoesKiez("Berlinale Goes Kiez");

    override fun toString(): String = categoryName

    companion object {
        val DELIMITER = "\n"

        fun findCategory(description: String): MovieCategory? {
            enumValues<MovieCategory>().forEach { cat ->
                // try to find any of the category strings as shown above in the description
                // as unfortunately it is not provided otherwise in the ics files ...
                if (description.contains(DELIMITER + cat.toString() + DELIMITER, false)) return cat
            }
            return null
        }
    }
}

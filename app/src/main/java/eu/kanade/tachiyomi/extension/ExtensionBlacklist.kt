package eu.kanade.tachiyomi.extension

val strictNsfwKeywords = listOf(
    "hentai", "porn", "doujin", "xxx", "smut", "adult", "pururin", "hitomi", 
    "luscious", "8muses", "multporn", "fap", "hbrowse", "tsumino", "asmhentai",
    "e-hentai", "exhentai", "simply-hentai", "hentaifox", "hentaihere", "nhentai",
    "hentairead", "hentai2read", "hentaihand", "fakku", "hanime", "hentaihaven",
    "hentaiplay", "hentaiclub"
)

fun isStrictNsfw(name: String, pkgName: String): Boolean {
    val lowerName = name.lowercase()
    val lowerPkg = pkgName.lowercase()
    return strictNsfwKeywords.any { it in lowerName || it in lowerPkg }
}

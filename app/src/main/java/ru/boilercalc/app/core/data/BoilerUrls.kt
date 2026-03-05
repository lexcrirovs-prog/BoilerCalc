package ru.boilercalc.app.core.data

object BoilerUrls {
    const val BASE_URL = "https://kotelpremium.ru"

    val steamUrls: Map<String, Map<String, String>> = mapOf(
        "s500" to mapOf("8 бар" to "/katalog/parovye-kotly/premium-s-500-8-bar", "12 бар" to "/katalog/parovye-kotly/premium-s-500-12-bar"),
        "s1000" to mapOf("8 бар" to "/katalog/parovye-kotly/premium-s-1000-8-bar", "12 бар" to "/katalog/parovye-kotly/premium-s-1000-12-bar"),
        "s1500" to mapOf("8 бар" to "/katalog/parovye-kotly/premium-s-1500-8-bar", "12 бар" to "/katalog/parovye-kotly/premium-s-1500-12-bar"),
        "s2000" to mapOf("8 бар" to "/katalog/parovye-kotly/premium-s-2000-8-bar", "12 бар" to "/katalog/parovye-kotly/premium-s-2000-12-bar"),
        "s2500" to mapOf("8 бар" to "/katalog/parovye-kotly/premium-s-2500-8-bar", "12 бар" to "/katalog/parovye-kotly/premium-s-2500-12-bar"),
        "s3000" to mapOf("8 бар" to "/katalog/parovye-kotly/premium-s-3000-8-bar", "12 бар" to "/katalog/parovye-kotly/premium-s-3000-12-bar"),
        "s3500" to mapOf("8 бар" to "/katalog/parovye-kotly/premium-s-3500-8-bar", "12 бар" to "/katalog/parovye-kotly/premium-s-3500-12-bar"),
        "s4000" to mapOf("8 бар" to "/katalog/parovye-kotly/premium-s-4000-8-bar", "12 бар" to "/katalog/parovye-kotly/premium-s-4000-12-bar"),
        "s5000" to mapOf("8 бар" to "/katalog/parovye-kotly/premium-s-5000-8-bar", "12 бар" to "/katalog/parovye-kotly/premium-s-5000-12-bar")
    )

    val waterUrls: Map<String, String> = mapOf(
        "c-250" to "/katalog/vodogrejnye-kotly/premium-c/c-250",
        "c-500" to "/katalog/vodogrejnye-kotly/premium-c/c-500",
        "c-750" to "/katalog/vodogrejnye-kotly/premium-c/c-750",
        "c-1000" to "/katalog/vodogrejnye-kotly/premium-c/c-1000",
        "c-1250" to "/katalog/vodogrejnye-kotly/premium-c/c-1250",
        "c-1500" to "/katalog/vodogrejnye-kotly/premium-c/c-1500",
        "c-1750" to "/katalog/vodogrejnye-kotly/premium-c/c-1750",
        "c-2000" to "/katalog/vodogrejnye-kotly/premium-c/c-2000",
        "c-2500" to "/katalog/vodogrejnye-kotly/premium-c/c-2500",
        "c-3000" to "/katalog/vodogrejnye-kotly/premium-c/c-3000",
        "c-3500" to "/katalog/vodogrejnye-kotly/premium-c/c-3500",
        "c-4000" to "/katalog/vodogrejnye-kotly/premium-c/c-4000",
        "c-5000" to "/katalog/vodogrejnye-kotly/premium-c/c-5000",
        "c-6000" to "/katalog/vodogrejnye-kotly/premium-c/c-6000",
        "e-2500" to "/katalog/vodogrejnye-kotly/premium-e/e-2500",
        "e-3500" to "/katalog/vodogrejnye-kotly/premium-e/e-3500",
        "e-4000" to "/katalog/vodogrejnye-kotly/premium-e/e-4000",
        "e-5000" to "/katalog/vodogrejnye-kotly/premium-e/e-5000",
        "e-6000" to "/katalog/vodogrejnye-kotly/premium-e/e-6000",
        "e-7000" to "/katalog/vodogrejnye-kotly/premium-e/e-7000"
    )
}

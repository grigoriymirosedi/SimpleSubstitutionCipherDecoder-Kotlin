class SimpleSubstitutionDescriptor {

    private val letterFrequencies: Map<Char, Double> = Utils.loadLetterFrequencies()
    private val bigramFrequencies: Map<String, Double> = Utils.loadBigramFrequencies()
    private val trigramFrequencies: Map<String, Double> = Utils.loadTrigramFrequencies()
    private val tetragramFrequencies: Map<String, Double> = Utils.loadTetragramFrequencies()
    private val conjunctionFrequencies: Map<String, Double> = Utils.loadConjunctionFrequencies()
    private val pronounFrequencies: Map<String, Double> = Utils.loadPronounFrequencies()
    private val adverbFrequencies: Map<String, Double> = Utils.loadAdverbFrequencies()
    private val suffixFrequencies: Map<String, Double> = Utils.loadSuffixFrequencies()

    fun decryptUsingStochasticOptimization(ciphertext: String, letterFrequencies: Map<Char, Double> = this.letterFrequencies, bigramFrequencies: Map<String, Double> = this.bigramFrequencies, trigramFrequencies: Map<String, Double> = this.trigramFrequencies, tetragramFrequencies: Map<String, Double> = this.tetragramFrequencies, conjunctionFrequencies: Map<String, Double> = this.conjunctionFrequencies, pronounFrequencies: Map<String, Double> = this.pronounFrequencies, adverbFrequencies: Map<String, Double> = this.adverbFrequencies, suffixFrequencies: Map<String, Double> = this.suffixFrequencies): String {
        val alphabet = "абвгдежзийклмнопрстуфхцчшщьыъэюя"
        var bestKey = alphabet.toList().shuffled()
        var bestDecryption = decryptWithKey(ciphertext, bestKey)
        var bestScore = calculateTextFitness(bestDecryption, letterFrequencies, bigramFrequencies, trigramFrequencies, tetragramFrequencies, conjunctionFrequencies, pronounFrequencies, adverbFrequencies, suffixFrequencies)

        repeat(1000) { // Количество итераций алгоритма
            val mutations = List(100) { bestKey.mutate() } // Генерируем 10 мутаций
            val candidates = mutations.map { it to decryptWithKey(ciphertext, it) }
            val evaluated = candidates.map { (key, text) -> key to calculateTextFitness(text, letterFrequencies, bigramFrequencies, trigramFrequencies, tetragramFrequencies, conjunctionFrequencies, pronounFrequencies, adverbFrequencies, suffixFrequencies) }

            val (newKey, newScore) = evaluated.maxByOrNull { it.second } ?: bestKey to bestScore

            if (newScore > bestScore) {
                bestKey = newKey
                bestDecryption = decryptWithKey(ciphertext, bestKey)
                bestScore = newScore
            }
        }

        return bestDecryption
    }

    private fun calculateTextFitness(
        text: String,
        letterFrequencies: Map<Char, Double>,
        bigramFrequencies: Map<String, Double>,
        trigramFrequencies: Map<String, Double>,
        tetragramFrequencies: Map<String, Double>,
        conjunctionFrequencies: Map<String, Double>,
        pronounFrequencies: Map<String, Double>,
        adverbFrequencies: Map<String, Double>,
        suffixFrequencies: Map<String, Double>
    ): Double {
        var fitnessScore = 0.0

        // Оценка по частотам букв
        fitnessScore += text.filter { it in letterFrequencies }
            .sumOf { char -> letterFrequencies[char] ?: -1.0 }

        // Оценка по биграммам
        val bigrams = text.windowed(2)
        for (bigram in bigrams) {
            if (bigram.length == 2) {
                val bigramFrequency = bigramFrequencies[bigram] ?: 0.0
                fitnessScore += bigramFrequency
            }
        }

        // Оценка по триграммам
        val trigrams = text.windowed(3)
        for (trigram in trigrams) {
            if (trigram.length == 3) {
                val trigramFrequency = trigramFrequencies[trigram] ?: 0.0
                fitnessScore += trigramFrequency
            }
        }

        // Оценка по тетрограмам
        val tetragrams = text.windowed(4)
        for (tetragram in tetragrams) {
            if (tetragram.length == 4) {
                val tetragramFrequency = tetragramFrequencies[tetragram] ?: 0.0
                fitnessScore += tetragramFrequency
            }
        }

        // Оценка по окончанию слов
        val words = text.split(" ")
        for (word in words) {
            for (suffix in suffixFrequencies.keys) {
                if (word.endsWith(suffix)) {
                    val suffixFrequency = suffixFrequencies[suffix] ?: 0.0
                    fitnessScore += suffixFrequency
                }
            }

            // Оценка по союзам
            val conjunctionFrequency = conjunctionFrequencies[word] ?: 0.0
            fitnessScore += conjunctionFrequency

            // Оценка по местоимениям
            val pronounFrequency = pronounFrequencies[word] ?: 0.0
            fitnessScore += pronounFrequency

            // Оценка по наречиям
            val adverbFrequency = adverbFrequencies[word] ?: 0.0
            fitnessScore += adverbFrequency
        }

        return fitnessScore
    }


    private fun decryptWithKey(ciphertext: String, key: List<Char>): String {
        val alphabet = "абвгдежзийклмнопрстуфхцчшщьыъэюя"
        val decryptionMap = key.withIndex().associate { alphabet[it.index] to it.value }
        return ciphertext.map { char ->
            if (char in alphabet) decryptionMap[char] ?: char else char
        }.joinToString("")
    }
}